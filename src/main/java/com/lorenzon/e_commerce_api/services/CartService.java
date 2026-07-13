package com.lorenzon.e_commerce_api.services;

import com.lorenzon.e_commerce_api.dto.CartItemRequestDTO;
import com.lorenzon.e_commerce_api.dto.CartResponseDTO;
import com.lorenzon.e_commerce_api.dto.OrderResponseDTO;
import com.lorenzon.e_commerce_api.entities.cart.Cart;
import com.lorenzon.e_commerce_api.entities.cartItem.CartItem;
import com.lorenzon.e_commerce_api.entities.order.Order;
import com.lorenzon.e_commerce_api.entities.product.Product;
import com.lorenzon.e_commerce_api.entities.user.User;
import com.lorenzon.e_commerce_api.exceptions.CartOrItemNotFoundException;
import com.lorenzon.e_commerce_api.exceptions.InsufficientStockException;
import com.lorenzon.e_commerce_api.mappers.CartMapper;
import com.lorenzon.e_commerce_api.mappers.OrderMapper;
import com.lorenzon.e_commerce_api.repositories.CartItemRepository;
import com.lorenzon.e_commerce_api.repositories.CartRepository;
import com.lorenzon.e_commerce_api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Transactional(readOnly = true)
    public CartResponseDTO getCart() {
        User user = getLoggedUser();
        Cart cart = getOrCreateCart(user);
        return cartMapper.toCartResponseDTO(cart);
    }

    @Transactional
    public CartResponseDTO insert(CartItemRequestDTO cartItemRequestDTO) {
        User user = getLoggedUser();
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
    public CartResponseDTO update(CartItemRequestDTO cartItemRequestDTO) {
        Cart cart = findCartByUser();
        if (cart == null) {
            throw new CartOrItemNotFoundException();
        }
        Product product = productService.findById(cartItemRequestDTO.productId());
        CartItem existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());
        if (existingItem == null) {
            throw new CartOrItemNotFoundException();
        }
        validateStock(product, cartItemRequestDTO.quantity());
        existingItem.setQuantity(cartItemRequestDTO.quantity());
        cartRepository.save(cart);
        return cartMapper.toCartResponseDTO(cart);
    }

    @Transactional
    public void delete(Long productId) {
        Cart cart = findCartByUser();
        cartItemRepository.deleteByCartIdAndProductId(cart.getId(), productId);
    }

    @Transactional
    public OrderResponseDTO checkout() {
        Cart cart = findCartByUser();
        Order order = orderService.createOrder(cart);
        cart.getItems().clear();
        return orderMapper.toResponseDTO(order);
    }

    private Cart findCartByUser() {
        User user = getLoggedUser();
        return user.getCart();
    }

    private Cart getOrCreateCart(User user) {
        Cart cart = user.getCart();
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            user.setCart(cart);
        }
        return cart;
    }

    private void validateStock(Product product, Integer quantity) {
        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException(product.getName());
        }
    }

    private User getLoggedUser() {
        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        return (User) userRepository.findByEmail(email);
    }
}
