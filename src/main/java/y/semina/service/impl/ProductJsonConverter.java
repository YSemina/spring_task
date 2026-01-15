package y.semina.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;
import y.semina.dto.ProductRequestDto;
import y.semina.dto.ProductResponseDto;
import y.semina.model.Product;
import y.semina.service.AbstractJsonConverter;

@Service
public class ProductJsonConverter extends AbstractJsonConverter<Product, ProductRequestDto, ProductResponseDto> {

    public ProductJsonConverter(ObjectMapper objectMapper, Validator validator) {
        super(objectMapper, validator);
    }

    @Override
    public ProductRequestDto jsonToRequestDto(String json) {
        ProductRequestDto requestDto = null;
        try {
            requestDto = objectMapper.readValue(json, ProductRequestDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        validateRequestDto(requestDto);
        return requestDto;
    }

    @Override
    public Product requestDtoToEntity(ProductRequestDto requestDto) {
        Product product = new Product();
        product.setName(requestDto.getName());
        product.setDescription(requestDto.getDescription());
        product.setPrice(requestDto.getPrice());
        product.setQuantityInStock(requestDto.getQuantityInStock());
        return product;
    }

    @Override
    public ProductResponseDto entityToResponseDto(Product entity) {
        return new ProductResponseDto(
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getQuantityInStock()
        );
    }

}
