package com.lorenzon.e_commerce_api.entities.payment;

import com.lorenzon.e_commerce_api.entities.order.Order;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "tb_payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    private Long id;

    private Instant moment;

    @OneToOne
    @MapsId
    @JoinColumn(name = "order_id")
    private Order order;
}
