package y.semina.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import y.semina.dto.ProductRequestDto;
import y.semina.dto.ProductResponseDto;
import y.semina.exeption.ProductNotFoundException;
import y.semina.model.Product;
import y.semina.repository.ProductRepository;
import y.semina.service.impl.ProductJsonConverter;
import y.semina.service.impl.ProductServiceImpl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductJsonConverter converterProduct;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductResponseDto responseDto;
    private ProductRequestDto requestDto;
    private String validJson;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setProductId(1L);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("99.99"));
        product.setQuantityInStock(10);

        responseDto = ProductResponseDto.builder()
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("99.99"))
                .quantityInStock(10)
                .build();

        requestDto = ProductRequestDto.builder()
                .name("New Product")
                .description("New Description")
                .price(new BigDecimal("149.99"))
                .quantityInStock(20)
                .build();

        validJson = """
            {
                "name": "New Product",
                "description": "New Description",
                "price": 149.99,
                "quantity_in_stock": 20
            }
            """;
    }

    @Test
    @DisplayName("findProducts - успешная сериализация списка в JSON")
    void findProducts_Success() throws JsonProcessingException {
        List<Product> products = Arrays.asList(product, product);
        List<ProductResponseDto> dtos = Arrays.asList(responseDto, responseDto);
        String expectedJson = "[{\"name\":\"Test Product\"},{\"name\":\"Test Product\"}]";

        when(productRepository.findAll()).thenReturn(products);
        when(converterProduct.entityToResponseDto(any(Product.class))).thenReturn(responseDto);
        when(objectMapper.writeValueAsString(dtos)).thenReturn(expectedJson);

        String result = productService.findProducts();

        assertNotNull(result);
        assertEquals(expectedJson, result);
        verify(productRepository).findAll();
        verify(converterProduct, times(2)).entityToResponseDto(any(Product.class));
        verify(objectMapper).writeValueAsString(dtos);
    }

    @Test
    @DisplayName("findProducts - пустой список продуктов")
    void findProducts_EmptyList() throws JsonProcessingException {
        when(productRepository.findAll()).thenReturn(Collections.emptyList());
        when(objectMapper.writeValueAsString(Collections.emptyList())).thenReturn("[]");

        String result = productService.findProducts();

        assertEquals("[]", result);
        verify(productRepository).findAll();
        verify(converterProduct, never()).entityToResponseDto(any());
        verify(objectMapper).writeValueAsString(Collections.emptyList());
    }

    @Test
    @DisplayName("findProducts - ошибка сериализации ObjectMapper")
    void findProducts_JsonSerializationError() throws JsonProcessingException {
        List<Product> products = Arrays.asList(product);
        List<ProductResponseDto> dtos = Arrays.asList(responseDto);

        when(productRepository.findAll()).thenReturn(products);
        when(converterProduct.entityToResponseDto(any(Product.class))).thenReturn(responseDto);
        when(objectMapper.writeValueAsString(dtos))
                .thenThrow(new JsonProcessingException("Serialization error") {});

        assertThrows(JsonProcessingException.class, () -> {
            productService.findProducts();
        });
    }

    @Test
    @DisplayName("findProductById - успешное нахождение продукта")
    void findProductById_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(converterProduct.entityToResponseDto(product)).thenReturn(responseDto);

        ProductResponseDto result = productService.findProductById(1L);

        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        verify(productRepository).findById(1L);
        verify(converterProduct).entityToResponseDto(product);
    }

    @Test
    @DisplayName("findProductById - продукт не найден")
    void findProductById_NotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> {
            productService.findProductById(999L);
        });

        assertTrue(exception.getMessage().contains("999"));
        verify(productRepository).findById(999L);
        verify(converterProduct, never()).entityToResponseDto(any());
    }

    @Test
    @DisplayName("getStringProductById - успешная сериализация в JSON")
    void getStringProductById_Success() throws JsonProcessingException {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(converterProduct.entityToResponseDto(product)).thenReturn(responseDto);
        when(objectMapper.writeValueAsString(responseDto)).thenReturn("{\"name\":\"Test Product\"}");

        String result = productService.getStringProductById(1L);

        assertNotNull(result);
        assertEquals("{\"name\":\"Test Product\"}", result);
        verify(productRepository).findById(1L);
        verify(converterProduct).entityToResponseDto(product);
        verify(objectMapper).writeValueAsString(responseDto);
    }

    @Test
    @DisplayName("getStringProductById - ошибка сериализации")
    void getStringProductById_SerializationError() throws JsonProcessingException {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(converterProduct.entityToResponseDto(product)).thenReturn(responseDto);
        when(objectMapper.writeValueAsString(responseDto))
                .thenThrow(new JsonProcessingException("Error") {});

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.getStringProductById(1L);
        });

        assertTrue(exception.getCause() instanceof JsonProcessingException);
        verify(objectMapper).writeValueAsString(responseDto);
    }

    @Test
    @DisplayName("createProduct - успешное создание из JSON")
    void createProduct_Success() throws JsonProcessingException {
        Product savedProduct = new Product();
        savedProduct.setProductId(1L);

        when(converterProduct.jsonToRequestDto(validJson)).thenReturn(requestDto);
        when(converterProduct.requestDtoToEntity(requestDto)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(savedProduct);
        when(converterProduct.entityToResponseDto(savedProduct)).thenReturn(responseDto);
        when(objectMapper.writeValueAsString(responseDto)).thenReturn("{\"id\":1}");

        String result = productService.createProduct(validJson);

        assertNotNull(result);
        assertEquals("{\"id\":1}", result);

        verify(converterProduct).jsonToRequestDto(validJson);
        verify(converterProduct).requestDtoToEntity(requestDto);
        verify(productRepository).save(product);
        verify(converterProduct).entityToResponseDto(savedProduct);
        verify(objectMapper).writeValueAsString(responseDto);
    }

    @Test
    @DisplayName("updateProduct - частичное обновление (только имя)")
    void updateProduct_PartialUpdateNameOnly() throws JsonProcessingException {
        Long productId = 1L;
        String newName = "Updated Name";
        String newDescription = null;
        BigDecimal newPrice = null;
        Integer newQuantity = null;

        Product updatedProduct = new Product();
        updatedProduct.setProductId(productId);
        updatedProduct.setName(newName);
        updatedProduct.setDescription("Test Description");
        updatedProduct.setPrice(new BigDecimal("99.99"));
        updatedProduct.setQuantityInStock(10);

        ProductResponseDto updatedResponse = ProductResponseDto.builder()
                .name(newName)
                .description("Test Description")
                .price(new BigDecimal("99.99"))
                .quantityInStock(10)
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);
        when(converterProduct.entityToResponseDto(updatedProduct)).thenReturn(updatedResponse);
        when(objectMapper.writeValueAsString(updatedResponse)).thenReturn("{\"name\":\"Updated Name\"}");

        String result = productService.updateProduct(productId, newName, newDescription, newPrice, newQuantity);

        assertNotNull(result);
        assertTrue(result.contains("Updated Name"));

        verify(productRepository).findById(productId);
        verify(productRepository).save(argThat(p ->
                p.getName().equals("Updated Name") &&
                        p.getDescription().equals("Test Description")
        ));
        verify(converterProduct).entityToResponseDto(updatedProduct);
        verify(objectMapper).writeValueAsString(updatedResponse);
    }

    @Test
    @DisplayName("updateProduct - полное обновление всех полей")
    void updateProduct_FullUpdate() throws JsonProcessingException {
        Long productId = 1L;
        String newName = "New Name";
        String newDescription = "New Description";
        BigDecimal newPrice = new BigDecimal("199.99");
        Integer newQuantity = 50;

        Product updatedProduct = new Product();
        updatedProduct.setProductId(productId);
        updatedProduct.setName(newName);
        updatedProduct.setDescription(newDescription);
        updatedProduct.setPrice(newPrice);
        updatedProduct.setQuantityInStock(newQuantity);

        ProductResponseDto updatedResponse = ProductResponseDto.builder()
                .name(newName)
                .description(newDescription)
                .price(newPrice)
                .quantityInStock(newQuantity)
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);
        when(converterProduct.entityToResponseDto(updatedProduct)).thenReturn(updatedResponse);
        when(objectMapper.writeValueAsString(updatedResponse)).thenReturn("{\"name\":\"New Name\"}");

        String result = productService.updateProduct(productId, newName, newDescription, newPrice, newQuantity);

        assertNotNull(result);

        verify(productRepository).save(argThat(p ->
                p.getName().equals("New Name") &&
                        p.getDescription().equals("New Description") &&
                        p.getPrice().equals(new BigDecimal("199.99")) &&
                        p.getQuantityInStock() == 50
        ));
    }

    @Test
    @DisplayName("updateProduct - обновление без параметров (ничего не меняется)")
    void updateProduct_NoParameters() throws JsonProcessingException {
        Long productId = 1L;

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(converterProduct.entityToResponseDto(product)).thenReturn(responseDto);
        when(objectMapper.writeValueAsString(responseDto)).thenReturn("{\"name\":\"Test Product\"}");

        String result = productService.updateProduct(productId, null, null, null, null);

        assertNotNull(result);

        verify(productRepository).save(argThat(p ->
                p.getName().equals("Test Product") &&
                        p.getDescription().equals("Test Description") &&
                        p.getPrice().equals(new BigDecimal("99.99")) &&
                        p.getQuantityInStock() == 10
        ));
    }

    @Test
    @DisplayName("updateProduct - продукт не найден")
    void updateProduct_ProductNotFound() throws JsonProcessingException {
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> {
            productService.updateProduct(productId, "New Name", null, null, null);
        });

        verify(productRepository, never()).save(any());
        verify(converterProduct, never()).entityToResponseDto(any());
        verify(objectMapper, never()).writeValueAsString(any());
    }

    @Test
    @DisplayName("updateProduct - ошибка сериализации после обновления")
    void updateProduct_SerializationError() throws JsonProcessingException {
        Long productId = 1L;

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(converterProduct.entityToResponseDto(product)).thenReturn(responseDto);
        when(objectMapper.writeValueAsString(responseDto))
                .thenThrow(new JsonProcessingException("Error") {});

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.updateProduct(productId, "New Name", null, null, null);
        });

        assertTrue(exception.getCause() instanceof JsonProcessingException);
    }

    @Test
    @DisplayName("deleteProduct - успешное удаление")
    void deleteProduct_Success() {
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        productService.deleteProduct(productId);

        verify(productRepository).findById(productId);
        verify(productRepository).delete(product);
    }

    @Test
    @DisplayName("deleteProduct - продукт не найден")
    void deleteProduct_NotFound() {
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> {
            productService.deleteProduct(productId);
        });

        verify(productRepository).findById(productId);
        verify(productRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Тестирование разных форматов BigDecimal в JSON")
    void testBigDecimalJsonFormats() throws JsonProcessingException {
        ObjectMapper testMapper = new ObjectMapper();

        String json1 = "{\"price\": 99.99}";
        assertEquals(99.99, testMapper.readTree(json1).get("price").asDouble(), 0.001);

        String json2 = "{\"price\": \"99.99\"}";
        assertEquals(99.99, testMapper.readTree(json2).get("price").asDouble(), 0.001);

        String json3 = "{\"price\": 99.999999}";
        assertEquals(99.999999, testMapper.readTree(json3).get("price").asDouble(), 0.000001);
    }

    @Test
    @DisplayName("Тестирование обработки null и пустых строк в параметрах")
    void testNullAndEmptyParameters() throws JsonProcessingException {
        Long productId = 1L;
        String emptyName = "";
        String nullDescription = null;

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(converterProduct.entityToResponseDto(product)).thenReturn(responseDto);
        when(objectMapper.writeValueAsString(responseDto)).thenReturn("{}");

        String result = productService.updateProduct(productId, emptyName, nullDescription, null, null);

        verify(productRepository).save(argThat(p ->
                p.getName().equals("") &&
                        p.getDescription().equals("Test Description")
        ));
    }

}
