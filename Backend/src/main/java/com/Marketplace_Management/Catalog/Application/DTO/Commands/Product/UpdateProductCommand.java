package com.Marketplace_Management.Catalog.Application.DTO.Commands.Product;

import java.util.List;
import java.util.UUID;

import com.Marketplace_Management.Catalog.Application.DTO.Requests.Product.UpdateProductRequest;
import com.Marketplace_Management.Shared.Application.DTO.Commands.BaseCommand;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UpdateProductCommand extends BaseCommand {
    private String name;
    private UUID brandId;
    private String description;
    private List<UUID> categoryIds;
    private String status;

    public static UpdateProductCommand fromRequest(UpdateProductRequest request) {
        return new UpdateProductCommand(
            BaseCommand.safeTrim(request.getName()),
            UUID.fromString(request.getBrandId()),
            BaseCommand.safeTrim(request.getDescription()),
            request.getCategoryIds().stream().map(UUID::fromString).toList(),
            BaseCommand.safeTrim(request.getStatus())
        );
    }
}

