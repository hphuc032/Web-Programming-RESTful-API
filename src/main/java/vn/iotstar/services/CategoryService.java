package vn.iotstar.services;
import java.util.*;
import org.springframework.data.domain.*;
import vn.iotstar.dto.*;

public interface CategoryService {
 List<CategoryView> findAll();
 CategoryView findById(Long id);
 CategoryView save(Long id, CategoryForm form);
 void delete(Long id);
 void deleteById(Long id);
 long count();
 Optional<CategoryView> findByCategoryName(String name);
 List<CategoryView> findByCategoryNameContainingIgnoreCase(String name);
 Page<CategoryView> findByCategoryNameContainingIgnoreCase(String name, Pageable pageable);
}
