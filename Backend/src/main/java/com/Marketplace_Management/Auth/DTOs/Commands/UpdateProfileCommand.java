package com.Marketplace_Management.Auth.DTOs.Commands;

import org.springframework.web.multipart.MultipartFile;

import com.Marketplace_Management.Auth.DTOs.Request.UpdateProfileRequest;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateProfileCommand {
    private String name;
    private String phone;
    private MultipartFile avatar;

    public static UpdateProfileCommand fromRequest(UpdateProfileRequest request) {
        return UpdateProfileCommand.builder()
            .name(request.getName())
            .phone(request.getPhone())
            .avatar(request.getAvatar())
            .build();
    }
}
