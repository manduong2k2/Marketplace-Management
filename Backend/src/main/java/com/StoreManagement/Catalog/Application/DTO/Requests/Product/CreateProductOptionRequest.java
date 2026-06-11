package com.StoreManagement.Catalog.Application.DTO.Requests.Product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateProductOptionRequest {
    @NotNull(message = "Temporary ID is required")
    private Integer tempId;

    @NotBlank(message = "Option name is required")
    @Size(min = 1, max = 100, message = "Option name must be between 1 and 100 characters")
    private String name;

    @NotBlank(message = "Option value is required")
    @Size(min = 1, max = 100, message = "Option value must be between 1 and 100 characters")
    private String value;
}
