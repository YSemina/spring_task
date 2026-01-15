package y.semina.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import y.semina.dto.ProductRequestDto;
import y.semina.dto.ProductResponseDto;
import y.semina.exeption.ProductNotFoundException;
import y.semina.model.Product;
import y.semina.repository.ProductRepository;
import y.semina.service.ProductService;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductJsonConverter converterProduct;
    private final ObjectMapper objectMapper;

    @Override
    public ProductResponseDto findProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Товара с id " + id + " не существует."));
        return converterProduct.entityToResponseDto(product);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Товара с id " + id + " не существует."));
    }

    @Override
    @SneakyThrows
    public String findProducts(){
        List<Product> products = productRepository.findAll();
        List<ProductResponseDto> list = products.stream()
                .map(converterProduct::entityToResponseDto)
                .toList();
        return objectMapper.writeValueAsString(list);
    }

    @Override
    public String getStringProductById(Long id) {
        try {
            return objectMapper.writeValueAsString(findProductById(id));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public String createProduct (String jsonRequest){
        ProductRequestDto requestDto = converterProduct.jsonToRequestDto(jsonRequest);
        Product product = converterProduct.requestDtoToEntity(requestDto);
        ProductResponseDto responseDto = converterProduct.entityToResponseDto(productRepository.save(product));
        try {
            return objectMapper.writeValueAsString(responseDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    @Override
    public String updateProduct(
            Long id,
            String name,
            String description,
            BigDecimal price,
            Integer quantity_in_stock
    ){
        Product product = getProductById(id);
        if(name!=null)
            product.setName(name);
        if(description!=null)
            product.setDescription(description);
        if(price!=null)
            product.setPrice(price);
        if(quantity_in_stock!=null)
            product.setQuantityInStock(quantity_in_stock);
        ProductResponseDto responseDto = converterProduct.entityToResponseDto(productRepository.save(product));
        try {
            return objectMapper.writeValueAsString(responseDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void deleteProduct(Long id){
        Product product = getProductById(id);
        productRepository.delete(product);
    }

}
