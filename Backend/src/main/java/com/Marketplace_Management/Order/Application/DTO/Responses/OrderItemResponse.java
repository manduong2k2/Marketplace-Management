package com.Marketplace_Management.Order.Application.DTO.Responses;

import java.util.UUID;

import com.Marketplace_Management.Order.Domain.Models.OrderItem;

import lombok.Data;

@Data
public class OrderItemResponse {
    private UUID id;
    private UUID productId;
    private int quantity;
    private double total;
    private ProductSnapShotResponse snapShot;

    public static OrderItemResponse from(OrderItem item) {
        OrderItemResponse r = new OrderItemResponse();
        r.id               = item.getId();
        r.productId        = item.getProductId();
        r.quantity         = item.getQuantity();
        r.total            = item.getTotal();
        r.snapShot         = ProductSnapShotResponse.from(item.getSnapShot());
        return r;
    }
}
