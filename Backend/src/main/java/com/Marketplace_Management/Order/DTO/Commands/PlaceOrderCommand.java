package com.Marketplace_Management.Order.DTO.Commands;

import java.util.List;
import java.util.UUID;

import com.Marketplace_Management.Order.DTO.Requests.PlaceOrderRequest;

import lombok.Data;

@Data
public class PlaceOrderCommand{
    private UUID cartId;
    private UUID userId;
    private List<OrderItemCommand> items;
    private String name;
    private String phone;
    private String address;
    private String note;

    public static PlaceOrderCommand fromRequest(PlaceOrderRequest request) {
        PlaceOrderCommand command = new PlaceOrderCommand();
        command.setName(request.getName());
        command.setPhone(request.getPhone());
        command.setAddress(request.getAddress());
        command.setNote(request.getNote());
        return command;
    }
}
