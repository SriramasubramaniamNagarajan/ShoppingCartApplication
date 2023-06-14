package com.ShoppingCart.ProductService.service;

import com.ShoppingCart.ProductService.model.ProductRequest;

public interface ProductService {
    long addProduct(ProductRequest productRequest);
}
