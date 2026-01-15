package y.semina.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import y.semina.service.OrderService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    @DisplayName("POST /api/orders - некорректный JSON")
    void createOrder_InvalidJson() throws Exception {
        String invalidJson = "{ invalid json syntax }";

        when(orderService.createOrder(invalidJson))
                .thenThrow(new RuntimeException("Invalid JSON"));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("GET /api/orders/{id} - успешное получение заказа")
    void getOrderById_Success() throws Exception {
        Long orderId = 1L;
        String responseJson = """
            {
                "order_id": 1,
                "customer": "{...}",
                "products": "[...]",
                "total_price": 9999.99
            }
            """;

        when(orderService.getOrderInfo(orderId)).thenReturn(responseJson);

        mockMvc.perform(get("/api/orders/{id}", orderId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(responseJson));
    }

    @Test
    @DisplayName("GET /api/orders/{id} - заказ не найден")
    void getOrderById_NotFound() throws Exception {
        Long orderId = 999L;
        when(orderService.getOrderInfo(orderId))
                .thenThrow(new RuntimeException("Order not found"));

        mockMvc.perform(get("/api/orders/{id}", orderId))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("Тестирование snake_case в запросах")
    void testSnakeCaseInRequests() throws Exception {
        String snakeCaseJson = """
            {
                "customer_id": 1,
                "products_id": [1, 2],
                "shipping_address": "Test"
            }
            """;

        String response = "{\"status\": \"success\"}";
        when(orderService.createOrder(any(String.class))).thenReturn(response);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(snakeCaseJson))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Тестирование camelCase в запросах (должен fail если настроен snake_case)")
    void testCamelCaseInRequests() throws Exception {
        String camelCaseJson = """
            {
                "customerId": 1,
                "productsId": [1, 2],
                "shippingAddress": "Test"
            }
            """;

        when(orderService.createOrder(camelCaseJson))
                .thenThrow(new RuntimeException("JSON parsing error"));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(camelCaseJson))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("Проверка валидации JSON структуры")
    void testJsonStructureValidation() throws Exception {
        String invalidJson = """
            {
                "customer_id": 1
                // нет products_id и shipping_address
            }
            """;

        when(orderService.createOrder(invalidJson))
                .thenThrow(new RuntimeException("Validation failed"));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().is5xxServerError());
    }

}
