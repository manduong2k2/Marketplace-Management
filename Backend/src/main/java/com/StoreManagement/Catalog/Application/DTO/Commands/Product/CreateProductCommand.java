package com.StoreManagement.Catalog.Application.DTO.Commands.Product;

import java.util.List;
import java.util.UUID;

import com.StoreManagement.Catalog.Application.DTO.Requests.Product.CreateProductRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductCommand {
    private String name;
    private String code;
    private double price;
    private int stock;
    private UUID brandId;
    private String description;
    private List<UUID> categoryIds;
    private String status;

    public static CreateProductCommand fromRequest(CreateProductRequest request) {
        CreateProductCommand command = new CreateProductCommand();
        command.setName(request.getName());
        command.setCode(request.getCode());
        command.setPrice(request.getPrice());
        command.setStock(request.getStock());
        command.setBrandId(request.getBrandId());
        command.setDescription(request.getDescription());
        command.setCategoryIds(request.getCategoryIds());
        command.setStatus(request.getStatus());
        return command;
    }
}

