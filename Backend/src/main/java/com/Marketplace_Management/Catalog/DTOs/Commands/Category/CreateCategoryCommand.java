package com.Marketplace_Management.Catalog.DTOs.Commands.Category;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.Marketplace_Management.Catalog.DTOs.Requests.Category.CreateCategoryRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCategoryCommand {
    private String name;
    private UUID parentId;
    private MultipartFile image;
    private String description;

    public static CreateCategoryCommand fromRequest(CreateCategoryRequest request) {
        return new CreateCategoryCommand(
            request.getName(),
            request.getParentId(),
            request.getImage(),
            request.getDescription()
        );
    }
}
