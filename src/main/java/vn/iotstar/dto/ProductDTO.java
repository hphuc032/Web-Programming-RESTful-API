package vn.iotstar.dto;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductDTO {
 @NotBlank(message = "Tên sản phẩm không được để trống")
 @Size(max = 200, message = "Tên sản phẩm tối đa 200 ký tự")
 private String productName;
 @NotNull(message = "Số lượng là bắt buộc") @Min(value = 0, message = "Số lượng phải >= 0")
 private Integer quantity;
 @NotNull(message = "Đơn giá là bắt buộc") @DecimalMin(value = "0", message = "Đơn giá phải >= 0")
 @DecimalMax(value = "1000000000000", message = "Đơn giá tối đa 1.000 tỷ")
 private Double unitPrice;
 @Size(max = 2000, message = "Mô tả tối đa 2000 ký tự")
 private String description;
 @NotNull(message = "Giảm giá là bắt buộc") @DecimalMin(value = "0", message = "Giảm giá phải >= 0")
 @DecimalMax(value = "100", message = "Giảm giá tối đa 100%")
 private Double discount;
 @NotNull(message = "Trạng thái là bắt buộc") @Min(0) @Max(1)
 private Short status;
 @NotNull(message = "Danh mục là bắt buộc") @Positive
 private Long categoryId;
 private MultipartFile image;
}
