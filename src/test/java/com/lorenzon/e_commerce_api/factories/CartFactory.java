package com.lorenzon.e_commerce_api.factories;

import com.lorenzon.e_commerce_api.entities.cart.Cart;
import com.lorenzon.e_commerce_api.entities.user.User;

public class CartFactory {

    public static Cart createCart(User user) {
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        user.setCart(cart);
        return cart;
    }
}
