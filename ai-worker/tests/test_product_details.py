import base64
from io import BytesIO
import json
import urllib.parse
import pytest
from PIL import Image
import shop


def product_page(name='63-929 면 원단', material='면100%', width='110cm', ident='1'):
    data={'@type':'Product','sku':ident,'url':f'https://fashionstart.net/goods/goods_view.php?goodsNo={ident}',
          'name':name,'material':material,'image':['https://img.kohasid.com/photos/goods/test/photo.gif']}
    return '<script type="application/ld+json">'+json.dumps(data)+'</script>'+f'<table><tr><th>원단폭</th><td>{width}</td></tr></table>'


def test_enrichment_uses_source_specs_and_never_purchase_price(monkeypatch):
    pages=iter(['<a href="/goods/goods_view.php?goodsNo=1">63-929 면 원단</a>',product_page()])
    monkeypatch.setattr(shop,'download',lambda *_:next(pages))
    monkeypatch.setattr(shop,'product_photo',lambda *_:{'imageBase64':'photo','imageMimeType':'image/jpeg'})
    result=shop.enrich({'seller':'패션스타트','productName':'63-929 면 원단'})
    assert result['status']=='COMPLETE'
    assert result['materialComposition']=='면100%' and result['width']=='110cm'
    assert 'purchasePrice' not in result


def test_product_page_must_match_identity(monkeypatch):
    pages=iter(['<a href="/goods/goods_view.php?goodsNo=1">63-929 면</a>',product_page('다른 원단')])
    monkeypatch.setattr(shop,'download',lambda *_:next(pages))
    monkeypatch.setattr(shop,'product_photo',lambda *_:pytest.fail('mismatched page photo fetched'))
    assert shop.enrich({'seller':'패션스타트','productName':'63-929 면'})['reason']=='TITLE_MISMATCH'
    assert shop.read_product(product_page(ident='2'),'https://fashionstart.net/goods/goods_view.php?goodsNo=1',code='63-929') is None


def test_name_search_preserves_colour_and_size_and_rejects_ambiguity():
    page='<a href="/goods/goods_view.php?goodsNo=1"><strong class="item_name">73-929 체크 원단_네이비</strong><span>판매단위 1마</span></a>'
    assert shop.find_product(page,name='체크 원단_네이비').endswith('goodsNo=1')
    assert shop.find_product(page,name='체크 원단_블랙') is None
    assert shop.find_product(page,name='체크 원단') is None
    assert shop.find_product(page+page.replace('goodsNo=1','goodsNo=2'),name='체크 원단_네이비') is None


def test_code_free_name_search_reaches_product(monkeypatch):
    pages=iter(['<a href="/goods/goods_view.php?goodsNo=1"><strong class="item_name">63-929 소프트 면 원단</strong>판매단위 1마</a>',product_page('63-929 소프트 면 원단')])
    queries=[]
    def fetch(url):
        queries.append(url)
        return next(pages)
    monkeypatch.setattr(shop,'download',fetch)
    monkeypatch.setattr(shop,'product_photo',lambda *_:{'imageBase64':'photo','imageMimeType':'image/jpeg'})
    assert shop.enrich({'seller':'패션스타트','productName':'소프트 면 원단'})['status']=='COMPLETE'
    assert '소프트 면 원단' in urllib.parse.unquote_plus(queries[0])


def test_receipt_separated_size_or_colour_is_recombined_without_guessing():
    page='<a href="/goods/goods_view.php?goodsNo=1"><strong class="item_name">73-925 소프트 니트 도티 2종_(1/2EA)</strong></a>'
    assert shop.find_product(page,name='소프트 니트 도티 2종') is None
    assert shop.find_product(page,name='소프트 니트 도티 2종',size='1/2EA').endswith('goodsNo=1')
    assert shop.find_product(page,name='소프트 니트 도티 2종',size='1EA') is None
    assert shop.name_matches('73-929 글렌체크_네이비','글렌체크',color='네이비')
    assert not shop.name_matches('73-929 글렌체크_블랙','글렌체크',color='네이비')


@pytest.mark.parametrize('missing',['photo','material','width'])
def test_each_required_detail_missing_stays_incomplete(monkeypatch,missing):
    pages=iter(['<a href="/goods/goods_view.php?goodsNo=1">63-929 면</a>',product_page(material=None if missing=='material' else '면100%',width='' if missing=='width' else '110cm')])
    monkeypatch.setattr(shop,'download',lambda *_:next(pages))
    def photo(_):
        if missing=='photo':raise ValueError('missing')
        return {'imageBase64':'photo','imageMimeType':'image/jpeg'}
    monkeypatch.setattr(shop,'product_photo',photo)
    result=shop.enrich({'seller':'패션스타트','productName':'63-929 면'})
    assert result['status']=='INCOMPLETE' and result['reason']
    assert result['productUrl'].endswith('goodsNo=1')


@pytest.mark.parametrize('format',['GIF','PNG','JPEG'])
def test_product_photo_decodes_resizes_and_removes_metadata(monkeypatch,format):
    raw=BytesIO();Image.new('RGB',(1400,800),'green').save(raw,format=format)
    monkeypatch.setattr(shop,'fetch_bytes',lambda *a,**k:(raw.getvalue(),'utf-8'))
    result=shop.product_photo('https://img.kohasid.com/photos/goods/test/photo.gif')
    image=Image.open(BytesIO(base64.b64decode(result['imageBase64'])))
    assert image.format=='JPEG' and image.width==960 and max(image.size)<=960
    assert len(base64.b64decode(result['imageBase64']))<=524288


def test_photo_url_and_actual_content_are_checked(monkeypatch):
    for url in ['http://img.kohasid.com/photos/goods/a.jpg','https://127.0.0.1/a.jpg','https://img.kohasid.com/logo.jpg','https://img.kohasid.com.evil.test/photos/goods/a.jpg']:
        with pytest.raises(ValueError):shop.validate_url(url,image=True)
    with pytest.raises(ValueError):shop.SafeRedirect(True).redirect_request(None,None,302,'',{},'https://fashionstart.net/admin')
    monkeypatch.setattr(shop,'fetch_bytes',lambda *a,**k:(b'<svg>not a photo</svg>','utf-8'))
    with pytest.raises(Exception):shop.product_photo('https://img.kohasid.com/photos/goods/a.jpg')
