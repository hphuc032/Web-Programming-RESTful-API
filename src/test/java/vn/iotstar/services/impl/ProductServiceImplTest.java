package vn.iotstar.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import vn.iotstar.dto.ProductDTO;
import vn.iotstar.entity.Product;
import vn.iotstar.mapper.ProductMapper;
import vn.iotstar.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    private static final Path UPLOAD_DIR = Paths.get("uploads/products");

    @Mock
    private ProductRepository productRepository;

    private ProductServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ProductServiceImpl(productRepository, new ProductMapper());
    }

    @AfterEach
    void removeTestImages() throws Exception {
        if (Files.isDirectory(UPLOAD_DIR)) {
            try (var files = Files.list(UPLOAD_DIR)) {
                files.filter(path -> path.getFileName().toString().startsWith("test-old-"))
                        .forEach(this::deleteQuietly);
            }
        }
    }

    @Test
    void searchesByTrimmedKeywordAndMapsPageToDto() {
        Product product = product("Laptop", "laptop.jpg");
        when(productRepository.findByNameContainingIgnoreCase(eq("laptop"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(product)));

        Page<ProductDTO> result = service.findAll("  laptop  ", 0, 5);

        assertThat(result.getContent()).singleElement()
                .extracting(ProductDTO::getName)
                .isEqualTo("Laptop");
        verify(productRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void keepsOldImageWhenUpdateHasNoNewFile() {
        Product existing = product("Laptop cũ", "old.jpg");
        when(productRepository.findById(1L)).thenReturn(java.util.Optional.of(existing));
        when(productRepository.save(existing)).thenReturn(existing);
        ProductDTO update = dto("Laptop mới");

        ProductDTO result = service.update(1L, update);

        assertThat(result.getImages()).isEqualTo("old.jpg");
        assertThat(existing.getName()).isEqualTo("Laptop mới");
    }

    @Test
    void savesNewUuidImageAndDeletesOldImageOnUpdate() throws Exception {
        Files.createDirectories(UPLOAD_DIR);
        String oldName = "test-old-" + System.nanoTime() + ".jpg";
        Path oldFile = UPLOAD_DIR.resolve(oldName);
        Files.writeString(oldFile, "old");

        Product existing = product("Laptop cũ", oldName);
        when(productRepository.findById(1L)).thenReturn(java.util.Optional.of(existing));
        when(productRepository.save(existing)).thenReturn(existing);
        ProductDTO update = dto("Laptop mới");
        update.setImage(new MockMultipartFile(
                "image",
                "original.jpg",
                "image/jpeg",
                "new-image".getBytes()));

        ProductDTO result = service.update(1L, update);
        Path newFile = UPLOAD_DIR.resolve(result.getImages());

        try {
            assertThat(result.getImages()).isNotEqualTo("original.jpg");
            assertThat(result.getImages()).endsWith(".jpg");
            assertThat(newFile).exists();
            assertThat(oldFile).doesNotExist();
        } finally {
            Files.deleteIfExists(newFile);
        }
    }

    @Test
    void deletesPhysicalImageAndProduct() throws Exception {
        Files.createDirectories(UPLOAD_DIR);
        String fileName = "test-old-" + System.nanoTime() + ".png";
        Path image = UPLOAD_DIR.resolve(fileName);
        Files.writeString(image, "image");
        Product existing = product("Laptop", fileName);
        when(productRepository.findById(1L)).thenReturn(java.util.Optional.of(existing));

        service.delete(1L);

        assertThat(image).doesNotExist();
        verify(productRepository).delete(existing);
    }

    private Product product(String name, String image) {
        return Product.builder()
                .id(1L)
                .name(name)
                .price(new BigDecimal("100.00"))
                .quantity(2)
                .description("Mô tả")
                .images(image)
                .build();
    }

    private ProductDTO dto(String name) {
        return ProductDTO.builder()
                .id(1L)
                .name(name)
                .price(new BigDecimal("120.00"))
                .quantity(4)
                .description("Mô tả mới")
                .build();
    }

    private void deleteQuietly(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (Exception ignored) {
            // Test cleanup must not hide the assertion result.
        }
    }
}
