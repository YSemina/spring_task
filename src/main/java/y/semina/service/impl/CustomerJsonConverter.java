package y.semina.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;
import y.semina.dto.CustomerRequestDto;
import y.semina.dto.CustomerResponseDto;
import y.semina.model.Customer;
import y.semina.service.AbstractJsonConverter;

import java.util.ArrayList;

@Service
public class CustomerJsonConverter extends AbstractJsonConverter <Customer, CustomerRequestDto, CustomerResponseDto> {

    public CustomerJsonConverter(ObjectMapper objectMapper, Validator validator) {
        super(objectMapper, validator);
    }

    @Override
    public CustomerRequestDto jsonToRequestDto(String json) {
        CustomerRequestDto requestDto = null;
        try {
            requestDto = objectMapper.readValue(json, CustomerRequestDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        validateRequestDto(requestDto);
        return requestDto;
    }

    @Override
    public Customer requestDtoToEntity(CustomerRequestDto requestDto) {
        Customer customer = new Customer();
        customer.setFirstName(requestDto.getFirstName());
        customer.setLastName(requestDto.getLastName());
        customer.setEmail(requestDto.getEmail());
        customer.setContactNumber(Long.parseLong(requestDto.getContactNumber()));
        customer.setOrders(new ArrayList<>());
        return customer;
    }

    @Override
    public CustomerResponseDto entityToResponseDto(Customer entity) {
        return new CustomerResponseDto(
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getContactNumber()
        );
    }

}
