package vn.iotstar.services.impl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.iotstar.dto.*;
import vn.iotstar.entity.Category;
import vn.iotstar.exception.ApiException;
import vn.iotstar.repository.*;
import vn.iotstar.services.*;
import java.util.*;

@Service @RequiredArgsConstructor @Transactional
public class CategoryServiceImpl implements CategoryService {
 private final CategoryRepository repository;
 private final ProductRepository productRepository;
 private final StorageService storage;
 @Override @Transactional(readOnly = true)
 public List<CategoryView> findAll() { return repository.findAll(Sort.by("categoryId")).stream().map(CategoryView::from).toList(); }
 @Override @Transactional(readOnly = true)
 public CategoryView findById(Long id) { return CategoryView.from(get(id)); }
 @Override public CategoryView save(Long id, CategoryForm form) {
  String name = form.getCategoryName().trim();
  repository.findByCategoryNameIgnoreCase(name).filter(c -> !Objects.equals(c.getCategoryId(), id))
   .ifPresent(c -> { throw ApiException.conflict("Tên danh mục đã tồn tại"); });
  Category category = id == null ? new Category() : get(id);
  String oldIcon = category.getIcon();
  String newIcon = storage.store(form.getIcon(), "categories");
  category.setCategoryName(name);
  if (newIcon != null) category.setIcon(newIcon);
  try {
   Category saved = repository.saveAndFlush(category);
   if (newIcon != null) storage.delete(oldIcon);
   return CategoryView.from(saved);
  } catch (RuntimeException e) {
   storage.delete(newIcon);
   throw e;
  }
 }
 @Override public void delete(Long id) {
  Category category = get(id);
  if (productRepository.existsByCategoryCategoryId(id))
   throw new ApiException(HttpStatus.CONFLICT, "Không thể xóa danh mục đang có sản phẩm");
  repository.delete(category);
  repository.flush();
  storage.delete(category.getIcon());
 }
 @Override public void deleteById(Long id) { delete(id); }
 @Override @Transactional(readOnly = true) public long count() { return repository.count(); }
 @Override @Transactional(readOnly = true)
 public Optional<CategoryView> findByCategoryName(String name) { return repository.findByCategoryName(name).map(CategoryView::from); }
 @Override @Transactional(readOnly = true)
 public List<CategoryView> findByCategoryNameContainingIgnoreCase(String name) {
  return repository.findByCategoryNameContainingIgnoreCase(name).stream().map(CategoryView::from).toList();
 }
 @Override @Transactional(readOnly = true)
 public Page<CategoryView> findByCategoryNameContainingIgnoreCase(String name, Pageable pageable) {
  return repository.findByCategoryNameContainingIgnoreCase(name, pageable).map(CategoryView::from);
 }
 private Category get(Long id) { return repository.findById(id).orElseThrow(() -> ApiException.notFound("Category")); }
}
