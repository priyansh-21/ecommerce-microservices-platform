package com.priyansh.ecommerce.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Payment pay(@RequestBody PaymentRequest request) {
        return paymentService.processPayment(request.orderId(), request.method());
    }

    @GetMapping("/order/{orderId}")
    public List<Payment> getForOrder(@PathVariable Long orderId) {
        return paymentService.getForOrder(orderId);
    }

    public record PaymentRequest(Long orderId, String method) {}
}
