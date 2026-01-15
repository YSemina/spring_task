package y.semina.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class ProductRequestDto {

    @NotBlank(message = "Поле name для продукта не может быть пустым.")
    @Size(min = 3, max = 50, message = "Название продукта должно содержать от 3 до 50 символов.")
    private String name;

    @Size(max = 300, message = "Описание продукта не должно превышать 300 символов.")
    private String description;

    @NotNull(message = "Цена - обязательный параметр.")
    @DecimalMin(value = "0.01", message = "Стоимость товара должна быть больше нуля.")
    private BigDecimal price;

    @NotNull(message = "Количество товара не может быть null.")
    @Min(value = 0, message = "Количество товара не может быть меньше нуля.")
    private Integer quantityInStock;

}
