package com.Marketplace_Management.Auth.DTOs.Commands;

import org.springframework.web.multipart.MultipartFile;

import com.Marketplace_Management.Auth.DTOs.Request.UpdateProfileRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileCommand {
    private String name;
    private String phone;
    private MultipartFile avatar;

    public static UpdateProfileCommand fromRequest(UpdateProfileRequest request) {
        return new UpdateProfileCommand(
            request.getName(),
            request.getPhone(),
            request.getAvatar()
        );
    }
}
