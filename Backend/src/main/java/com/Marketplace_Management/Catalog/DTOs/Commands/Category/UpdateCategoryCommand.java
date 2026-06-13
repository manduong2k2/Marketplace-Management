package com.Marketplace_Management.Catalog.DTOs.Commands.Category;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.Marketplace_Management.Catalog.DTOs.Requests.Category.UpdateCategoryRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCategoryCommand {
    private String name;
    private String description;
    private MultipartFile image;
    private UUID parentId;
    
    public static UpdateCategoryCommand fromRequest(UpdateCategoryRequest request) {
        UpdateCategoryCommand command = new UpdateCategoryCommand();
        command.setName(request.getName());
        command.setDescription(request.getDescription());
        command.setImage(request.getImage());
        command.setParentId(request.getParentId());
        return command;
    }
}

