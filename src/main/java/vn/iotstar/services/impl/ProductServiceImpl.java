package vn.iotstar.services.impl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.iotstar.dto.*;
import vn.iotstar.entity.*;
import vn.iotstar.exception.ApiException;
import vn.iotstar.mapper.ProductMapper;
import vn.iotstar.repository.*;
import vn.iotstar.services.*;
import java.util.*;

@Service @RequiredArgsConstructor @Transactional
public class ProductServiceImpl implements ProductService {
 private final ProductRepository repository;
 private final CategoryRepository categoryRepository;
 private final ProductMapper mapper;
 private final StorageService storage;
 @Override @Transactional(readOnly = true)
 public List<ProductView> findAll() { return repository.findAll(Sort.by("productId")).stream().map(mapper::toDTO).toList(); }
 @Override @Transactional(readOnly = true)
 public ProductView findById(Long id) { return mapper.toDTO(get(id)); }
 @Override public ProductView save(Long id, ProductDTO form) {
  String name = form.getProductName().trim();
  repository.findByProductNameIgnoreCase(name).filter(p -> !Objects.equals(p.getProductId(), id))
   .ifPresent(p -> { throw ApiException.conflict("Tên sản phẩm đã tồn tại"); });
  Category category = categoryRepository.findById(form.getCategoryId()).orElseThrow(() -> ApiException.notFound("Category"));
  Product product = id == null ? new Product() : get(id);
  String oldImage = product.getImages();
  String newImage = storage.store(form.getImage(), "products");
  mapper.updateEntity(form, product);
  product.setProductName(name);
  product.setCategory(category);
  if (newImage != null) product.setImages(newImage);
  try {
   Product saved = repository.saveAndFlush(product);
   ProductView view = mapper.toDTO(saved);
   if (newImage != null) storage.delete(oldImage);
   return view;
  } catch (RuntimeException e) {
   storage.delete(newImage);
   throw e;
  }
 }
 @Override public void delete(Long id) {
  Product product = get(id);
  repository.delete(product);
  repository.flush();
  storage.delete(product.getImages());
 }
 @Override public void deleteById(Long id) { delete(id); }
 @Override @Transactional(readOnly = true) public long count() { return repository.count(); }
 @Override @Transactional(readOnly = true)
 public Optional<ProductView> findByProductName(String name) { return repository.findByProductName(name).map(mapper::toDTO); }
 @Override @Transactional(readOnly = true)
 public List<ProductView> findByProductNameContainingIgnoreCase(String name) {
  return repository.findByProductNameContainingIgnoreCase(name).stream().map(mapper::toDTO).toList();
 }
 @Override @Transactional(readOnly = true)
 public Page<ProductView> findByProductNameContainingIgnoreCase(String name, Pageable pageable) {
  return repository.findByProductNameContainingIgnoreCase(name, pageable).map(mapper::toDTO);
 }
 private Product get(Long id) { return repository.findById(id).orElseThrow(() -> ApiException.notFound("Product")); }
}
