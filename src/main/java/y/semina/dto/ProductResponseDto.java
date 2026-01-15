package y.semina.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDto {

    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantityInStock;

}
