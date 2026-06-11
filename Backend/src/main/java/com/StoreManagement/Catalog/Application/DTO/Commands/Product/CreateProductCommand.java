package com.StoreManagement.Catalog.Application.DTO.Commands.Product;

import java.util.List;
import java.util.UUID;

import com.StoreManagement.Catalog.Application.DTO.Requests.Product.CreateProductRequest;
import com.StoreManagement.Shared.Application.DTO.Commands.BaseCommand;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreateProductCommand extends BaseCommand{
    private String name;
    private UUID brandId;
    private String description;
    private List<UUID> categoryIds;
    private String status;
    private List<CreateProductVariantCommand> variants;
    private List<CreateProductOptionCommand> options;

    public static CreateProductCommand fromRequest(CreateProductRequest request) {
        return new CreateProductCommand(
            BaseCommand.safeTrim(request.getName()),
            UUID.fromString(request.getBrandId()),
            BaseCommand.safeTrim(request.getDescription()),
            request.getCategoryIds().stream().map(UUID::fromString).toList(),
            BaseCommand.safeTrim(request.getStatus()),
            request.getVariants().stream().map(CreateProductVariantCommand::fromRequest).toList(),
            request.getOptions().stream().map(CreateProductOptionCommand::fromRequest).toList()
        );
    }
}

