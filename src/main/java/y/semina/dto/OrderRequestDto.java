package y.semina.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDto {

    @NotNull(message = "Поле customer_id не может быть null!")
    @Positive(message = "Поле customer_id должно быть положительным.")
    private Long customerId;

    @NotNull(message = "Поле products_id не может быть null!")
    @Size(min = 1, message = "Должен быть как минимум один продукт.")
    private List<Long> productsId;

    @NotBlank(message = "Поле shipping_address не может быть пустым!")
    @Size(min = 10, max = 100, message = "Для адреса допустимо от 10 до 100 символов.")
    private String shippingAddress;

}
