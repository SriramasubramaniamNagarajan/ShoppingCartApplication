package com.ShoppingCart.OrderService.service;

import com.ShoppingCart.OrderService.model.OrderRequest;
import com.ShoppingCart.OrderService.model.OrderResponse;

public interface OrderService {
    Long placeOrder(OrderRequest orderRequest);

    OrderResponse getOrderDetails(long orderId);
}
