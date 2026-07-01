package com.lorenzon.e_commerce_api.mappers;

import com.lorenzon.e_commerce_api.dto.OrderItemResponseDTO;
import com.lorenzon.e_commerce_api.dto.OrderResponseDTO;
import com.lorenzon.e_commerce_api.entities.order.Order;
import com.lorenzon.e_commerce_api.entities.orderItem.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "subTotal", expression = "java(orderItem.getSubTotal())")
    OrderItemResponseDTO toOrderItemResponseDTO(OrderItem orderItem);

    OrderResponseDTO toResponseDTO(Order order);
}
