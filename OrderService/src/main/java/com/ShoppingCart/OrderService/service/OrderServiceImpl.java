package com.ShoppingCart.OrderService.service;

import com.ShoppingCart.OrderService.entity.Order;
import com.ShoppingCart.OrderService.external.client.ProductService;
import com.ShoppingCart.OrderService.model.OrderRequest;
import com.ShoppingCart.OrderService.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService{
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductService productService;
    @Override
    public Long placeOrder(OrderRequest orderRequest) {
        // Order Entity -> save data with status Order created
        log.info("Placing Order Request: {}", orderRequest);
        // Product Service -> Block Products (Reduce the Quantity)
        productService.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity());
        log.info("Creating order with status created");
        Order order = Order.builder()
                .amount(orderRequest.getTotalAmount())
                .orderStatus("CREATED")
                .productId(orderRequest.getProductId())
                .orderDate(Instant.now())
                .quantity(orderRequest.getQuantity())
                .build();
        order = orderRepository.save(order);
        log.info("Order Placed Successfully with Order Id: {}", order.getId());

        // Payment Service -> Payment -> success ->Complete
        //                            -> Failure -> Cancelled

        return order.getId();
    }
}
