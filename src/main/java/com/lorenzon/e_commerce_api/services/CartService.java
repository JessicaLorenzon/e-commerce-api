package com.lorenzon.e_commerce_api.services;

import com.lorenzon.e_commerce_api.dto.CartItemRequestDTO;
import com.lorenzon.e_commerce_api.dto.CartResponseDTO;
import com.lorenzon.e_commerce_api.entities.cart.Cart;
import com.lorenzon.e_commerce_api.entities.cartItem.CartItem;
import com.lorenzon.e_commerce_api.entities.product.Product;
import com.lorenzon.e_commerce_api.entities.user.User;
import com.lorenzon.e_commerce_api.exceptions.InsufficientStockException;
import com.lorenzon.e_commerce_api.exceptions.ResourceNotFoundException;
import com.lorenzon.e_commerce_api.infra.security.AuthenticatedUserService;
import com.lorenzon.e_commerce_api.mappers.CartMapper;
import com.lorenzon.e_commerce_api.repositories.CartItemRepository;
import com.lorenzon.e_commerce_api.repositories.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private AuthenticatedUserService authenticatedUserService;

    @Transactional(readOnly = true)
    public CartResponseDTO getCart() {
        User user = authenticatedUserService.getLoggedUser();
        Cart cart = getOrCreateCart(user);
        return cartMapper.toCartResponseDTO(cart);
    }

    @Transactional
    public CartResponseDTO insertItem(CartItemRequestDTO cartItemRequestDTO) {
        User user = authenticatedUserService.getLoggedUser();
        Cart cart = getOrCreateCart(user);
        Product product = productService.findById(cartItemRequestDTO.productId());
        CartItem existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());
        if (existingItem != null) {
            Integer newQuantity = existingItem.getQuantity() + cartItemRequestDTO.quantity();
            validateStock(product, newQuantity);
            existingItem.setQuantity(newQuantity);
        } else {
            validateStock(product, cartItemRequestDTO.quantity());
            CartItem cartItem = cartMapper.toCartItem(cartItemRequestDTO);
            cartItem.setProduct(product);
            cart.addItem(cartItem);
        }
        cart = cartRepository.save(cart);
        return cartMapper.toCartResponseDTO(cart);
    }

    @Transactional
    public CartResponseDTO updateItem(CartItemRequestDTO cartItemRequestDTO) {
        Cart cart = findCartByUser();
        if (cart == null) {
            throw new ResourceNotFoundException("Cart or Item not found");
        }
        Product product = productService.findById(cartItemRequestDTO.productId());
        CartItem existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());
        if (existingItem == null) {
            throw new ResourceNotFoundException("Cart or Item not found");
        }
        validateStock(product, cartItemRequestDTO.quantity());
        existingItem.setQuantity(cartItemRequestDTO.quantity());
        cartRepository.save(cart);
        return cartMapper.toCartResponseDTO(cart);
    }

    @Transactional
    public void deleteItem(Long productId) {
        Cart cart = findCartByUser();
        cartItemRepository.deleteByCartIdAndProductId(cart.getId(), productId);
    }

    private Cart findCartByUser() {
        User user = authenticatedUserService.getLoggedUser();
        return user.getCart();
    }

    private Cart getOrCreateCart(User user) {
        Cart cart = user.getCart();
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
        }
        return cart;
    }

    private void validateStock(Product product, Integer quantity) {
        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException(product.getName());
        }
    }
}
