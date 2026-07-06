package com.lorenzon.e_commerce_api.controllers;

import com.lorenzon.e_commerce_api.dto.ProductRequestDTO;
import com.lorenzon.e_commerce_api.dto.ProductResponseAdminDTO;
import com.lorenzon.e_commerce_api.dto.ProductResponseDTO;
import com.lorenzon.e_commerce_api.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService service;

    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> findAllProducts(@RequestParam(name = "name", defaultValue = "") String name, Pageable pageable) {
        Page<ProductResponseDTO> productDTOPage = service.findAll(name, pageable);
        return ResponseEntity.ok(productDTOPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> findProductById(@PathVariable Long id) {
        ProductResponseDTO responseDTO = service.searchById(id);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseAdminDTO> saveProduct(@RequestBody ProductRequestDTO productRequestDTO) {
        ProductResponseAdminDTO responseDTO = service.insert(productRequestDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(responseDTO.id()).toUri();
        return ResponseEntity.created(uri).body(responseDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseAdminDTO> updateProduct(@PathVariable Long id, @RequestBody @Valid ProductRequestDTO productRequestDTO) {
        ProductResponseAdminDTO responseDTO = service.update(id, productRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseAdminDTO> disableProduct(@PathVariable Long id) {
        ProductResponseAdminDTO responseDTO = service.disable(id);
        return ResponseEntity.ok(responseDTO);
    }
}
