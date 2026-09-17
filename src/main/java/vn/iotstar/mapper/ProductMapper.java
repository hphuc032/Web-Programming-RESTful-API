package vn.iotstar.mapper;
import org.springframework.stereotype.Component;
import vn.iotstar.dto.*;
import vn.iotstar.entity.Product;

@Component
public class ProductMapper {
 public ProductView toDTO(Product entity) {
  return new ProductView(entity.getProductId(), entity.getProductName(), entity.getQuantity(),
   entity.getUnitPrice(), entity.getImages(), entity.getDescription(), entity.getDiscount(),
   entity.getCreateDate(), entity.getStatus(), CategoryView.from(entity.getCategory()));
 }
 public void updateEntity(ProductDTO dto, Product entity) {
  entity.setProductName(dto.getProductName().trim());
  entity.setQuantity(dto.getQuantity());
  entity.setUnitPrice(dto.getUnitPrice());
  entity.setDescription(dto.getDescription());
  entity.setDiscount(dto.getDiscount());
  entity.setStatus(dto.getStatus());
 }
}
