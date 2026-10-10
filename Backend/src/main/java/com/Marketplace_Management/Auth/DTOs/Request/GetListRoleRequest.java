package com.Marketplace_Management.Auth.DTOs.Request;

import com.Marketplace_Management.Shared.DTOs.Requests.PageRequest;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** search matches the role name or code. */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetListRoleRequest extends PageRequest {
    public GetListRoleRequest() {
        super("name", "asc", 20);
    }

    @Override
    @Max(value = 100, message = "size must not exceed 100")
    public int getSize() {
        return super.getSize();
    }

    @Override
    @Pattern(regexp = "name|code|createdAt", message = "sortBy must be one of: name, code, createdAt")
    public String getSortBy() {
        return super.getSortBy();
    }
}
