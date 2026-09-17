package vn.iotstar.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Set;

import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

class ProductDTOValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void rejectsBlankNameNegativePriceAndNegativeQuantity() {
        ProductDTO dto = ProductDTO.builder()
                .name(" ")
                .price(new BigDecimal("-1"))
                .quantity(-1)
                .build();

        Set<ConstraintViolation<ProductDTO>> violations = validator.validate(dto);

        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("name", "price", "quantity");
    }
}
