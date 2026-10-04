import base64
from http.server import ThreadingHTTPServer
import json
import threading
import urllib.error
import urllib.request
import pytest
import receipt
import shop
from server import Handler


def test_preserve_purchase_semantics_and_unreadable_row():
    result=receipt.normalize({'seller':'천가게','purchasedAt':'잘 모름','items':[
        {'productName':'면 원단','color':'흰색','quantity':'2마','amount':8000,'amountType':'UNIT_PRICE','amountText':'8,000원/마'},
        {'productName':None,'amount':True,'amountType':'bad'}]})
    assert result['purchasedAt'] is None
    assert result['items'][0]['amountType']=='UNIT_PRICE'
    assert result['items'][0]['quantity']=='2마'
    assert result['items'][1]['productName'] is None
    assert result['items'][1]['amount'] is None
    assert result['items'][1]['warning']


def test_empty_receipt_is_not_a_success():
    with pytest.raises(ValueError):receipt.normalize({'items':[]})


def test_seller_hint_only_fills_missing_seller():
    raw={'items':[{'productName':'면'}]}
    assert receipt.normalize(raw,'패션스타트')['seller']=='패션스타트'
    assert receipt.normalize(dict(raw,seller='천가게'),'패션스타트')['seller']=='천가게'


def test_disabled_seller_never_scrapes(monkeypatch):
    monkeypatch.setattr(shop,'download',lambda *_:pytest.fail('disabled seller fetched'))
    assert shop.enrich({'seller':'썬퀼트','productName':'63-929 면'})['status']=='SKIPPED'
    monkeypatch.setenv('ENABLED_ENRICHMENT_SELLERS','')
    assert shop.enrich({'seller':'패션스타트','productName':'63-929 면'})['status']=='SKIPPED'


def test_only_one_exact_code_candidate_on_seller():
    raw='''<a href="/goods/goods_view.php?goodsNo=1">63-929 면</a>
    <a href="https://evil.test/goods/goods_view.php?goodsNo=2">63-929 면</a>
    <a href="/goods/goods_view.php?goodsNo=3">63-9290 다른 원단</a>'''
    assert shop.find_product(raw,'63-929')=='https://fashionstart.net/goods/goods_view.php?goodsNo=1'
    raw+='<a href="/goods/goods_view.php?goodsNo=4">63-929 면</a>'
    assert shop.find_product(raw,'63-929') is None
    assert shop.find_product('<a href="/goods/goods_view.php?goodsNo=2">비슷한 이름</a>','63-929') is None


def test_enrichment_never_returns_purchase_price(monkeypatch):
    pages=iter(['<a href="/goods/goods_view.php?goodsNo=1">63-929 원단</a>','<title>63-929 원단</title><p>면100% 폭110cm</p>'])
    monkeypatch.setattr(shop,'download',lambda *_:next(pages))
    monkeypatch.setattr(shop,'generate',lambda *_:{'materialComposition':'면100%','width':'110cm','purchasePrice':999})
    result=shop.enrich({'seller':'패션스타트','productName':'63-929 원단'})
    assert result=={'status':'COMPLETE','productUrl':'https://fashionstart.net/goods/goods_view.php?goodsNo=1','materialComposition':'면100%','width':'110cm'}


def test_product_page_must_match_title(monkeypatch):
    pages=iter(['<a href="/goods/goods_view.php?goodsNo=1">63-929 면</a>','<title>다른상품</title><p>추천상품 63-929</p>'])
    monkeypatch.setattr(shop,'download',lambda *_:next(pages))
    monkeypatch.setattr(shop,'generate',lambda *_:pytest.fail('mismatched page sent to LLM'))
    assert shop.enrich({'seller':'패션스타트','productName':'63-929 면'})['reason']=='TITLE_MISMATCH'


def test_arbitrary_network_target_rejected():
    with pytest.raises(ValueError):shop.download('http://127.0.0.1/admin')
    with pytest.raises(ValueError):shop.download('https://fashionstart.net.evil.test/')
    with pytest.raises(ValueError):shop.SafeRedirect().redirect_request(None,None,302,'',{},'http://127.0.0.1/')


@pytest.fixture
def worker(monkeypatch):
    monkeypatch.setenv('FABRIC_WORKER_TOKEN','private-test-token')
    server=ThreadingHTTPServer(('127.0.0.1',0),Handler)
    thread=threading.Thread(target=server.serve_forever,daemon=True);thread.start()
    yield f'http://127.0.0.1:{server.server_port}'
    server.shutdown();server.server_close();thread.join()


def post(url,data,token='private-test-token'):
    req=urllib.request.Request(url,data=json.dumps(data).encode(),headers={'Authorization':'Bearer '+token,'Content-Type':'application/json'})
    with urllib.request.urlopen(req,timeout=5) as response:return json.load(response)


def test_http_contract_private_auth_and_image_roundtrip(worker,monkeypatch):
    image=b'private-receipt-image'
    def extract(blob,mime,seller):
        assert blob==image and mime=='image/png' and seller is None
        return {'seller':'패션스타트','items':[{'productName':'면 원단'}]}
    monkeypatch.setattr(receipt,'extract',extract)
    payload={'image':base64.b64encode(image).decode(),'mimeType':'image/png'}
    with pytest.raises(urllib.error.HTTPError) as e:post(worker+'/extract',payload,'wrong')
    assert e.value.code==401
    assert post(worker+'/extract',payload)['items'][0]['productName']=='면 원단'


def test_http_failure_does_not_leak_provider_error(worker,monkeypatch):
    def fail(*_):raise RuntimeError('secret-key-or-private-receipt')
    monkeypatch.setattr(receipt,'extract',fail)
    with pytest.raises(urllib.error.HTTPError) as e:post(worker+'/extract',{'image':'eA==','mimeType':'image/png'})
    assert e.value.code==502
    assert 'secret' not in e.value.read().decode()


def test_provider_schema_is_small_but_local_bounds_are_enforced():
    encoded=json.dumps(receipt.provider_schema(receipt.Receipt))
    assert 'maxItems' not in encoded and 'maximum' not in encoded
    with pytest.raises(ValueError):receipt.normalize({'items':[{'productName':'x'}]*101})
    result=receipt.normalize({'items':[{'productName':'x','amount':2147483648}]})
    assert result['items'][0]['amount'] is None
