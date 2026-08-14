package com.lorenzon.e_commerce_api.factories;

import com.lorenzon.e_commerce_api.entities.cartItem.CartItem;

public class CartItemFactory {

    public static CartItem createCartItem() {
        CartItem cartItem = new CartItem();
        cartItem.setQuantity(2);
        return cartItem;
    }
}
