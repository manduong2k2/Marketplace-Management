package com.StoreManagement.Catalog.Application.DTO.Commands.Brand;

import com.StoreManagement.Catalog.Application.DTO.Requests.Brand.UpdateBrandRequest;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBrandCommand {
    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    public static UpdateBrandCommand fromRequest(UpdateBrandRequest request) {
        return new UpdateBrandCommand(
            request.getName(),
            request.getDescription()
        );
    }
}

