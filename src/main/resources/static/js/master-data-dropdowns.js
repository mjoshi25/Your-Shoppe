/* Admin-managed cascading dropdowns. Child options are loaded only after a parent is selected. */
(function(){
 async function load(select, preserve){
  const group=select.dataset.masterGroup; if(!group)return;
  const parent=select.dataset.parentSelect?document.querySelector(select.dataset.parentSelect):null;
  const wanted=preserve?(select.dataset.selected||select.value||''):'';
  const placeholder=select.dataset.placeholder||'Choose an option';
  select.disabled=true; select.replaceChildren(new Option(placeholder,''));
  if(parent && !parent.value){select.disabled=true;return;}
  const url='/api/master-data/'+encodeURIComponent(group)+(parent?'?parent='+encodeURIComponent(parent.value):'');
  try{
   const response=await fetch(url,{headers:{'Accept':'application/json'}}); if(!response.ok)throw new Error('Load failed');
   const items=await response.json();
   items.forEach(item=>{const opt=new Option(item.label,item.code); if(wanted && (wanted===item.code||wanted===item.label))opt.selected=true;select.add(opt);});
   select.disabled=false; select.dataset.loaded='true'; if(select.value) select.dispatchEvent(new Event('change',{bubbles:true}));
  }catch(e){select.add(new Option('Options unavailable — contact administrator',''));select.disabled=false;}
 }
 document.addEventListener('DOMContentLoaded',()=>{
  const selects=[...document.querySelectorAll('select[data-master-group]')];
  selects.forEach(s=>{const parent=s.dataset.parentSelect?document.querySelector(s.dataset.parentSelect):null;if(!parent||parent.value)load(s,true);});
  selects.forEach(s=>s.addEventListener('change',()=>{
   selects.filter(child=>child.dataset.parentSelect==='#'+s.id).forEach(child=>{child.dataset.selected='';child.value='';load(child,false);});
  }));
 });
})();
