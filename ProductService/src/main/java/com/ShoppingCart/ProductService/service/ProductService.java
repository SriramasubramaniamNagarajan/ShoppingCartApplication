package com.ShoppingCart.ProductService.service;

import com.ShoppingCart.ProductService.model.ProductRequest;
import com.ShoppingCart.ProductService.model.ProductResponse;

public interface ProductService {
    ProductResponse getProductById(long id) ;

    long addProduct(ProductRequest productRequest);
}
