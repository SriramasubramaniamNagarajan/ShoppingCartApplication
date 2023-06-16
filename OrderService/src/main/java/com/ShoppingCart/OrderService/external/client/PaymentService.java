package com.ShoppingCart.OrderService.external.client;

import com.ShoppingCart.OrderService.exception.CustomException;
import com.ShoppingCart.OrderService.external.request.PaymentRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
@CircuitBreaker(name = "external", fallbackMethod = "fallback")
@FeignClient(name = "PAYMENT-SERVICE/payment")
public interface PaymentService {
    @PostMapping
    public ResponseEntity<Long> doPayment(@RequestBody PaymentRequest paymentRequest);
    default void fallback(Exception exception){
        throw new CustomException(
                "Payment Service is not Available",
                "UNAVAILABLE",
                500
                );
    }
}
