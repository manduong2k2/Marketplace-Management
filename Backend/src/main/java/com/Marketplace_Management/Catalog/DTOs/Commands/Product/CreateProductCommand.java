package com.Marketplace_Management.Catalog.DTOs.Commands.Product;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.Marketplace_Management.Catalog.DTOs.Requests.Product.CreateProductOptionRequest;
import com.Marketplace_Management.Catalog.DTOs.Requests.Product.CreateProductRequest;
import com.Marketplace_Management.Catalog.DTOs.Requests.Product.CreateProductVariantRequest;
import com.Marketplace_Management.Shared.DTOs.Commands.BaseCommand;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreateProductCommand extends BaseCommand {
    private String name;
    private UUID brandId;
    private String description;
    private List<UUID> categoryIds;
    private String status;
    private List<CreateProductVariantCommand> variants;
    private List<CreateProductOptionCommand> options;
    private UUID vendorId;

    public static CreateProductCommand fromRequest(CreateProductRequest request) {
        return new CreateProductCommand(
                BaseCommand.safeTrim(request.getName()),
                UUID.fromString(request.getBrandId()),
                BaseCommand.safeTrim(request.getDescription()),
                request.getCategoryIds().stream().map(UUID::fromString).toList(),
                BaseCommand.safeTrim(request.getStatus()),
                request.getVariants().stream().map(CreateProductVariantCommand::fromRequest).toList(),
                request.getOptions() != null ? request.getOptions().stream().map(CreateProductOptionCommand::fromRequest).toList() : List.of(),
                request.getVendorId() != null ? UUID.fromString(request.getVendorId()) : null);
    }

    @Data
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class CreateProductVariantCommand extends BaseCommand {
        private String name;
        private String sku;
        private double price;
        private int stock;

        private List<MultipartFile> images;
        private List<Long> optionIds;

        public static CreateProductVariantCommand fromRequest(CreateProductVariantRequest request) {
            return new CreateProductVariantCommand(
                    BaseCommand.safeTrim(request.getName()),
                    BaseCommand.safeTrim(request.getSku()),
                    request.getPrice(),
                    request.getStock(),
                    request.getImages(),
                    request.getOptionIds());
        }
    }

    @Data
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class CreateProductOptionCommand extends BaseCommand {
        private Long tempId;
        private String name;
        private String value;

        public static CreateProductOptionCommand fromRequest(CreateProductOptionRequest request) {
            return new CreateProductOptionCommand(
                    request.getTempId(),
                    BaseCommand.safeTrim(request.getName()),
                    BaseCommand.safeTrim(request.getValue()));
        }
    }

}
