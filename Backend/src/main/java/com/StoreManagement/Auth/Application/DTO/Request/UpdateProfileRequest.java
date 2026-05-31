package com.StoreManagement.Auth.Application.DTO.Request;

import org.springframework.web.multipart.MultipartFile;

import com.StoreManagement.Shared.Application.Annotation.Rules.Unique;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;
    
    @Unique(table = "users", column = "phone", message = "Phone already exists")
    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;

    private MultipartFile avatar;
}
