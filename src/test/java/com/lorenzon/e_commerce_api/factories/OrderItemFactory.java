package com.lorenzon.e_commerce_api.factories;

import com.lorenzon.e_commerce_api.entities.order.Order;
import com.lorenzon.e_commerce_api.entities.orderItem.OrderItem;
import com.lorenzon.e_commerce_api.entities.product.Product;

import java.math.BigDecimal;

public class OrderItemFactory {

    public static OrderItem createOrderItem(Order order, Product product, Integer quantity, BigDecimal price) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(price);
        return orderItem;
    }
}
