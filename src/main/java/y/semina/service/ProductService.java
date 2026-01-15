package y.semina.service;

import y.semina.dto.ProductResponseDto;
import y.semina.model.Product;

import java.math.BigDecimal;

public interface ProductService {

    ProductResponseDto findProductById (Long id);

    Product getProductById(Long id);

    String findProducts();

    String getStringProductById(Long id);

    String createProduct (String jsonRequest);

    String updateProduct(
            Long id,
            String name,
            String description,
            BigDecimal price,
            Integer quantity_in_stock
    );
    void deleteProduct(Long id);

}
