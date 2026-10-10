package com.Marketplace_Management.Catalog.DTOs.Commands.Category;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.Marketplace_Management.Catalog.DTOs.Requests.Category.UpdateCategoryRequest;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class UpdateCategoryCommand {
    private String name;
    private String description;
    private MultipartFile image;
    private UUID parentId;

    public static UpdateCategoryCommand fromRequest(UpdateCategoryRequest request) {
        return UpdateCategoryCommand.builder()
            .name(request.getName())
            .description(request.getDescription())
            .image(request.getImage())
            .parentId(request.getParentId())
            .build();
    }
}

