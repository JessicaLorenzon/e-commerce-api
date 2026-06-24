package com.lorenzon.e_commerce_api.entities.order;

import com.lorenzon.e_commerce_api.entities.orderItem.OrderItem;
import com.lorenzon.e_commerce_api.entities.payment.Payment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant moment;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> items = new ArrayList<>();

    @OneToOne(mappedBy = "order")
    private Payment payment;
}
