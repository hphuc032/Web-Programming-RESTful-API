package vn.iotstar.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import vn.iotstar.dto.ProductDTO;
import vn.iotstar.entity.Product;
import vn.iotstar.mapper.ProductMapper;
import vn.iotstar.repository.ProductRepository;
import vn.iotstar.services.ProductService;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final Path uploadDir = Paths.get("uploads/products");

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> findAll(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("id").descending());

        Page<Product> products;
        if (keyword == null || keyword.isBlank()) {
            products = productRepository.findAll(pageable);
        } else {
            products = productRepository.findByNameContainingIgnoreCase(
                    keyword.trim(),
                    pageable);
        }

        return products.map(productMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        return productMapper.toDTO(getProduct(id));
    }

    @Override
    public ProductDTO create(ProductDTO dto) {
        String newImage = saveImage(dto.getImage());
        dto.setImages(newImage);

        try {
            Product saved = productRepository.save(productMapper.toEntity(dto));
            return productMapper.toDTO(saved);
        } catch (RuntimeException exception) {
            deleteImage(newImage);
            throw exception;
        }
    }

    @Override
    public ProductDTO update(Long id, ProductDTO dto) {
        Product product = getProduct(id);
        String oldImage = product.getImages();
        MultipartFile image = dto.getImage();
        String newImage = null;

        if (image != null && !image.isEmpty()) {
            newImage = saveImage(image);
            dto.setImages(newImage);
        } else {
            dto.setImages(oldImage);
        }

        try {
            productMapper.updateEntity(dto, product);
            Product updated = productRepository.save(product);

            if (newImage != null) {
                deleteImage(oldImage);
            }

            return productMapper.toDTO(updated);
        } catch (RuntimeException exception) {
            if (newImage != null) {
                deleteImage(newImage);
            }
            throw exception;
        }
    }

    @Override
    public void delete(Long id) {
        Product product = getProduct(id);
        deleteImage(product.getImages());
        productRepository.delete(product);
    }

    private Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Không tìm thấy sản phẩm có ID: " + id));
    }

    private String saveImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            Path uploadRoot = uploadDir.toAbsolutePath().normalize();
            Files.createDirectories(uploadRoot);

            String extension = getSafeExtension(file.getOriginalFilename());
            String fileName = UUID.randomUUID() + extension;
            Path target = uploadRoot.resolve(fileName).normalize();

            if (!target.startsWith(uploadRoot)) {
                throw new SecurityException("Tên file ảnh không hợp lệ");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(
                        inputStream,
                        target,
                        StandardCopyOption.REPLACE_EXISTING);
            }

            return fileName;
        } catch (IOException exception) {
            throw new UncheckedIOException("Không thể lưu ảnh sản phẩm", exception);
        }
    }

    private String getSafeExtension(String originalName) {
        if (originalName == null || originalName.isBlank()) {
            return "";
        }

        String simpleName = Paths.get(originalName).getFileName().toString();
        int lastDot = simpleName.lastIndexOf('.');
        if (lastDot < 0) {
            return "";
        }

        String extension = simpleName.substring(lastDot).toLowerCase();
        return extension.matches("\\.[a-z0-9]{1,10}") ? extension : "";
    }

    private void deleteImage(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return;
        }

        try {
            Path uploadRoot = uploadDir.toAbsolutePath().normalize();
            Path file = uploadRoot.resolve(fileName).normalize();

            if (!file.startsWith(uploadRoot)) {
                return;
            }

            Files.deleteIfExists(file);
        } catch (IOException exception) {
            System.err.println("Không thể xóa ảnh: " + fileName);
        }
    }
}
