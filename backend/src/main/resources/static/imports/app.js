'use strict';
const $=id=>document.getElementById(id);
let token=sessionStorage.getItem('sorosoroAccess'), refreshToken=sessionStorage.getItem('sorosoroRefresh');
let polling, busy=false, page=0, fabricCache=new Map(), editing=null;
function node(tag,text,cls){const n=document.createElement(tag);if(text!=null)n.textContent=text;if(cls)n.className=cls;return n;}
function tell(text){$('message').textContent=text;clearTimeout(tell.timer);tell.timer=setTimeout(()=>$('message').textContent='',6500);}
async function api(path,options={},renew=true){
 const headers=new Headers(options.headers||{});if(token)headers.set('Authorization','Bearer '+token);
 if(options.body&&!(options.body instanceof FormData))headers.set('Content-Type','application/json');
 const response=await fetch(path,{...options,headers});
 if(response.status===401&&refreshToken&&renew){
   const r=await fetch('/api/v1/auth/reissue',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({refreshToken})});
   if(r.ok){token=(await r.json()).accessToken;sessionStorage.setItem('sorosoroAccess',token);return api(path,options,false);}
 }
 if(response.status===401){logout();throw new Error('다시 로그인해주세요.');}
 if(!response.ok){let error;try{error=await response.json();}catch{}throw new Error(error?.message||'처리하지 못했습니다. 잠시 후 다시 시도해주세요.');}
 return response.status===204?null:response.json();
}
function signedIn(){ $('login').hidden=!!token;$('workspace').hidden=!token;$('logout').hidden=!token;if(token)load();}
function logout(){token=null;refreshToken=null;sessionStorage.removeItem('sorosoroAccess');sessionStorage.removeItem('sorosoroRefresh');clearTimeout(polling);fabricCache.clear();$('jobs').replaceChildren();$('fabrics').replaceChildren();signedIn();}
$('logout').onclick=async()=>{if(refreshToken)try{await api('/api/v1/auth/logout',{method:'POST',body:JSON.stringify({refreshToken})});}catch{}logout();};
async function init(){
 try{
  const config=await api('/api/v1/import-ui-config');
  $('demo').hidden=!config.demo;
  $('kakao').disabled=!config.kakaoClientId;
  if(!config.kakaoClientId)$('login-help').textContent=config.demo?'로컬 데모에서 가져오기 흐름을 확인할 수 있어요.':'카카오 로그인 설정이 필요합니다.';
  $('kakao').onclick=()=>{const state=crypto.randomUUID();sessionStorage.setItem('sorosoroOAuthState',state);
   location.assign('https://kauth.kakao.com/oauth/authorize?'+new URLSearchParams({client_id:config.kakaoClientId,redirect_uri:location.origin+'/imports/',response_type:'code',state}));};
  $('demo').onclick=async()=>{try{const data=await api('/api/v1/local-demo/login',{method:'POST'});token=data.accessToken;sessionStorage.setItem('sorosoroAccess',token);signedIn();}catch(e){tell(e.message);}};
  const q=new URLSearchParams(location.search);
  if(q.has('code')){
   const expected=sessionStorage.getItem('sorosoroOAuthState');sessionStorage.removeItem('sorosoroOAuthState');
   history.replaceState({},'',location.pathname);
   if(!expected||q.get('state')!==expected)throw new Error('로그인 요청을 다시 시작해주세요.');
   const data=await api('/api/v1/auth/kakao/login',{method:'POST',body:JSON.stringify({authorizationCode:q.get('code'),redirectUri:location.origin+'/imports/'})});
   token=data.accessToken;refreshToken=data.refreshToken;sessionStorage.setItem('sorosoroAccess',token);sessionStorage.setItem('sorosoroRefresh',refreshToken);
  }
  signedIn();
 }catch(e){tell(e.message);}
}
async function upload(files){
 if(busy)return;busy=true;$('images').disabled=true;
 try{
  let count=0;
  for(const file of files){
   if(!['image/png','image/jpeg','image/webp'].includes(file.type)||file.size>10*1024*1024){tell(file.name+': 10MB 이하의 이미지를 선택해주세요.');continue;}
   const form=new FormData();form.append('image',file);if($('seller').value)form.append('seller',$('seller').value);
   try{await api('/api/v1/fabric-imports',{method:'POST',body:form});count++;}catch(e){tell(file.name+': '+e.message);}
  }
  if(count)tell(count+'장의 주문 내역을 받았어요. 읽은 원단은 자동으로 저장해요.');
  await load();
 }finally{busy=false;$('images').disabled=false;$('images').value='';}
}
$('images').onchange=e=>upload([...e.target.files]);
$('dropzone').ondragover=e=>{e.preventDefault();$('dropzone').classList.add('dragging');};
$('dropzone').ondragleave=()=>$('dropzone').classList.remove('dragging');
$('dropzone').ondrop=e=>{e.preventDefault();$('dropzone').classList.remove('dragging');upload([...e.dataTransfer.files]);};
$('refresh').onclick=()=>load();
function button(text,fn,cls='quiet'){const b=node('button',text,cls);b.type='button';b.onclick=async()=>{b.disabled=true;try{await fn();}catch(e){tell(e.message);}finally{b.disabled=false;}};return b;}
async function load(){
 clearTimeout(polling);if(!token)return;
 try{
  const jobs=await api('/api/v1/fabric-imports');
  const details=await Promise.all(jobs.map(j=>api('/api/v1/fabric-imports/'+j.id)));
  await loadFabrics(true);
  $('jobs').replaceChildren();if(!details.length)$('jobs').append(node('p','첫 주문 내역을 올려보세요.','empty'));
  let pending=false;
  for(const job of details){renderJob(job);if(['QUEUED','PROCESSING'].includes(job.status)||job.items.some(i=>['PENDING','PROCESSING'].includes(i.enrichment_status)))pending=true;}
  if(pending)polling=setTimeout(load,2500);
 }catch(e){tell(e.message);}
}
function renderJob(job){
 const card=node('article',null,'job'),head=node('div',null,'job-header'),left=node('div'),actions=node('div',null,'job-actions');
 const saved=job.items.filter(i=>i.fabric_id).length,review=job.items.filter(i=>i.status==='NEEDS_REVIEW').length;
 const labels={QUEUED:'순서를 기다리고 있어요',PROCESSING:'주문 내역을 읽고 있어요',FAILED:'다시 확인이 필요해요',CANCELLED:'가져오기 취소됨',COMPLETED:`원단 ${saved}건 등록${review?' · '+review+'건 확인 필요':''}`};
 left.append(node('div',labels[job.status]||job.status,'title'),node('small',new Date(job.created_at).toLocaleString('ko-KR'),'muted'));
 if(job.image_available)actions.append(button('원본',async()=>{const r=await fetch(`/api/v1/fabric-imports/${job.id}/image`,{headers:{Authorization:'Bearer '+token}});if(!r.ok)throw new Error('원본을 가져올 수 없어요.');const url=URL.createObjectURL(await r.blob());window.open(url,'_blank','noopener');setTimeout(()=>URL.revokeObjectURL(url),60000);}));
 if(job.status==='FAILED')actions.append(button('다시 시도',async()=>{await api(`/api/v1/fabric-imports/${job.id}/retry`,{method:'POST'});await load();}));
 if(job.status!=='CANCELLED')actions.append(button('가져오기 취소',async()=>{if(!confirm('이번 가져오기에서 등록한 원단도 함께 삭제할까요?'))return;await api('/api/v1/fabric-imports/'+job.id,{method:'DELETE'});await load();},'quiet danger'));
 head.append(left,actions);card.append(head);
 if(job.error_message)card.append(node('p',job.error_message,'muted small'));
 for(const item of (job.status==='CANCELLED'?[]:job.items.filter(i=>i.status!=='DISMISSED'))){
  const row=node('div',null,'item-row'),text=node('div');const f=fabricCache.get(item.fabric_id);
  text.append(node('strong',f?.name||item.product_name||'상품명 확인 필요'));
  const color=f?.color??item.color,qty=f?.purchaseQuantity??item.quantity;
  text.append(node('small',[color,qty,item.amount_text].filter(Boolean).join(' · ')||'캡처의 구매 항목'));
  if(item.warning)text.append(node('small',item.warning));
  if(item.fabric_id)text.append(node('small',({PENDING:'상세 정보 확인 대기',PROCESSING:'상품 상세를 찾고 있어요',COMPLETE:'상품 상세를 연결했어요',SKIPPED:'캡처의 구매 정보로 기록했어요',FAILED:'구매 기록은 저장됐어요. 상세 정보는 찾지 못했어요'})[item.enrichment_status]));
  const actions=node('div',null,'job-actions');actions.append(button(item.fabric_id?'수정':'확인',()=>edit(item.fabric_id,job.id,item)),button('제외',async()=>{if(!confirm('이 항목만 보관함에서 제외할까요?'))return;await api(`/api/v1/fabric-imports/${job.id}/items/${item.id}`,{method:'DELETE'});await load();},'quiet danger'));row.append(text,actions);card.append(row);
 }
 $('jobs').append(card);
}
async function loadFabrics(reset){
 if(reset){page=0;fabricCache.clear();$('fabrics').replaceChildren();}
 const data=await api(`/api/v1/fabrics?page=${page}&size=20`);
 $('fabric-count').textContent=data.totalElements+'개의 기록';$('more').hidden=!data.hasNext;
 if(!data.totalElements)$('fabrics').append(node('p','구매한 원단이 이곳에 모여요.','empty'));
 for(const f of data.items){fabricCache.set(f.id,f);const card=node('article',null,'fabric-card'),row=node('div',null,'row');row.append(node('div',null,'swatch'),node('span',f.storeName||'내 원단','badge'));card.append(row,node('h3',f.name),node('p',[f.color,f.purchaseQuantity,f.purchasedAt,f.purchasePrice!=null?f.purchasePrice.toLocaleString('ko-KR')+'원':null].filter(Boolean).join(' · ')||'구매 정보 미입력'));
  if(f.materialComposition||f.width)card.append(node('p',[f.materialComposition,f.width].filter(Boolean).join(' / ')));
  const controls=node('div',null,'row');controls.append(button('기록 수정',()=>edit(f.id)),button('삭제',async()=>{if(!confirm('이 원단 기록을 삭제할까요?'))return;await api('/api/v1/fabrics/'+f.id,{method:'DELETE'});await load();},'quiet danger'));
  if(f.productUrl&&/^https?:\/\//.test(f.productUrl)){const a=node('a','상품 보기 ↗');a.href=f.productUrl;a.target='_blank';a.rel='noopener noreferrer';controls.append(a);}card.append(controls);$('fabrics').append(card);
 }
}
$('more').onclick=async()=>{page++;try{await loadFabrics(false);}catch(e){tell(e.message);page--;}};
const fields=[['name','내 원단 이름','text',150],['productName','쇼핑몰 상품명','text',200],['storeName','판매처','text',150],['purchasedAt','구매일','date'],['purchaseQuantity','구매 수량','text',100],['purchasePrice','이 원단의 총 구매금액 (원)','number'],['color','색상 / 옵션','text',100],['size','구매 규격','text',100],['productCode','품번','text',100],['width','원단폭','text',100],['materialComposition','소재 / 혼용률','text',5000],['productUrl','상품 URL','url',2000],['memo','메모','textarea',10000]];
async function edit(id,jobId,item){
 const f=id?await api('/api/v1/fabrics/'+id):{name:item.product_name,productName:item.product_name,productCode:item.product_code,storeName:item.seller,purchasedAt:item.purchased_at,purchasePrice:item.line_total,purchaseQuantity:item.quantity,color:item.color,size:item.size};
 editing={id,jobId,itemId:item?.id,original:f};$('fields').replaceChildren();
 for(const [key,label,type,max]of fields){const l=node('label',label,type==='textarea'?'wide':'');const input=node(type==='textarea'?'textarea':'input');input.name=key;if(type!=='textarea')input.type=type;if(max)input.maxLength=max;input.value=f[key]??'';if(key==='name')input.required=true;if(type==='number'){input.min='0';input.max='2147483647';input.step='1';}l.append(input);$('fields').append(l);}
 $('editor').showModal();
}
$('close-editor').onclick=()=>$('editor').close();
$('edit-form').onsubmit=async e=>{e.preventDefault();const submit=e.submitter;submit.disabled=true;
 try{const values=Object.fromEntries(new FormData(e.target));for(const k of Object.keys(values))values[k]=values[k].trim()||null;if(values.purchasePrice!=null)values.purchasePrice=Number(values.purchasePrice);values.rating=editing.original.rating??null;values.repurchaseIntention=editing.original.repurchaseIntention??'UNKNOWN';
 const path=editing.id?'/api/v1/fabrics/'+editing.id:`/api/v1/fabric-imports/${editing.jobId}/items/${editing.itemId}/register`;
 await api(path,{method:editing.id?'PUT':'POST',body:JSON.stringify(values)});$('editor').close();tell('원단 기록을 저장했어요.');await load();
 }catch(err){tell(err.message);}finally{submit.disabled=false;}};
init();
