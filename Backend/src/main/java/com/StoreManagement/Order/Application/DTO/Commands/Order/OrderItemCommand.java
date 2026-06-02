package com.StoreManagement.Order.Application.DTO.Commands.Order;

import java.util.List;
import java.util.UUID;

import com.StoreManagement.Order.Application.DTO.Requests.Order.OrderItemRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemCommand {
    private UUID productId;
    private int quantity;
    private String productName;
    private double productPrice;
    private String productCode;
    private String productDescription;
    private List<String> productImages;
    
    public static OrderItemCommand fromRequest(OrderItemRequest request) {
        OrderItemCommand command = new OrderItemCommand();
        command.setProductId(UUID.fromString(request.getProductId()));
        command.setQuantity(request.getQuantity());
        return command;
    }
}
