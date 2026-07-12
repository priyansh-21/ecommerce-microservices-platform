package com.priyansh.ecommerce.order;

import com.priyansh.ecommerce.common.BadRequestException;
import com.priyansh.ecommerce.common.ResourceNotFoundException;
import com.priyansh.ecommerce.product.Product;
import com.priyansh.ecommerce.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;

    @Transactional
    public Order createOrder(String username, List<OrderItemRequest> requestItems) {
        if (requestItems == null || requestItems.isEmpty()) {
            throw new BadRequestException("Order must contain at least one item");
        }

        Order order = new Order();
        order.setUsername(username);
        order.setStatus(Order.Status.CREATED);

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest req : requestItems) {
            Product product = productService.getById(req.productId());

            productService.reduceStock(req.productId(), req.quantity());

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setQuantity(req.quantity());
            item.setPriceAtPurchase(product.getPrice());
            order.getItems().add(item);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(req.quantity())));
        }

        order.setTotalAmount(total);
        return orderRepository.save(order);
    }

    public Order getById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
    }

    public List<Order> getForUser(String username) {
        return orderRepository.findByUsername(username);
    }

    public Order updateStatus(Long id, Order.Status status) {
        Order order = getById(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public record OrderItemRequest(Long productId, Integer quantity) {}
}
