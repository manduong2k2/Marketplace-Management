package com.StoreManagement.Order.Application.Controllers;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.StoreManagement.Catalog.Application.DTO.Response.PaginatedResponse;
import com.StoreManagement.Order.Application.DTO.Commands.Order.ListOrderCommand;
import com.StoreManagement.Order.Application.DTO.Commands.Order.PlaceOrderCommand;
import com.StoreManagement.Order.Application.DTO.Requests.Order.ListOrderRequest;
import com.StoreManagement.Order.Application.DTO.Requests.Order.PlaceOrderRequest;
import com.StoreManagement.Order.Application.DTO.Responses.Order.HistoryResponse;
import com.StoreManagement.Order.Application.DTO.Responses.Order.OrderResponse;
import com.StoreManagement.Order.Domain.Contracts.IOrderService;
import com.StoreManagement.Shared.Application.Annotation.Auth.Authenticated;
import com.StoreManagement.Shared.Domain.Constants.UserRole;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    @GetMapping
    @PreAuthorize("hasAuthority('" + UserRole.ADMIN + "')")
    public ResponseEntity<PaginatedResponse<HistoryResponse>> list(ListOrderRequest request) {
        ListOrderCommand command = ListOrderCommand.fromRequest(request);
        
        PaginatedResponse<HistoryResponse> response = orderService.list(command);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Authenticated
    public ResponseEntity<PaginatedResponse<HistoryResponse>> listByUser(ListOrderRequest request) {
        ListOrderCommand command = ListOrderCommand.fromRequest(request);

        PaginatedResponse<HistoryResponse> response = orderService.listByUser(command);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Authenticated
    public ResponseEntity<HashMap<String, Object>> place(@Valid @RequestBody PlaceOrderRequest request) {
        PlaceOrderCommand command = PlaceOrderCommand.fromRequest(request);

        OrderResponse orderRes = orderService.placeOrder(command);

        HashMap<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Order placed successfully");
        response.put("data", orderRes);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Authenticated
    @PreAuthorize("@orderSecurity.canViewOrder(#id)")
    public ResponseEntity<OrderResponse> detail(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.findById(id));
    }
}
