package com.Marketplace_Management.Auth.DTOs.Request;

import java.util.UUID;

import com.Marketplace_Management.Shared.DTOs.Requests.PageRequest;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** search matches the user name or email. */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetListUserRequest extends PageRequest {
    @Nullable
    private UUID roleId;

    @Nullable
    @Pattern(regexp = "ACTIVE|INACTIVE", message = "status must be ACTIVE or INACTIVE")
    private String status;

    public GetListUserRequest() {
        super("createdAt", "desc", 10);
    }

    @Override
    @Max(value = 100, message = "size must not exceed 100")
    public int getSize() {
        return super.getSize();
    }

    @Override
    @Pattern(regexp = "name|email|status|createdAt", message = "sortBy must be one of: name, email, status, createdAt")
    public String getSortBy() {
        return super.getSortBy();
    }
}
