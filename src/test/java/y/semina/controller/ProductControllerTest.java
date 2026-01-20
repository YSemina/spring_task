package y.semina.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import y.semina.config.SecurityConfig;
import y.semina.service.ProductService;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import(SecurityConfig.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    @WithAnonymousUser
    @DisplayName("GET /api/products - успешное получение всех продуктов")
    void getAllProducts_Success() throws Exception {
        String responseJson = """
            [
                {
                    "name": "Product 1",
                    "price": 99.99
                },
                {
                    "name": "Product 2",
                    "price": 149.99
                }
            ]
            """;

        when(productService.findProducts()).thenReturn(responseJson);

        mockMvc.perform(get("/api/products")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(responseJson));
    }

    @Test
    @WithMockUser(username = "Test", roles = "USER")
    @DisplayName("GET /api/products/{id} - успешное получение продукта по ID")
    void getProductById_Success() throws Exception {
        Long productId = 1L;
        String responseJson = """
            {
                "name": "Test Product",
                "price": 99.99,
                "quantity_in_stock": 10
            }
            """;

        when(productService.getStringProductById(productId)).thenReturn(responseJson);

        mockMvc.perform(get("/api/products/{id}", productId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(responseJson));
    }

    @Test
    @WithMockUser(username = "Test", roles = "USER")
    @DisplayName("GET /api/products/{id} - продукт не найден")
    void getProductById_NotFound() throws Exception {
        Long productId = 999L;
        when(productService.getStringProductById(productId))
                .thenThrow(new RuntimeException("Product not found"));

        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @WithMockUser(username = "Test", roles = "USER")
    @DisplayName("POST /api/products - пустое тело запроса")
    void createProduct_EmptyBody() throws Exception {
        when(productService.createProduct(""))
                .thenThrow(new RuntimeException("Empty JSON"));

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @WithMockUser(username = "test-admin", roles = "ADMIN")
    @DisplayName("PUT /api/products/{id} - успешное полное обновление")
    void updateProduct_FullUpdate() throws Exception {
        Long productId = 1L;
        String responseJson = "{\"name\":\"Updated\"}";

        when(productService.updateProduct(
                eq(productId),
                eq("Updated Product"),
                eq("Updated Description"),
                eq(new BigDecimal("199.99")),
                eq(50)
        )).thenReturn(responseJson);

        mockMvc.perform(put("/api/products/{id}", productId)
                        .param("name", "Updated Product")
                        .param("description", "Updated Description")
                        .param("price", "199.99")
                        .param("quantity_in_stock", "50"))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    @WithMockUser(username = "test-admin", roles = "ADMIN")
    @DisplayName("PUT /api/products/{id} - обновление без параметров")
    void updateProduct_NoParameters() throws Exception {
        Long productId = 1L;
        String responseJson = "{\"name\":\"Same Product\"}";

        when(productService.updateProduct(eq(productId), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(responseJson);

        mockMvc.perform(put("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    @WithMockUser(username = "test-admin", roles = "ADMIN")
    @DisplayName("PUT /api/products/{id} - проверка параметров с разными типами")
    void updateProduct_DifferentParameterTypes() throws Exception {
        Long productId = 1L;

        mockMvc.perform(put("/api/products/{id}", productId)
                        .param("price", "99.99"))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/products/{id}", productId)
                        .param("quantity_in_stock", "100"))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/products/{id}", productId)
                        .param("description", ""))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test-admin", roles = "ADMIN")
    @DisplayName("DELETE /api/products/{id} - успешное удаление")
    void deleteProduct_Success() throws Exception {
        Long productId = 1L;

        mockMvc.perform(delete("/api/products/{id}", productId))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "test-admin", roles = "ADMIN")
    @DisplayName("DELETE /api/products/{id} - продукт не найден")
    void deleteProduct_NotFound() throws Exception {
        Long productId = 999L;

        org.mockito.Mockito.doThrow(new RuntimeException("Not found"))
                .when(productService).deleteProduct(productId);

        mockMvc.perform(delete("/api/products/{id}", productId))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @WithMockUser(username = "test-admin", roles = "ADMIN")
    @DisplayName("Тестирование snake_case vs camelCase в параметрах")
    void testParameterNaming() throws Exception {
        Long productId = 1L;

        mockMvc.perform(put("/api/products/{id}", productId)
                        .param("quantity_in_stock", "100"))
                .andExpect(status().isOk());
    }

}
