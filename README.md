# LTWeb REST API + AJAX Product Category

## Student Assignment

Project 1 - REST API + AJAX CRUD Product & Category.

Project dùng Springdoc OpenAPI để cung cấp Swagger 3 tương thích Spring Boot 3. Không dùng Springfox vì thư viện này không tương thích tốt với Jakarta/Spring Boot 3. Project không triển khai GraphQL; GraphQL thuộc Project 2.

## Technologies

- Java 17
- Spring Boot 3.1.5
- Maven
- Spring Data JPA / Hibernate
- MySQL 8
- Validation và Lombok
- Springdoc OpenAPI / Swagger UI
- Thymeleaf page shell
- jQuery 3.6.4 AJAX và Bootstrap 5

## Features

- Category CRUD bằng REST API và AJAX
- Product CRUD bằng REST API và AJAX
- Product chọn Category được tải từ database
- Upload icon/ảnh bằng tên UUID, kiểm tra định dạng và chống path traversal
- Giữ ảnh cũ khi update không gửi file mới
- Chặn xóa Category đang có Product bằng HTTP 409
- Response thống nhất: `status`, `message`, `body`
- Swagger API documentation

## Database setup

```sql
CREATE DATABASE ltweb_rest_ajax
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

Copy `application-local.properties.example` thành `application-local.properties`, sau đó sửa duy nhất các giá trị local:

```properties
spring.datasource.username=root
spring.datasource.password=your_mysql_password
```

File `application-local.properties` đã được `.gitignore` để không commit mật khẩu. URL mặc định nằm trong `src/main/resources/application.properties` và có thể ghi đè bằng `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`.

## Run

Yêu cầu JDK 17 và MySQL đang chạy.

```powershell
mvn clean test
mvn spring-boot:run
```

Hoặc build JAR rồi dùng script nhập password an toàn:

```powershell
mvn clean package
./run-local.ps1
```

## URLs

- Home: http://localhost:8080/
- Category: http://localhost:8080/categories
- Product: http://localhost:8080/products
- Swagger: http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## API endpoints

### Category API

| Method | Endpoint | Mô tả |
|---|---|---|
| GET | `/api/category` | Lấy tất cả Category |
| POST | `/api/category/getCategory` | Lấy Category theo parameter `id` |
| POST | `/api/category/addCategory` | Thêm Category bằng multipart/form-data |
| PUT | `/api/category/updateCategory?categoryId={id}` | Sửa Category; file trống giữ icon cũ |
| DELETE | `/api/category/deleteCategory?categoryId={id}` | Xóa Category |

### Product API

| Method | Endpoint | Mô tả |
|---|---|---|
| GET | `/api/product` | Lấy tất cả Product |
| POST | `/api/product/getProduct` | Lấy Product theo parameter `id` |
| POST | `/api/product/addProduct` | Thêm Product bằng multipart/form-data |
| PUT | `/api/product/updateProduct?productId={id}` | Sửa Product; file trống giữ ảnh cũ |
| DELETE | `/api/product/deleteProduct?productId={id}` | Xóa Product |

Ảnh runtime nằm trong `uploads/categories` và `uploads/products`, được truy cập qua `/uploads/{folder}/{filename}`. Khi database trống, ứng dụng chỉ thêm dữ liệu mẫu một lần và không xóa dữ liệu ở các lần khởi động sau.
