package vn.iotstar.services;
import java.util.*;
import org.springframework.data.domain.*;
import vn.iotstar.dto.*;

public interface ProductService {
 List<ProductView> findAll();
 ProductView findById(Long id);
 ProductView save(Long id, ProductDTO form);
 void delete(Long id);
 void deleteById(Long id);
 long count();
 Optional<ProductView> findByProductName(String name);
 List<ProductView> findByProductNameContainingIgnoreCase(String name);
 Page<ProductView> findByProductNameContainingIgnoreCase(String name, Pageable pageable);
}
