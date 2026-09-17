package vn.iotstar.controllers.api;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import vn.iotstar.dto.*;
import vn.iotstar.services.CategoryService;
import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
@Tag(name = "Category API", description = "CRUD danh mục bằng REST API")
public class CategoryApiController {
 private final CategoryService service;
 @GetMapping
 public ResponseEntity<ApiResponse<List<CategoryView>>> getAll() {
  return ResponseEntity.ok(ApiResponse.ok("Lấy danh sách danh mục thành công", service.findAll()));
 }
 @PostMapping("/getCategory")
 public ResponseEntity<ApiResponse<CategoryView>> get(@RequestParam Long id) {
  return ResponseEntity.ok(ApiResponse.ok("Lấy danh mục thành công", service.findById(id)));
 }
 @PostMapping(value = "/addCategory", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
 public ResponseEntity<ApiResponse<CategoryView>> add(@Valid @ModelAttribute CategoryForm form) {
  return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Thêm danh mục thành công", service.save(null, form)));
 }
 @PutMapping(value = "/updateCategory", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
 public ResponseEntity<ApiResponse<CategoryView>> update(@RequestParam Long categoryId, @Valid @ModelAttribute CategoryForm form) {
  return ResponseEntity.ok(ApiResponse.ok("Cập nhật danh mục thành công", service.save(categoryId, form)));
 }
 @DeleteMapping("/deleteCategory")
 public ResponseEntity<ApiResponse<Void>> delete(@RequestParam Long categoryId) {
  service.deleteById(categoryId);
  return ResponseEntity.ok(ApiResponse.ok("Xóa danh mục thành công", null));
 }
}
