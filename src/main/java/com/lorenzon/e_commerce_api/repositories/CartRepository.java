package com.lorenzon.e_commerce_api.repositories;

import com.lorenzon.e_commerce_api.entities.cart.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
