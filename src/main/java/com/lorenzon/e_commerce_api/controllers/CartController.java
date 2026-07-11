package com.lorenzon.e_commerce_api.controllers;

import com.lorenzon.e_commerce_api.dto.CartItemRequestDTO;
import com.lorenzon.e_commerce_api.dto.CartResponseDTO;
import com.lorenzon.e_commerce_api.services.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/carts")
public class CartController {

    @Autowired
    private CartService service;

    @GetMapping
    public ResponseEntity<CartResponseDTO> getCart() {
        CartResponseDTO responseDTO = service.getCart();
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping
    public ResponseEntity<CartResponseDTO> insertItemCart(@RequestBody @Valid CartItemRequestDTO cartItemRequestDTO) {
        CartResponseDTO responseDTO = service.insert(cartItemRequestDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(responseDTO.id()).toUri();
        return ResponseEntity.created(uri).body(responseDTO);
    }
}
