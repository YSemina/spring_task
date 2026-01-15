package y.semina.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDto {

    private CustomerResponseDto customer;
    private List<ProductResponseDto> products;
    private LocalDate orderDate;
    private String shippingAddress;
    private BigDecimal totalPrice;
    private String orderStatus;

}
