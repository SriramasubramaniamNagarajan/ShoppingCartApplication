package com.ShoppingCart.ProductService.controller;

import com.ShoppingCart.ProductService.model.ProductRequest;
import com.ShoppingCart.ProductService.model.ProductResponse;
import com.ShoppingCart.ProductService.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;
    @PostMapping
    public ResponseEntity<String> addProduct(@RequestBody ProductRequest productRequest){
        productService.addProduct(productRequest);
        return new ResponseEntity<String>("Product Created Successfully...", HttpStatus.CREATED);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable long id){
        ProductResponse productResponse =
                productService.getProductById(id);
        return new ResponseEntity<ProductResponse>(productResponse, HttpStatus.OK);
    }
    @PutMapping("/reduceQuantity/{id}")
    public ResponseEntity<Void> reduceQuantity(@PathVariable("id") long productId,
                                               @RequestParam long quantity){
        productService.reduceQuantity(productId, quantity);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
