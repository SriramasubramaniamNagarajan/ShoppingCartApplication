package com.ShoppingCart.PaymentService.service;

import com.ShoppingCart.PaymentService.model.PaymentRequest;
import com.ShoppingCart.PaymentService.model.PaymentResponse;

public interface PaymentService {
    long doPayment(PaymentRequest paymentRequest);

    PaymentResponse getPaymentDetailsByOrderId(long orderId);
}
