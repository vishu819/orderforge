package com.orderforge.product.controller;

import com.orderforge.product.model.Product;
import com.orderforge.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.orderforge.product.exception.ProductNotFoundException;
import java.util.Optional;


@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/{productId}") ///products/
    ResponseEntity<Product> getProductById(@PathVariable String productId){
        Optional<Product> product = productService.getProductById(productId);
        return ResponseEntity.ok(product.orElseThrow(ProductNotFoundException::new));
    }
}
