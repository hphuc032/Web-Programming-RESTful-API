(function(){
 const modal=new bootstrap.Modal(document.getElementById('categoryModal'));
 const esc=v=>$('<div>').text(v??'').html();
 const fileUrl=p=>p?'/uploads/'+p.split('/').map(encodeURIComponent).join('/'):'';
 function notify(message,type='success'){ $('#alertBox').html(`<div class="alert alert-${type} alert-dismissible fade show">${esc(message)}<button class="btn-close" data-bs-dismiss="alert"></button></div>`); }
 function error(xhr){ const r=xhr.responseJSON; let m=r?.message||'Không thể thực hiện yêu cầu'; if(r?.body&&typeof r.body==='object')m+=': '+Object.values(r.body).join(', '); notify(m,'danger'); }
 function load(){ $.getJSON('/api/category').done(r=>{ const rows=r.body.map(c=>`<tr><td>${c.categoryId}</td><td>${c.icon?`<img class="thumb" src="${fileUrl(c.icon)}" alt="">`:'<span class="thumb placeholder-thumb">No icon</span>'}</td><td class="fw-semibold">${esc(c.categoryName)}</td><td class="text-end"><button class="btn btn-sm btn-outline-primary edit" data-id="${c.categoryId}">Sửa</button> <button class="btn btn-sm btn-outline-danger delete" data-id="${c.categoryId}">Xóa</button></td></tr>`).join(''); $('#categoryRows').html(rows||'<tr><td colspan="4" class="text-center py-4 text-secondary">Chưa có danh mục</td></tr>'); }).fail(error); }
 $('#addCategory').on('click',()=>{ $('#categoryForm')[0].reset(); $('#categoryId').val(''); $('#categoryModalTitle').text('Thêm danh mục'); modal.show(); });
 $('#categoryRows').on('click','.edit',function(){ $.post('/api/category/getCategory',{id:$(this).data('id')}).done(r=>{ $('#categoryForm')[0].reset(); $('#categoryId').val(r.body.categoryId); $('#categoryName').val(r.body.categoryName); $('#categoryModalTitle').text('Cập nhật danh mục'); modal.show(); }).fail(error); });
 $('#categoryRows').on('click','.delete',function(){ const id=$(this).data('id'),name=$(this).closest('tr').find('td').eq(2).text(); if(!confirm(`Xóa danh mục "${name}"?`))return; $.ajax({url:'/api/category/deleteCategory?categoryId='+encodeURIComponent(id),method:'DELETE'}).done(r=>{notify(r.message);load();}).fail(error); });
 $('#categoryForm').on('submit',function(e){ e.preventDefault(); const id=$('#categoryId').val(),data=new FormData(this); $('#saveCategory').prop('disabled',true); $.ajax({url:id?'/api/category/updateCategory?categoryId='+encodeURIComponent(id):'/api/category/addCategory',method:id?'PUT':'POST',data,processData:false,contentType:false}).done(r=>{modal.hide();notify(r.message);load();}).fail(error).always(()=>$('#saveCategory').prop('disabled',false)); });
 load();
})();
