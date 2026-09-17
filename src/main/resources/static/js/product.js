(function () {
    const modal = new bootstrap.Modal(document.getElementById('productModal'));
    const esc = value => $('<div>').text(value ?? '').html();
    const fileUrl = path => path ? '/uploads/' + path.split('/').map(encodeURIComponent).join('/') : '';
    const money = value => new Intl.NumberFormat('vi-VN').format(value) + ' ₫';

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
        $('#productImagePreview').html(path
            ? `<img src="${fileUrl(path)}" alt="Hình sản phẩm">`
            : '<span>Chưa có ảnh</span>');
    }

    function loadCategories(selected) {
        return $.getJSON('/api/category').done(response => {
            const options = response.body
                .map(category => `<option value="${category.categoryId}">${esc(category.categoryName)}</option>`)
                .join('');
            $('#categoryId').html('<option value="">Chọn danh mục</option>' + options).val(selected || '');
        });
    }

    function loadProducts() {
        $.getJSON('/api/product').done(response => {
            const rows = response.body.map(product => `
                <tr>
                    <td class="text-secondary">#${product.productId}</td>
                    <td>${product.images
                        ? `<img class="thumb" src="${fileUrl(product.images)}" alt="${esc(product.productName)}">`
                        : '<span class="thumb placeholder-thumb">Chưa có ảnh</span>'}</td>
                    <td class="fw-semibold">${esc(product.productName)}</td>
                    <td>${esc(product.category.categoryName)}</td>
                    <td class="price-cell">${money(product.unitPrice)}${product.discount > 0 ? `<span class="discount-note">Ưu đãi ${product.discount}%</span>` : ''}</td>
                    <td>${product.quantity}</td>
                    <td><span class="status-badge ${product.status === 1 ? 'status-active' : 'status-inactive'}">${product.status === 1 ? 'Đang bán' : 'Tạm ngừng'}</span></td>
                    <td class="text-end text-nowrap">
                        <button class="btn btn-sm btn-outline-primary edit" data-id="${product.productId}">Chỉnh sửa</button>
                        <button class="btn btn-sm btn-outline-danger delete" data-id="${product.productId}">Xóa</button>
                    </td>
                </tr>`).join('');
            $('#productRows').html(rows || '<tr><td colspan="8" class="text-center py-5 text-secondary">Cửa hàng chưa có sản phẩm hoa.</td></tr>');
        }).fail(showError);
    }

    $('#addProduct').on('click', () => {
        $('#productForm')[0].reset();
        $('#productId').val('');
        $('#status').val('1');
        $('#discount').val('0');
        $('#productModalTitle').text('Thêm sản phẩm');
        $('#saveProduct').text('Thêm sản phẩm');
        showPreview(null);
        loadCategories().done(() => modal.show()).fail(showError);
    });

    $('#productRows').on('click', '.edit', function () {
        $.post('/api/product/getProduct', { id: $(this).data('id') }).done(response => {
            const product = response.body;
            $('#productForm')[0].reset();
            $('#productId').val(product.productId);
            $('#productName').val(product.productName);
            $('#quantity').val(product.quantity);
            $('#unitPrice').val(product.unitPrice);
            $('#discount').val(product.discount);
            $('#status').val(product.status);
            $('#description').val(product.description);
            $('#productModalTitle').text('Cập nhật sản phẩm');
            $('#saveProduct').text('Lưu thay đổi');
            showPreview(product.images);
            loadCategories(product.category.categoryId).done(() => modal.show()).fail(showError);
        }).fail(showError);
    });

    $('#productRows').on('click', '.delete', function () {
        const id = $(this).data('id');
        const name = $(this).closest('tr').find('td').eq(2).text();
        if (!confirm(`Bạn muốn xóa sản phẩm “${name}”?`)) return;
        $.ajax({
            url: '/api/product/deleteProduct?productId=' + encodeURIComponent(id),
            method: 'DELETE'
        }).done(response => {
            notify(response.message);
            loadProducts();
        }).fail(showError);
    });

    $('#image').on('change', function () {
        const file = this.files?.[0];
        if (file) {
            $('#productImagePreview').html(`<img src="${URL.createObjectURL(file)}" alt="Ảnh xem trước">`);
        }
    });

    $('#productForm').on('submit', function (event) {
        event.preventDefault();
        const id = $('#productId').val();
        const data = new FormData(this);
        $('#saveProduct').prop('disabled', true);
        $.ajax({
            url: id ? '/api/product/updateProduct?productId=' + encodeURIComponent(id) : '/api/product/addProduct',
            method: id ? 'PUT' : 'POST',
            data,
            processData: false,
            contentType: false
        }).done(response => {
            modal.hide();
            notify(response.message);
            loadProducts();
        }).fail(showError).always(() => $('#saveProduct').prop('disabled', false));
    });

    loadProducts();
})();
