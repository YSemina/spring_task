package y.semina.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import y.semina.dto.OrderRequestDto;
import y.semina.exeption.OrderNotFoundException;
import y.semina.model.Customer;
import y.semina.model.Order;
import y.semina.repository.OrderRepository;
import y.semina.service.CustomerService;
import y.semina.service.OrderService;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderJsonConverter orderConverter;
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;
    private final CustomerService customerService;

    @Override
    @Transactional
    public String createOrder(String json) {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        Customer customer = customerService.findCustomerByUsername(username);

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

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isModeratorOrAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MODERATOR") ||
                        a.getAuthority().equals("ROLE_SUPER_ADMIN"));

        if (!isModeratorOrAdmin && !order.getCustomer().getUser().getUsername().equals(username)) {
            throw new OrderNotFoundException("Доступ запрещен: вы можете просматривать только свои заказы");
        }

        try {
            return objectMapper.writeValueAsString(orderConverter.entityToResponseDto(order));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
