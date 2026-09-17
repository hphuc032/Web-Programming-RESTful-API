package vn.iotstar.configs;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.iotstar.entity.*;
import vn.iotstar.repository.*;
import java.time.LocalDateTime;
import java.util.*;

@Component @RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed-data", havingValue = "true", matchIfMissing = true)
public class DataInitializer implements ApplicationRunner {
 private final CategoryRepository categories;
 private final ProductRepository products;
 @Override @Transactional
 public void run(ApplicationArguments args) {
  if (categories.count() == 0) {
   for (String name : List.of("Hoa Hồng", "Hoa Sinh Nhật", "Hoa Cưới")) {
    Category category = new Category();
    category.setCategoryName(name);
    categories.save(category);
   }
  }
  if (products.count() == 0) {
   List<Category> all = categories.findAll();
   if (!all.isEmpty()) {
    products.save(sample("Hoa Hồng Đỏ", 20, 250000d, 5d, all.get(0)));
    products.save(sample("Birthday Flower", 10, 350000d, 0d, all.get(Math.min(1, all.size() - 1))));
    products.save(sample("Wedding Bouquet", 7, 650000d, 10d, all.get(Math.min(2, all.size() - 1))));
   }
  }
 }
 private Product sample(String name, int quantity, double price, double discount, Category category) {
  return Product.builder().productName(name).quantity(quantity).unitPrice(price).description("Dữ liệu mẫu phục vụ demo")
   .discount(discount).createDate(LocalDateTime.now()).status((short) 1).category(category).build();
 }
}
