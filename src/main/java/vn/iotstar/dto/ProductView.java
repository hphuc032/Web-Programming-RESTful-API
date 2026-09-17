package vn.iotstar.dto;
import java.time.LocalDateTime;
public record ProductView(Long productId, String productName, Integer quantity, Double unitPrice,
 String images, String description, Double discount, LocalDateTime createDate, Short status, CategoryView category) {}
