package vn.iotstar.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories", uniqueConstraints = @UniqueConstraint(name = "uk_category_name", columnNames = "category_name"))
@Getter @Setter @NoArgsConstructor
public class Category {
 @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
 @Column(name = "category_id")
 private Long categoryId;
 @Column(name = "category_name", nullable = false, length = 200)
 private String categoryName;
 @Column(length = 100)
 private String icon;
 @JsonIgnore
 @OneToMany(mappedBy = "category")
 private List<Product> products = new ArrayList<>();
}
