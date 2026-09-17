package vn.iotstar.repository;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.iotstar.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
 Optional<Product> findByProductName(String name);
 Optional<Product> findByProductNameIgnoreCase(String name);
 List<Product> findByProductNameContainingIgnoreCase(String name);
 Page<Product> findByProductNameContainingIgnoreCase(String name, Pageable pageable);
 boolean existsByCategoryCategoryId(Long categoryId);
 List<Product> findByCategoryCategoryId(Long categoryId);
 List<Product> findAllByOrderByUnitPriceAsc();
}
