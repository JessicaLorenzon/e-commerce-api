package com.lorenzon.e_commerce_api.factories;

import com.lorenzon.e_commerce_api.entities.product.Product;

public class ProductFactory {

    public static Product createProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setStockQuantity(10);
        return product;
    }
}
