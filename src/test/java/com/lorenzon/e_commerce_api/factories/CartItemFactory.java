package com.lorenzon.e_commerce_api.factories;

import com.lorenzon.e_commerce_api.entities.cartItem.CartItem;
import com.lorenzon.e_commerce_api.entities.product.Product;

public class CartItemFactory {

    public static CartItem createCartItem(Product product, Integer quantity) {
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        return cartItem;
    }
}
