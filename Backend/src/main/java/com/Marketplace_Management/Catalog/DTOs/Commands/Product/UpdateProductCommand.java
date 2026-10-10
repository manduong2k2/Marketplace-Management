package com.Marketplace_Management.Catalog.DTOs.Commands.Product;

import java.util.List;
import java.util.UUID;

import com.Marketplace_Management.Catalog.DTOs.Requests.Product.UpdateProductRequest;
import com.Marketplace_Management.Shared.DTOs.Commands.BaseCommand;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class UpdateProductCommand extends BaseCommand {
    private String name;
    private UUID brandId;
    private String description;
    private List<UUID> categoryIds;
    private String status;

    public static UpdateProductCommand fromRequest(UpdateProductRequest request) {
        // Every field is optional: null means "keep the current value"
        return UpdateProductCommand.builder()
            .name(safeTrim(request.getName()))
            .brandId(uuidOrNull(request.getBrandId()))
            .description(safeTrim(request.getDescription()))
            .categoryIds(request.getCategoryIds() != null ? request.getCategoryIds().stream().map(UUID::fromString).toList() : null)
            .status(safeTrim(request.getStatus()))
            .build();
    }
}

