package vn.iotstar.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import vn.iotstar.dto.ProductDTO;
import vn.iotstar.entity.Product;

class ProductMapperTest {

    private final ProductMapper mapper = new ProductMapper();

    @Test
    void mapsEntityAndDtoInBothDirections() {
        Product entity = Product.builder()
                .id(1L)
                .name("Laptop")
                .price(new BigDecimal("1500.50"))
                .quantity(3)
                .description("Máy tính xách tay")
                .images("product.jpg")
                .build();

        ProductDTO dto = mapper.toDTO(entity);
        Product mappedBack = mapper.toEntity(dto);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getImages()).isEqualTo("product.jpg");
        assertThat(mappedBack.getName()).isEqualTo("Laptop");
        assertThat(mappedBack.getDescription()).isEqualTo("Máy tính xách tay");
    }

    @Test
    void updatesExistingEntityInsteadOfCreatingAnotherOne() {
        Product existing = Product.builder()
                .id(7L)
                .name("Tên cũ")
                .price(BigDecimal.ONE)
                .quantity(1)
                .images("old.jpg")
                .build();
        ProductDTO dto = ProductDTO.builder()
                .name("Tên mới")
                .price(BigDecimal.TEN)
                .quantity(5)
                .description("Mô tả mới")
                .images("new.jpg")
                .build();

        mapper.updateEntity(dto, existing);

        assertThat(existing.getId()).isEqualTo(7L);
        assertThat(existing.getName()).isEqualTo("Tên mới");
        assertThat(existing.getImages()).isEqualTo("new.jpg");
    }
}
