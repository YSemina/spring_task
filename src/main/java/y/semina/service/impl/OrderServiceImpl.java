package y.semina.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import y.semina.dto.OrderRequestDto;
import y.semina.exeption.OrderNotFoundException;
import y.semina.model.Order;
import y.semina.repository.OrderRepository;
import y.semina.service.OrderService;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderJsonConverter orderConverter;
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public String createOrder(String json) {
        OrderRequestDto requestDto = orderConverter.jsonToRequestDto(json);
        Order order = orderConverter.requestDtoToEntity(requestDto);
        Order savedOrder = orderRepository.save(order);
        try {
            return objectMapper.writeValueAsString(orderConverter.entityToResponseDto(savedOrder));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getOrderInfo(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Заказа с id " + id + " не существует!"));
        try {
            return objectMapper.writeValueAsString(orderConverter.entityToResponseDto(order));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
