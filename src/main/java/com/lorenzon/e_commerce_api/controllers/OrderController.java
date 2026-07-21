package com.lorenzon.e_commerce_api.controllers;

import com.lorenzon.e_commerce_api.dto.OrderResponseDTO;
import com.lorenzon.e_commerce_api.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService service;

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> findAllOrders() {
        List<OrderResponseDTO> orderDTOS = service.findAll();
        return ResponseEntity.ok(orderDTOS);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> findById(@PathVariable Long id) {
        OrderResponseDTO orderDTO = service.findById(id);
        return ResponseEntity.ok(orderDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> cancelOrder(@PathVariable Long id) {
        OrderResponseDTO responseDTO = service.cancel(id);
        return ResponseEntity.ok(responseDTO);
    }
}
