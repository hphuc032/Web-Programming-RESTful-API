package vn.iotstar.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "products", uniqueConstraints = @UniqueConstraint(name = "uk_product_name", columnNames = "product_name"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Product {
 @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
 @Column(name = "product_id")
 private Long productId;
 @Column(name = "product_name", nullable = false, length = 200)
 private String productName;
 @Column(nullable = false)
 private Integer quantity;
 @Column(name = "unit_price", nullable = false)
 private Double unitPrice;
 @Column(length = 100)
 private String images;
 @Column(length = 2000)
 private String description;
 @Column(nullable = false)
 private Double discount;
 @Column(name = "create_date", nullable = false, updatable = false)
 private LocalDateTime createDate;
 @Column(nullable = false)
 private Short status;
 @ManyToOne(fetch = FetchType.LAZY, optional = false)
 @JoinColumn(name = "category_id", nullable = false, foreignKey = @ForeignKey(name = "fk_product_category"))
 private Category category;
 @PrePersist
 void initializeCreatedAt() { if (createDate == null) createDate = LocalDateTime.now(); }
}
