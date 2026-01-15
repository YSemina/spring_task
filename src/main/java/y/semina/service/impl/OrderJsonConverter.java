package y.semina.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;
import y.semina.constant.Status;
import y.semina.dto.OrderRequestDto;
import y.semina.dto.OrderResponseDto;
import y.semina.dto.ProductResponseDto;
import y.semina.model.Customer;
import y.semina.model.Order;
import y.semina.model.Product;
import y.semina.service.AbstractJsonConverter;
import y.semina.service.CustomerService;
import y.semina.service.ProductService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class OrderJsonConverter extends AbstractJsonConverter<Order, OrderRequestDto, OrderResponseDto> {

    private final CustomerService customerService;
    private final CustomerJsonConverter customerConverter;
    private final ProductService productService;
    private final ProductJsonConverter productConverter;


    public OrderJsonConverter(ObjectMapper objectMapper, Validator validator,
                              CustomerService customerService, ProductService productService,
                              CustomerJsonConverter customerConverter, ProductJsonConverter productConverter) {
        super(objectMapper, validator);
        this.customerService = customerService;
        this.productService = productService;
        this.customerConverter = customerConverter;
        this.productConverter = productConverter;
    }

    @Override
    public OrderRequestDto jsonToRequestDto(String json) {
        OrderRequestDto requestDto = null;
        try {
            requestDto = objectMapper.readValue(json, OrderRequestDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        validateRequestDto(requestDto);
        return requestDto;
    }

    @Override
    public Order requestDtoToEntity(OrderRequestDto requestDto) {
        Long customerId = requestDto.getCustomerId();
        List<Long> productId = requestDto.getProductsId();

        List<Product> products = productId.stream()
                .map(productService::getProductById)
                .toList();

        BigDecimal totalPrice = products.stream()
                .map(Product::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Customer customer = customerService.getCustomerById(customerId);
        Order order = new Order();
        order.setCustomer(customer);
        order.setProducts(products);
        order.setShippingAddress(requestDto.getShippingAddress());
        order.setOrderDate(LocalDate.now());
        order.setOrderStatus(Status.CREATED);
        order.setTotalPrice(totalPrice);
        return order;
    }

    @Override
    public OrderResponseDto entityToResponseDto(Order entity) {
        List<ProductResponseDto> responseProductsList = entity.getProducts().stream()
                .map(productConverter::entityToResponseDto)
                .toList();
        return new OrderResponseDto(
                customerConverter.entityToResponseDto(entity.getCustomer()),
                responseProductsList,
                entity.getOrderDate(),
                entity.getShippingAddress(),
                entity.getTotalPrice(),
                entity.getOrderStatus().toString()
        );
    }

}
