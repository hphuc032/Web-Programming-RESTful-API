package vn.iotstar;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import vn.iotstar.entity.Category;
import vn.iotstar.repository.*;
import java.nio.charset.StandardCharsets;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
 "spring.datasource.url=jdbc:h2:mem:ltweb;MODE=MySQL;DB_CLOSE_DELAY=-1",
 "spring.datasource.driver-class-name=org.h2.Driver",
 "spring.jpa.hibernate.ddl-auto=create-drop",
 "app.seed-data=false",
 "storage.location=target/test-uploads"
})
@AutoConfigureMockMvc
class ApiIntegrationTest {
 @Autowired MockMvc mvc;
 @Autowired CategoryRepository categories;
 @Autowired ProductRepository products;

 @BeforeEach void clean() { products.deleteAll(); categories.deleteAll(); }

 @Test void categoryCrudAndValidationWork() throws Exception {
  String created = mvc.perform(multipart("/api/category/addCategory").param("categoryName", "Hoa Lan"))
   .andExpect(status().isCreated()).andExpect(jsonPath("$.status").value(true))
   .andExpect(jsonPath("$.body.categoryName").value("Hoa Lan"))
   .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
  long id = categories.findByCategoryName("Hoa Lan").orElseThrow().getCategoryId();
  mvc.perform(multipart("/api/category/updateCategory").param("categoryId", String.valueOf(id))
    .param("categoryName", "Hoa Lan Trắng").with(r -> { r.setMethod("PUT"); return r; }))
   .andExpect(status().isOk()).andExpect(jsonPath("$.body.categoryName").value("Hoa Lan Trắng"));
  mvc.perform(get("/api/category")).andExpect(status().isOk()).andExpect(jsonPath("$.body", hasSize(1)));
  mvc.perform(delete("/api/category/deleteCategory").param("categoryId", String.valueOf(id)))
   .andExpect(status().isOk());
  mvc.perform(multipart("/api/category/addCategory").param("categoryName", " "))
   .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(false));
  Assertions.assertFalse(created.isBlank());
 }

 @Test void productCrudUploadAndCategoryConstraintWork() throws Exception {
  Category category = new Category(); category.setCategoryName("Hoa Hồng"); category = categories.saveAndFlush(category);
  MockMultipartFile image = new MockMultipartFile("image", "rose.png", "image/png", new byte[]{1,2,3});
  mvc.perform(multipart("/api/product/addProduct").file(image).param("productName", "Hoa Hồng Đỏ")
    .param("quantity", "10").param("unitPrice", "250000").param("description", "Mô tả")
    .param("discount", "5").param("status", "1").param("categoryId", category.getCategoryId().toString()))
   .andExpect(status().isCreated()).andExpect(jsonPath("$.body.images", endsWith(".png")))
   .andExpect(jsonPath("$.body.category.categoryName").value("Hoa Hồng"));
  var product = products.findByProductName("Hoa Hồng Đỏ").orElseThrow();
  var createdAt = product.getCreateDate();
  mvc.perform(multipart("/api/product/updateProduct").param("productId", product.getProductId().toString())
    .param("productName", "Hoa Hồng Premium").param("quantity", "8").param("unitPrice", "300000")
    .param("description", "Mới").param("discount", "10").param("status", "1")
    .param("categoryId", category.getCategoryId().toString()).with(r -> { r.setMethod("PUT"); return r; }))
   .andExpect(status().isOk()).andExpect(jsonPath("$.body.productName").value("Hoa Hồng Premium"));
  Assertions.assertEquals(createdAt, products.findById(product.getProductId()).orElseThrow().getCreateDate());
  mvc.perform(delete("/api/category/deleteCategory").param("categoryId", category.getCategoryId().toString()))
   .andExpect(status().isConflict());
  mvc.perform(delete("/api/product/deleteProduct").param("productId", product.getProductId().toString()))
   .andExpect(status().isOk());
  mvc.perform(delete("/api/category/deleteCategory").param("categoryId", category.getCategoryId().toString()))
   .andExpect(status().isOk());
 }

 @Test void productRejectsMissingCategoryAndBadNumbers() throws Exception {
  mvc.perform(multipart("/api/product/addProduct").param("productName", "Sai dữ liệu")
    .param("quantity", "-1").param("unitPrice", "-1").param("discount", "101")
    .param("status", "1").param("categoryId", "999"))
   .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("Dữ liệu không hợp lệ"));
  mvc.perform(multipart("/api/product/addProduct").param("productName", "Không có category")
    .param("quantity", "1").param("unitPrice", "100").param("discount", "0")
    .param("status", "1").param("categoryId", "999"))
   .andExpect(status().isNotFound()).andExpect(jsonPath("$.message").value("Category không tồn tại"));
 }
}
