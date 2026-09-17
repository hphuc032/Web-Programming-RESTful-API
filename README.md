# Project 1 - Product Management

## Features

- CRUD Product
- DTO + Mapper
- Validation
- Search
- Pagination
- Single Image Upload

## Tech Stack

- Java 21
- Spring Boot 4.0.0
- Spring MVC, Spring Data JPA, Hibernate
- Thymeleaf, Thymeleaf Layout Dialect, Bootstrap 5
- Jakarta Validation, Lombok, Maven
- MySQL

## Database Configuration

Ứng dụng dùng MySQL tại `localhost:3306`, database mặc định `webst2`. URL có thể được thay đổi bằng biến môi trường `DB_URL`. Tạo database trước khi chạy nếu tài khoản không có quyền tự tạo:

```sql
CREATE DATABASE webst2
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
```

Không lưu mật khẩu thật trong source code. Cách khuyến nghị là chạy script bảo mật; mật khẩu được nhập ẩn và chỉ tồn tại trong tiến trình ứng dụng:

```powershell
.\run-local.ps1
```

Hoặc tự thiết lập biến môi trường chỉ trong phiên terminal hiện tại:

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your-password"
```

Hibernate dùng `spring.jpa.hibernate.ddl-auto=update` và sẽ tạo/cập nhật bảng `products`.

## How to Run

```powershell
cd springboot-product-single-image
mvn clean package
.\run-local.ps1
```

`run-local.ps1` chạy file JAR đã build và không ghi mật khẩu xuống ổ đĩa.

## Main URL

[http://localhost:8099/products](http://localhost:8099/products)

## Upload Directory

Ảnh được lưu vật lý tại:

```text
uploads/products/
```

MySQL chỉ lưu tên file UUID, không lưu binary hoặc Base64.

## Project Structure

```text
src/main/java/vn/iotstar/
├── configs/WebConfig.java
├── controllers/ProductController.java
├── dto/ProductDTO.java
├── entity/Product.java
├── mapper/ProductMapper.java
├── repository/ProductRepository.java
├── services/ProductService.java
├── services/impl/ProductServiceImpl.java
└── SpringBootApplication.java

src/main/resources/
├── templates/fragments/{header,footer}.html
├── templates/layouts/layout.html
├── templates/products/{list,form}.html
└── application.properties
```

## Test Cases

1. Tạo Product không có ảnh.
2. Tạo Product có một ảnh.
3. Kiểm tra ảnh hiển thị trong danh sách.
4. Tìm kiếm theo một phần tên, không phân biệt hoa thường.
5. Chuyển trang và kiểm tra keyword/page size được giữ lại.
6. Thay đổi page size giữa 5, 10 và 20.
7. Sửa Product không chọn ảnh mới và kiểm tra ảnh cũ vẫn còn.
8. Sửa Product có ảnh mới và kiểm tra ảnh cũ bị xóa.
9. Xóa Product và kiểm tra cả record lẫn file ảnh bị xóa.
10. Kiểm tra validation với tên rỗng, giá âm và số lượng âm.
