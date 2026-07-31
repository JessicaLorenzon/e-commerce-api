package com.lorenzon.e_commerce_api.repositories;

import com.lorenzon.e_commerce_api.entities.cartItem.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    CartItem findByCartIdAndProductId(Long cartId, Long productId);
}
