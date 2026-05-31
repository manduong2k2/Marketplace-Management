package com.StoreManagement.Catalog.Application.DTO.Commands.Category;

import com.StoreManagement.Catalog.Application.DTO.Requests.Category.UpdateCategoryRequest;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCategoryCommand {
    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String description;
    
    public static UpdateCategoryCommand fromRequest(UpdateCategoryRequest request) {
        UpdateCategoryCommand command = new UpdateCategoryCommand();
        command.setName(request.getName());
        command.setDescription(request.getDescription());
        return command;
    }
}

