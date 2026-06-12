package com.Marketplace_Management.Catalog.Application.DTO.Commands.Product;

import com.Marketplace_Management.Catalog.Application.DTO.Requests.Product.CreateProductOptionRequest;
import com.Marketplace_Management.Shared.Application.DTO.Commands.BaseCommand;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CreateProductOptionCommand extends BaseCommand {
    private int tempId;
    private String name;
    private String value;

    public static CreateProductOptionCommand fromRequest(CreateProductOptionRequest request) {
        return new CreateProductOptionCommand(
            request.getTempId(),
            BaseCommand.safeTrim(request.getName()),
            BaseCommand.safeTrim(request.getValue())
        );
    }
}
