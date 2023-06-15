package com.ShoppingCart.OrderService.service;

import com.ShoppingCart.OrderService.entity.Order;
import com.ShoppingCart.OrderService.exception.CustomException;
import com.ShoppingCart.OrderService.external.client.PaymentService;
import com.ShoppingCart.OrderService.external.client.ProductService;
import com.ShoppingCart.OrderService.external.request.PaymentRequest;
import com.ShoppingCart.OrderService.external.response.PaymentResponse;
import com.ShoppingCart.OrderService.external.response.ProductResponse;
import com.ShoppingCart.OrderService.model.OrderRequest;
import com.ShoppingCart.OrderService.model.OrderResponse;
import com.ShoppingCart.OrderService.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService{
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private RestTemplate restTemplate;
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

        // Payment Service -> Payment -> success ->Complete
        //                            -> Failure -> Cancelled
        log.info("Calling Payment Service to Complete the Payment");
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(order.getId())
                .paymentMode(orderRequest.getPaymentMode())
                .amount(orderRequest.getTotalAmount())
                .build();

        String orderStatus = null;
        try {
            paymentService.doPayment(paymentRequest);
            log.info("Payment Done Successfully. Changing the Order Status to PLACED");
            orderStatus = "PLACED";
        }catch (Exception ex){
            log.info("Error Occured in Payment. Changing the Order Status to PAYMENT_FAILED");
            orderStatus = "PAYMENT_FAILED";
        }

        order.setOrderStatus(orderStatus);
        orderRepository.save(order);

        log.info("Order Placed Successfully with Order Id: {}", order.getId());
        return order.getId();
    }

    @Override
    public OrderResponse getOrderDetails(long orderId) {
        log.info("Get Order Details for Order Id: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()-> new CustomException(
                        "Order Not Found for the given Order ID: "+orderId,
                        "NOT_FOUND",
                        404
                ));

        log.info("Invoking Product Service to fetch the product for Product Id: {}", order.getProductId());

        ProductResponse productResponse = restTemplate.getForObject(
                "http://PRODUCT-SERVICE/product/"+order.getProductId(),
                ProductResponse.class
        );

        OrderResponse.ProductDetails productDetails = OrderResponse.ProductDetails.builder()
                .name(productResponse.getName())
                .id(productResponse.getId())
                .price(productResponse.getPrice())
                .quantity(productResponse.getQuantity())
                .build();

        log.info("Getting Payment Information from the Payment Service");
        PaymentResponse paymentResponse = restTemplate.getForObject(
                "http://PAYMENT-SERVICE/payment/order/" + order.getId(),
                PaymentResponse.class
        );

        OrderResponse.PaymentDetails paymentDetails = OrderResponse.PaymentDetails.builder()
                .paymentId(paymentResponse.getPaymentId())
                .paymentStatus(paymentResponse.getStatus())
                .paymentDate(paymentResponse.getPaymentDate())
                .paymentMode(paymentResponse.getPaymentMode())
                .build();


        OrderResponse orderResponse = OrderResponse.builder()
                .orderId(order.getId())
                .orderStatus(order.getOrderStatus())
                .amount(order.getAmount())
                .orderDate(order.getOrderDate())
                .productDetails(productDetails)
                .paymentDetails(paymentDetails)
                .build();
        return orderResponse;
    }
}
