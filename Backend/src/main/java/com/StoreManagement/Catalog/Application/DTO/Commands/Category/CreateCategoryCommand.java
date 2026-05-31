package com.StoreManagement.Catalog.Application.DTO.Commands.Category;

import java.util.UUID;

import com.StoreManagement.Catalog.Application.DTO.Requests.Category.CreateCategoryRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCategoryCommand {
    private String name;
    private UUID parentId;
    private String image;
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
