package com.lorenzon.e_commerce_api.services;

import com.lorenzon.e_commerce_api.dto.ProductRequestDTO;
import com.lorenzon.e_commerce_api.dto.ProductResponseDTO;
import com.lorenzon.e_commerce_api.entities.product.Product;
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
    public ProductResponseDTO findById(Long id) {
        Product product = repository.getReferenceById(id);
        return mapper.toResponseDTO(product);
    }

    @Transactional
    public ProductResponseDTO insert(ProductRequestDTO productRequestDTO) {
        Product product = mapper.toEntity(productRequestDTO);
        product = repository.save(product);
        return mapper.toResponseDTO(product);
    }

    @Transactional
    public ProductResponseDTO update(Long id, ProductRequestDTO productRequestDTO) {
        Product product = repository.getReferenceById(id);
        mapper.updateEntity(productRequestDTO, product);
        return mapper.toResponseDTO(product);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
