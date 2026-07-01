package com.lorenzon.e_commerce_api.repositories;

import com.lorenzon.e_commerce_api.entities.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
