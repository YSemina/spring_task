package y.semina.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import y.semina.dto.OrderRequestDto;
import y.semina.dto.OrderResponseDto;
import y.semina.exeption.OrderNotFoundException;
import y.semina.model.Order;
import y.semina.repository.OrderRepository;
import y.semina.service.impl.OrderJsonConverter;
import y.semina.service.impl.OrderServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderJsonConverter orderConverter;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderRequestDto requestDto;
    private Order order;
    private Order savedOrder;
    private OrderResponseDto responseDto;
    private String validJson;

    @BeforeEach
    void setUp() {
        requestDto = OrderRequestDto.builder()
                .customerId(1L)
                .productsId(java.util.Arrays.asList(1L, 2L))
                .shippingAddress("ул. Тестовая, 10")
                .build();

        order = new Order();
        order.setOrderId(1L);
        order.setOrderDate(LocalDate.now());
        order.setShippingAddress("ул. Тестовая, 10");
        order.setTotalPrice(new BigDecimal("9999.99"));

        savedOrder = new Order();
        savedOrder.setOrderId(1L);

        responseDto = OrderResponseDto.builder()
                .orderDate(LocalDate.now())
                .shippingAddress("ул. Тестовая, 10")
                .totalPrice(new BigDecimal("9999.99"))
                .build();

        validJson = """
            {
                "customer_id": 1,
                "products_id": [1, 2],
                "shipping_address": "ул. Тестовая, 10"
            }
            """;
    }

    @Test
    @DisplayName("Создание заказа - успешный JSON парсинг")
    void createOrder_SuccessfulJsonParsing() throws JsonProcessingException {
        when(orderConverter.jsonToRequestDto(validJson)).thenReturn(requestDto);
        when(orderConverter.requestDtoToEntity(requestDto)).thenReturn(order);
        when(orderRepository.save(order)).thenReturn(savedOrder);
        when(orderConverter.entityToResponseDto(savedOrder)).thenReturn(responseDto);
        when(objectMapper.writeValueAsString(responseDto)).thenReturn("{\"order_id\": 1}");

        String result = orderService.createOrder(validJson);

        assertNotNull(result);
        assertEquals("{\"order_id\": 1}", result);

        verify(orderConverter).jsonToRequestDto(validJson);
        verify(orderConverter).requestDtoToEntity(requestDto);
        verify(orderRepository).save(order);
        verify(orderConverter).entityToResponseDto(savedOrder);
        verify(objectMapper).writeValueAsString(responseDto);
    }

    @Test
    @DisplayName("Создание заказа - ошибка JSON парсинга в ObjectMapper")
    void createOrder_ObjectMapperThrowsException() throws JsonProcessingException {
        when(orderConverter.jsonToRequestDto(validJson)).thenReturn(requestDto);
        when(orderConverter.requestDtoToEntity(requestDto)).thenReturn(order);
        when(orderRepository.save(order)).thenReturn(savedOrder);
        when(orderConverter.entityToResponseDto(savedOrder)).thenReturn(responseDto);
        when(objectMapper.writeValueAsString(responseDto))
                .thenThrow(new JsonProcessingException("JSON error") {});

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(validJson);
        });

        assertTrue(exception.getCause() instanceof JsonProcessingException);
        assertEquals("JSON error", exception.getCause().getMessage());
    }

    @Test
    @DisplayName("Получение информации о заказе - успешный JSON сериализация")
    void getOrderInfo_SuccessfulJsonSerialization() throws JsonProcessingException {
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderConverter.entityToResponseDto(order)).thenReturn(responseDto);
        when(objectMapper.writeValueAsString(responseDto)).thenReturn("{\"order_id\": 1}");

        String result = orderService.getOrderInfo(orderId);

        assertNotNull(result);
        assertEquals("{\"order_id\": 1}", result);

        verify(orderRepository).findById(orderId);
        verify(orderConverter).entityToResponseDto(order);
        verify(objectMapper).writeValueAsString(responseDto);
    }

    @Test
    @DisplayName("Получение информации о заказе - заказ не найден")
    void getOrderInfo_OrderNotFound() throws JsonProcessingException {
        Long orderId = 999L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> {
            orderService.getOrderInfo(orderId);
        });

        assertTrue(exception.getMessage().contains("999"));
        verify(orderRepository).findById(orderId);
        verify(orderConverter, never()).entityToResponseDto(any());
        verify(objectMapper, never()).writeValueAsString(any());
    }

    @Test
    @DisplayName("Получение информации о заказе - ошибка сериализации в JSON")
    void getOrderInfo_JsonSerializationError() throws JsonProcessingException {
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderConverter.entityToResponseDto(order)).thenReturn(responseDto);
        when(objectMapper.writeValueAsString(responseDto))
                .thenThrow(new JsonProcessingException("Serialization error") {});

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.getOrderInfo(orderId);
        });

        assertTrue(exception.getCause() instanceof JsonProcessingException);
        assertEquals("Serialization error", exception.getCause().getMessage());
    }

    @Test
    @DisplayName("Тестирование разных форматов JSON - snake_case")
    void testSnakeCaseJsonFormat() throws JsonProcessingException {
        ObjectMapper snakeMapper = new ObjectMapper();
        snakeMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

        String snakeJson = """
            {
                "customer_id": 1,
                "products_id": [1, 2, 3],
                "shipping_address": "Test address"
            }
            """;

        OrderRequestDto dto = snakeMapper.readValue(snakeJson, OrderRequestDto.class);

        assertNotNull(dto);
        assertEquals(1L, dto.getCustomerId());
        assertEquals(3, dto.getProductsId().size());
        assertEquals("Test address", dto.getShippingAddress());
    }

    @Test
    @DisplayName("Тестирование разных форматов JSON - camelCase")
    void testCamelCaseJsonFormat() throws JsonProcessingException {
        ObjectMapper camelMapper = new ObjectMapper();
        camelMapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);

        String camelJson = """
            {
                "customerId": 1,
                "productsId": [1, 2, 3],
                "shippingAddress": "Test address"
            }
            """;

        OrderRequestDto dto = camelMapper.readValue(camelJson, OrderRequestDto.class);

        assertNotNull(dto);
        assertEquals(1L, dto.getCustomerId());
        assertEquals(3, dto.getProductsId().size());
        assertEquals("Test address", dto.getShippingAddress());
    }

}
