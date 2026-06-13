package com.Marketplace_Management.Catalog.DTO.Commands.Brand;

import org.springframework.web.multipart.MultipartFile;

import com.Marketplace_Management.Catalog.DTO.Requests.Brand.CreateBrandRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBrandCommand {
    private String name;
    private MultipartFile image;
    private String description;
    
    public static CreateBrandCommand fromRequest(CreateBrandRequest request) {
        return new CreateBrandCommand(
            request.getName(),
            request.getImage(),
            request.getDescription()
        );
    }
}
