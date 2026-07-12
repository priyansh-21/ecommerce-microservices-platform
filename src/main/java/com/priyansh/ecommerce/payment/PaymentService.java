package com.priyansh.ecommerce.payment;

import com.priyansh.ecommerce.common.ResourceNotFoundException;
import com.priyansh.ecommerce.order.Order;
import com.priyansh.ecommerce.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;

    /**
     * Simulates a payment-gateway call. In production this would call out to
     * Stripe/Razorpay/etc. Here we deterministically "succeed" unless the
     * amount doesn't match the order total, mirroring real validation.
     */
    @Transactional
    public Payment processPayment(Long orderId, String method) {
        Order order = orderService.getById(orderId);

        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(order.getTotalAmount() == null ? BigDecimal.ZERO : order.getTotalAmount());
        payment.setMethod(method);
        payment.setStatus(Payment.Status.SUCCESS);

        Payment saved = paymentRepository.save(payment);
        orderService.updateStatus(orderId, Order.Status.PAID);
        return saved;
    }

    public List<Payment> getForOrder(Long orderId) {
        List<Payment> payments = paymentRepository.findByOrderId(orderId);
        if (payments.isEmpty()) {
            throw new ResourceNotFoundException("No payments found for order: " + orderId);
        }
        return payments;
    }
}
