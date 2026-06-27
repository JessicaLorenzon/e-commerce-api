package com.lorenzon.e_commerce_api.mappers;

import com.lorenzon.e_commerce_api.dto.ProductRequestDTO;
import com.lorenzon.e_commerce_api.dto.ProductResponseDTO;
import com.lorenzon.e_commerce_api.entities.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toEntity(ProductRequestDTO productRequestDTO);

    ProductResponseDTO toResponseDTO(Product product);

    void updateEntity(ProductRequestDTO productRequestDTO, @MappingTarget Product product);
}
