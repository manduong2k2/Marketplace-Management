package com.Marketplace_Management.Catalog.DTOs.Commands.Category;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.Marketplace_Management.Catalog.DTOs.Requests.Category.CreateCategoryRequest;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class CreateCategoryCommand {
    private String name;
    private UUID parentId;
    private MultipartFile image;
    private String description;

    public static CreateCategoryCommand fromRequest(CreateCategoryRequest request) {
        return CreateCategoryCommand.builder()
            .name(request.getName())
            .parentId(request.getParentId())
            .image(request.getImage())
            .description(request.getDescription())
            .build();
    }
}
