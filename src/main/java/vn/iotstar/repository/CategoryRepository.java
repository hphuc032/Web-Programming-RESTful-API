package vn.iotstar.repository;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.iotstar.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
 Optional<Category> findByCategoryName(String name);
 Optional<Category> findByCategoryNameIgnoreCase(String name);
 List<Category> findByCategoryNameContainingIgnoreCase(String name);
 Page<Category> findByCategoryNameContainingIgnoreCase(String name, Pageable pageable);
 
}
