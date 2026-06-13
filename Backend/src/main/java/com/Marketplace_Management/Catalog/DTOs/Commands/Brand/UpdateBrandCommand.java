package com.Marketplace_Management.Catalog.DTOs.Commands.Brand;

import org.springframework.web.multipart.MultipartFile;

import com.Marketplace_Management.Catalog.DTOs.Requests.Brand.UpdateBrandRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBrandCommand {

    private String name;
    private String description;
    private MultipartFile image;

    public static UpdateBrandCommand fromRequest(UpdateBrandRequest request) {
        return new UpdateBrandCommand(
            request.getName(),
            request.getDescription(),
            request.getImage()
        );
    }
}

