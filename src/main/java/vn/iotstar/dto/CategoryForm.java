package vn.iotstar.dto;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
public class CategoryForm {
 @NotBlank(message = "Tên danh mục không được để trống")
 @Size(max = 200, message = "Tên danh mục tối đa 200 ký tự")
 private String categoryName;
 private MultipartFile icon;
}
