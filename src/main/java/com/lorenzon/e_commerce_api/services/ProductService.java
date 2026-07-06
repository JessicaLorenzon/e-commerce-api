package com.lorenzon.e_commerce_api.services;

import com.lorenzon.e_commerce_api.dto.ProductRequestDTO;
import com.lorenzon.e_commerce_api.dto.ProductResponseAdminDTO;
import com.lorenzon.e_commerce_api.dto.ProductResponseDTO;
import com.lorenzon.e_commerce_api.entities.product.Product;
import com.lorenzon.e_commerce_api.exceptions.ResourceNotFoundException;
import com.lorenzon.e_commerce_api.mappers.ProductMapper;
import com.lorenzon.e_commerce_api.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private ProductMapper mapper;

    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> findAll(String name, Pageable pageable) {
        Page<Product> products = repository.searchByName(name, pageable);
        return products.map(mapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO searchById(Long id) {
        Product product = findById(id);
        return mapper.toResponseDTO(product);
    }

    @Transactional
    public ProductResponseAdminDTO insert(ProductRequestDTO productRequestDTO) {
        Product product = mapper.toEntity(productRequestDTO);
        product = repository.save(product);
        return mapper.toResponseAdminDTO(product);
    }

    @Transactional
    public ProductResponseAdminDTO update(Long id, ProductRequestDTO productRequestDTO) {
        Product product = findById(id);
        mapper.updateEntity(productRequestDTO, product);
        return mapper.toResponseAdminDTO(product);
    }

    @Transactional
    public ProductResponseAdminDTO disable(Long id) {
        Product product = findById(id);
        product.setStockQuantity(0);
        return mapper.toResponseAdminDTO(product);
    }

    public Product findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }
}
