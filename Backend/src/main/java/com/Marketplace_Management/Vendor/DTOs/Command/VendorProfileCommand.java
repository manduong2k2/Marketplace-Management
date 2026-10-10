package com.Marketplace_Management.Vendor.DTOs.Command;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.Marketplace_Management.Shared.DTOs.Commands.BaseCommand;
import com.Marketplace_Management.Vendor.DTOs.Request.VendorProfileFields;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** Vendor profile data for create (admin), register (current user) and update. */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class VendorProfileCommand extends BaseCommand {
    private String name;
    private String description;
    private MultipartFile logo;
    private MultipartFile banner;
    private String taxCode;
    private String email;
    private UUID addressId;
    private String phone;

    public static VendorProfileCommand fromRequest(VendorProfileFields request) {
        return VendorProfileCommand.builder()
                .name(safeTrim(request.getName()))
                .description(blankToNull(request.getDescription()))
                .logo(request.getLogo())
                .banner(request.getBanner())
                .taxCode(blankToNull(request.getTaxCode()))
                .email(blankToNull(request.getEmail()))
                .addressId(uuidOrNull(request.getAddressId()))
                .phone(blankToNull(request.getPhone()))
                .build();
    }
}
