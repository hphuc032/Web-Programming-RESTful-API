(function () {
    const modal = new bootstrap.Modal(document.getElementById('categoryModal'));
    const esc = value => $('<div>').text(value ?? '').html();
    const fileUrl = path => path ? '/uploads/' + path.split('/').map(encodeURIComponent).join('/') : '';

    function notify(message, type = 'success') {
        $('#alertBox').html(`<div class="alert alert-${type} alert-dismissible fade show">${esc(message)}<button class="btn-close" data-bs-dismiss="alert" aria-label="Đóng"></button></div>`);
    }

    function showError(xhr) {
        const response = xhr.responseJSON;
        let message = response?.message || 'Không thể thực hiện yêu cầu';
        if (response?.body && typeof response.body === 'object') {
            message += ': ' + Object.values(response.body).join(', ');
        }
        notify(message, 'danger');
    }

    function showPreview(path) {
        $('#categoryImagePreview').html(path
            ? `<img src="${fileUrl(path)}" alt="Hình danh mục">`
            : '<span>Chưa có ảnh</span>');
    }

    function loadCategories() {
        $.getJSON('/api/category').done(response => {
            const rows = response.body.map(category => `
                <tr>
                    <td class="text-secondary">#${category.categoryId}</td>
                    <td>${category.icon
                        ? `<img class="thumb" src="${fileUrl(category.icon)}" alt="${esc(category.categoryName)}">`
                        : '<span class="thumb placeholder-thumb">Chưa có ảnh</span>'}</td>
                    <td class="fw-semibold">${esc(category.categoryName)}</td>
                    <td class="text-end text-nowrap">
                        <button class="btn btn-sm btn-outline-primary edit" data-id="${category.categoryId}">Chỉnh sửa</button>
                        <button class="btn btn-sm btn-outline-danger delete" data-id="${category.categoryId}">Xóa</button>
                    </td>
                </tr>`).join('');
            $('#categoryRows').html(rows || '<tr><td colspan="4" class="text-center py-5 text-secondary">Cửa hàng chưa có danh mục hoa.</td></tr>');
        }).fail(showError);
    }

    $('#addCategory').on('click', () => {
        $('#categoryForm')[0].reset();
        $('#categoryId').val('');
        $('#categoryModalTitle').text('Thêm danh mục');
        $('#saveCategory').text('Thêm danh mục');
        showPreview(null);
        modal.show();
    });

    $('#categoryRows').on('click', '.edit', function () {
        $.post('/api/category/getCategory', { id: $(this).data('id') }).done(response => {
            const category = response.body;
            $('#categoryForm')[0].reset();
            $('#categoryId').val(category.categoryId);
            $('#categoryName').val(category.categoryName);
            $('#categoryModalTitle').text('Cập nhật danh mục');
            $('#saveCategory').text('Lưu thay đổi');
            showPreview(category.icon);
            modal.show();
        }).fail(showError);
    });

    $('#categoryRows').on('click', '.delete', function () {
        const id = $(this).data('id');
        const name = $(this).closest('tr').find('td').eq(2).text();
        if (!confirm(`Bạn muốn xóa danh mục “${name}”?`)) return;
        $.ajax({
            url: '/api/category/deleteCategory?categoryId=' + encodeURIComponent(id),
            method: 'DELETE'
        }).done(response => {
            notify(response.message);
            loadCategories();
        }).fail(showError);
    });

    $('#icon').on('change', function () {
        const file = this.files?.[0];
        if (file) {
            $('#categoryImagePreview').html(`<img src="${URL.createObjectURL(file)}" alt="Ảnh xem trước">`);
        }
    });

    $('#categoryForm').on('submit', function (event) {
        event.preventDefault();
        const id = $('#categoryId').val();
        const data = new FormData(this);
        $('#saveCategory').prop('disabled', true);
        $.ajax({
            url: id ? '/api/category/updateCategory?categoryId=' + encodeURIComponent(id) : '/api/category/addCategory',
            method: id ? 'PUT' : 'POST',
            data,
            processData: false,
            contentType: false
        }).done(response => {
            modal.hide();
            notify(response.message);
            loadCategories();
        }).fail(showError).always(() => $('#saveCategory').prop('disabled', false));
    });

    loadCategories();
})();
