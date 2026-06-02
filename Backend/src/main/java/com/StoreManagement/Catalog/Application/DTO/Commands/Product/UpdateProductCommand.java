package com.StoreManagement.Catalog.Application.DTO.Commands.Product;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.StoreManagement.Catalog.Application.DTO.Requests.Product.UpdateProductRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductCommand {
    private String name;
    private String code;
    private UUID brandId;
    private String description;
    private String status;
    private List<UUID> categoryIds;
    private List<String> imageUrls;
    private List<MultipartFile> images;

    public static UpdateProductCommand fromRequest(UpdateProductRequest request) {
        return new UpdateProductCommand(
            request.getName(),
            request.getCode(),
            request.getBrandId(),
            request.getDescription(),
            request.getStatus(),
            request.getCategoryIds(),
            request.getImageUrls(),
            request.getImages()
        );
    }
}

