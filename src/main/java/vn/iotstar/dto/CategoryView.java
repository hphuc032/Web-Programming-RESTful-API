package vn.iotstar.dto;
import vn.iotstar.entity.Category;
public record CategoryView(Long categoryId, String categoryName, String icon) {
 public static CategoryView from(Category c) { return new CategoryView(c.getCategoryId(), c.getCategoryName(), c.getIcon()); }
}
