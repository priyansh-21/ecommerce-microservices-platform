package com.priyansh.ecommerce.order;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order create(Authentication authentication, @RequestBody CreateOrderRequest request) {
        return orderService.createOrder(authentication.getName(), request.items());
    }

    @GetMapping("/{id}")
    public Order getById(@PathVariable Long id) {
        return orderService.getById(id);
    }

    @GetMapping("/me")
    public List<Order> myOrders(Authentication authentication) {
        return orderService.getForUser(authentication.getName());
    }

    @PutMapping("/{id}/status")
    public Order updateStatus(@PathVariable Long id, @RequestBody UpdateStatusRequest request) {
        return orderService.updateStatus(id, request.status());
    }

    public record CreateOrderRequest(List<OrderService.OrderItemRequest> items) {}
    public record UpdateStatusRequest(Order.Status status) {}
}
