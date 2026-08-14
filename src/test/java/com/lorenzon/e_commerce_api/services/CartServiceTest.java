package com.lorenzon.e_commerce_api.services;

import com.lorenzon.e_commerce_api.dto.CartItemRequestDTO;
import com.lorenzon.e_commerce_api.dto.CartItemResponseDTO;
import com.lorenzon.e_commerce_api.dto.CartResponseDTO;
import com.lorenzon.e_commerce_api.entities.cart.Cart;
import com.lorenzon.e_commerce_api.entities.cartItem.CartItem;
import com.lorenzon.e_commerce_api.entities.product.Product;
import com.lorenzon.e_commerce_api.entities.user.User;
import com.lorenzon.e_commerce_api.exceptions.InsufficientStockException;
import com.lorenzon.e_commerce_api.exceptions.ResourceNotFoundException;
import com.lorenzon.e_commerce_api.factories.*;
import com.lorenzon.e_commerce_api.infra.security.AuthenticatedUserService;
import com.lorenzon.e_commerce_api.mappers.CartMapper;
import com.lorenzon.e_commerce_api.repositories.CartItemRepository;
import com.lorenzon.e_commerce_api.repositories.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartMapper cartMapper;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @Mock
    private ProductService productService;

    private Long nonExistingProductId;
    private Integer excessStock;
    private User user;
    private Cart cart;
    private Product product;
    private CartItemRequestDTO request;
    private CartItem cartItem;
    private CartItemResponseDTO itemResponse;
    private CartResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        nonExistingProductId = 100L;
        excessStock = 11;

        user = UserFactory.createUser();
        cart = CartFactory.createCart(user);
        product = ProductFactory.createProduct();
        request = CartItemRequestFactory.createRequest();
        cartItem = CartItemFactory.createCartItem();

        itemResponse = new CartItemResponseDTO(1L, "Product 1", 2, new BigDecimal("200.00"));
        responseDTO = new CartResponseDTO(1L, List.of(itemResponse), new BigDecimal("200.00"));

        when(authenticatedUserService.getLoggedUser()).thenReturn(user);
    }

    @Test
    public void insertItemShouldAddNewItemToCart() {
        when(productService.findById(1L)).thenReturn(product);
        when(cartItemRepository.findByCartIdAndProductId(1L, 1L)).thenReturn(null);
        when(cartMapper.toCartItem(request)).thenReturn(cartItem);
        when(cartRepository.save(cart)).thenReturn(cart);
        when(cartMapper.toCartResponseDTO(cart)).thenReturn(responseDTO);

        CartResponseDTO result = cartService.insertItem(request);

        assertEquals(responseDTO, result);
        assertEquals(1, cart.getItems().size());
        assertEquals(product, cart.getItems().getFirst().getProduct());
        assertEquals(2, cart.getItems().getFirst().getQuantity());
    }

    @Test
    public void insertItemShouldIncreaseQuantityWhenProductAlreadyExistsInCart() {
        when(productService.findById(1L)).thenReturn(product);
        when(cartItemRepository.findByCartIdAndProductId(1L, 1L)).thenReturn(cartItem);
        when(cartRepository.save(cart)).thenReturn(cart);
        when(cartMapper.toCartResponseDTO(cart)).thenReturn(responseDTO);

        CartResponseDTO result = cartService.insertItem(request);

        assertEquals(responseDTO, result);
        assertEquals(4, cartItem.getQuantity());
        verify(cartItemRepository).findByCartIdAndProductId(1L, 1L);
        verify(cartRepository).save(cart);
    }

    @Test
    public void insertItemShouldThrowResourceNotFoundExceptionWhenProductDoesNotExist() {
        when(productService.findById(nonExistingProductId)).thenThrow(ResourceNotFoundException.class);

        request = new CartItemRequestDTO(nonExistingProductId, 2);

        assertThrows(ResourceNotFoundException.class, () -> {
            cartService.insertItem(request);
        });
    }

    @Test
    public void insertItemShouldThrowInsufficientStockExceptionWhenQuantityExceedsStock() {
        when(productService.findById(1L)).thenReturn(product);
        when(cartItemRepository.findByCartIdAndProductId(1L, 1L)).thenReturn(null);

        request = new CartItemRequestDTO(1L, excessStock);

        assertThrows(InsufficientStockException.class, () -> {
            cartService.insertItem(request);
        });
    }

    @Test
    public void insertItemShouldThrowInsufficientStockExceptionWhenNewQuantityExceedsStock() {
        when(productService.findById(1L)).thenReturn(product);
        cartItem.setQuantity(8);
        when(cartItemRepository.findByCartIdAndProductId(1L, 1L)).thenReturn(cartItem);

        request = new CartItemRequestDTO(1L, 4);

        assertThrows(InsufficientStockException.class, () -> {
            cartService.insertItem(request);
        });
    }

    @Test
    public void updateItemShouldUpdateItemQuantity() {
        when(productService.findById(1L)).thenReturn(product);
        when(cartItemRepository.findByCartIdAndProductId(1L, 1L)).thenReturn(cartItem);
        when(cartRepository.save(cart)).thenReturn(cart);
        when(cartMapper.toCartResponseDTO(cart)).thenReturn(responseDTO);

        CartResponseDTO result = cartService.updateItem(request);

        assertEquals(responseDTO, result);
        assertEquals(2, cartItem.getQuantity());
        verify(cartRepository).save(cart);
        verify(cartMapper).toCartResponseDTO(cart);
    }

    @Test
    public void updateItemShouldThrowResourceNotFoundExceptionWhenCartDoesNotExists() {
        user.setCart(null);

        assertThrows(ResourceNotFoundException.class, () -> {
            cartService.updateItem(request);
        });
    }

    @Test
    public void updateItemShouldThrowResourceNotFoundExceptionWhenItemDoesNotExists() {
        when(productService.findById(1L)).thenReturn(product);
        when(cartItemRepository.findByCartIdAndProductId(1L, 1L)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> {
            cartService.updateItem(request);
        });
    }

    @Test
    public void deleteItemShouldRemoveItemFromCart() {
        when(productService.findById(1L)).thenReturn(product);
        when(cartItemRepository.findByCartIdAndProductId(1L, 1L)).thenReturn(cartItem);

        cartService.deleteItem(request.productId());

        verify(cartItemRepository).delete(cartItem);
    }

    @Test
    public void deleteItemShouldThrowResourceNotFoundExceptionWhenItemDoesNotExist() {
        when(productService.findById(1L)).thenReturn(product);
        when(cartItemRepository.findByCartIdAndProductId(1L, 1L)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> {
            cartService.deleteItem(request.productId());
        });

        verify(cartItemRepository, never()).delete(any(CartItem.class));
    }

    @Test
    public void deleteItemShouldThrowResourceNotFoundExceptionWhenProductDoesNotExist() {
        when(productService.findById(1L)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> {
            cartService.deleteItem(request.productId());
        });

        verify(cartItemRepository, never()).delete(any(CartItem.class));
    }
}
