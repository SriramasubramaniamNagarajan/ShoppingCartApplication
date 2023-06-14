package com.ShoppingCart.ProductService.service;

import com.ShoppingCart.ProductService.entity.Product;
import com.ShoppingCart.ProductService.exception.ProductServiceCustomException;
import com.ShoppingCart.ProductService.model.ProductRequest;
import com.ShoppingCart.ProductService.model.ProductResponse;
import com.ShoppingCart.ProductService.repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.springframework.beans.BeanUtils.*;

@Service
@Log4j2
public class ProductServiceImpl implements ProductService{
    @Autowired
    private ProductRepository productRepository;

    @Override
    public long addProduct(ProductRequest productRequest) {
        log.info("Adding Product...");
        Product product = Product.builder()
                .name(productRequest.getName())
                .price(productRequest.getPrice())
                .quantity(productRequest.getQuantity())
                .build();
        productRepository.save(product);
        log.info("Product Created...");
        return 0;
    }

    @Override
    public void reduceQuantity(long productId, long quantity) {
        log.info("Reduce Quantity {} for Id {}", quantity, productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductServiceCustomException("Product With Given Id not Found", "PRODUCT_NOT_FOUND"));
        if(product.getQuantity() < quantity)
            throw new ProductServiceCustomException("Product does not have sufficient Quantity", "INSUFFICIENT_QUANTITY");
        product.setQuantity(product.getQuantity()-quantity);
        productRepository.save(product);
        log.info("Product Quantity Updated Successfully...");
    }

    @Override
    public ProductResponse getProductById(long id) {
        log.info("Get the Product for product Id : {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductServiceCustomException("Product with Given Id is not found", "PRODUCT_NOT_FOUND"));
        ProductResponse productResponse = new ProductResponse();
        copyProperties(product, productResponse);
        return productResponse;
    }

}
