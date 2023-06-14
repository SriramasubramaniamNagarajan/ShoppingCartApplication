package com.ShoppingCart.OrderService.service;

import com.ShoppingCart.OrderService.model.OrderRequest;

public interface OrderService {
    Long placeOrder(OrderRequest orderRequest);
}
