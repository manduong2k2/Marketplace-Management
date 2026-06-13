package com.Marketplace_Management.Auth.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterResponse {
    private Boolean success;
    private String message;
}
