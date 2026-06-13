package com.Marketplace_Management.Catalog.DTOs.Commands.Product;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.Marketplace_Management.Catalog.DTOs.Requests.Product.CreateProductVariantRequest;
import com.Marketplace_Management.Shared.DTOs.Commands.BaseCommand;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreateProductVariantCommand extends BaseCommand {
    private String name;
    private String code;
    private double price;
    private int stock;
    
    private List<MultipartFile> images;
    private List<Integer> optionIds;

    public static CreateProductVariantCommand fromRequest(CreateProductVariantRequest request) {
        return new CreateProductVariantCommand(
            BaseCommand.safeTrim(request.getName()),
            BaseCommand.safeTrim(request.getCode()),
            request.getPrice(),
            request.getStock(),
            request.getImages(),
            request.getOptionIds()
        );
    }
}
