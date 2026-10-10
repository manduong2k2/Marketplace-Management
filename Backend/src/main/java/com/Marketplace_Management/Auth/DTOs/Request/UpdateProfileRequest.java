package com.Marketplace_Management.Auth.DTOs.Request;

import org.springframework.web.multipart.MultipartFile;


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
    
    // Uniqueness is checked in AuthService.updateProfile: only when the phone changes (the user's own number is not a clash)
    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;

    private MultipartFile avatar;
}
