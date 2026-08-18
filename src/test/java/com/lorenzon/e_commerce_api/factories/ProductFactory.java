package com.lorenzon.e_commerce_api.factories;

import com.lorenzon.e_commerce_api.entities.product.Product;

import java.math.BigDecimal;

public class ProductFactory {

    public static Product createProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Product 1");
        product.setPrice(new BigDecimal("100.00"));
        product.setStockQuantity(10);
        return product;
    }
}
