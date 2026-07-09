package com.lorenzon.e_commerce_api.mappers;

import com.lorenzon.e_commerce_api.dto.CartItemRequestDTO;
import com.lorenzon.e_commerce_api.dto.CartItemResponseDTO;
import com.lorenzon.e_commerce_api.dto.CartResponseDTO;
import com.lorenzon.e_commerce_api.entities.cart.Cart;
import com.lorenzon.e_commerce_api.entities.cartItem.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    CartItem toCartItem(CartItemRequestDTO cartItemRequestDTO);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "subTotal", expression = "java(cartItem.getSubTotal())")
    CartItemResponseDTO toCartItemResponseDTO(CartItem cartItem);

    @Mapping(target = "total", expression = "java(cart.getTotal())")
    CartResponseDTO toCartResponseDTO(Cart cart);

}
