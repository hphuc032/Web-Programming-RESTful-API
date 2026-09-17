package vn.iotstar.controllers.api;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import vn.iotstar.dto.*;
import vn.iotstar.services.ProductService;
import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
@Tag(name = "Product API", description = "CRUD sản phẩm bằng REST API")
public class ProductApiController {
 private final ProductService service;
 @GetMapping
 public ResponseEntity<ApiResponse<List<ProductView>>> getAll() {
  return ResponseEntity.ok(ApiResponse.ok("Lấy danh sách sản phẩm thành công", service.findAll()));
 }
 @PostMapping("/getProduct")
 public ResponseEntity<ApiResponse<ProductView>> get(@RequestParam Long id) {
  return ResponseEntity.ok(ApiResponse.ok("Lấy sản phẩm thành công", service.findById(id)));
 }
 @PostMapping(value = "/addProduct", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
 public ResponseEntity<ApiResponse<ProductView>> add(@Valid @ModelAttribute ProductDTO form) {
  return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Thêm sản phẩm thành công", service.save(null, form)));
 }
 @PutMapping(value = "/updateProduct", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
 public ResponseEntity<ApiResponse<ProductView>> update(@RequestParam Long productId, @Valid @ModelAttribute ProductDTO form) {
  return ResponseEntity.ok(ApiResponse.ok("Cập nhật sản phẩm thành công", service.save(productId, form)));
 }
 @DeleteMapping("/deleteProduct")
 public ResponseEntity<ApiResponse<Void>> delete(@RequestParam Long productId) {
  service.deleteById(productId);
  return ResponseEntity.ok(ApiResponse.ok("Xóa sản phẩm thành công", null));
 }
}
