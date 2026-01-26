package y.semina.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import y.semina.service.OrderService;

@RestController
@RequestMapping("api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'MODERATOR', 'SUPER_ADMIN')")
    public ResponseEntity<String> createOrder (@RequestBody String request){
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'MODERATOR', 'SUPER_ADMIN')")
    public ResponseEntity<String> findOrderById(@PathVariable Long id){
        return ResponseEntity.ok(orderService.getOrderInfo(id));
    }

}
