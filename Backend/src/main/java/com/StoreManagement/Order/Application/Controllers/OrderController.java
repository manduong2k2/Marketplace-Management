package com.StoreManagement.Order.Application.Controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.StoreManagement.Order.Application.DTO.Responses.Order.OrderResponse;
import com.StoreManagement.Order.Application.Services.OrderService;
import com.StoreManagement.Shared.Application.Annotation.Auth.Authenticated;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @Authenticated
    public ResponseEntity<List<OrderResponse>> list() {
        return ResponseEntity.ok(orderService.list());
    }

    @GetMapping("/{id}")
    @Authenticated
    public ResponseEntity<OrderResponse> detail(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.findById(id));
    }
}
