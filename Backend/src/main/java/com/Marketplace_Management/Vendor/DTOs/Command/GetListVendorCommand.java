package com.Marketplace_Management.Vendor.DTOs.Command;

import com.Marketplace_Management.Vendor.DTOs.Request.GetListVendorRequest;
import com.Marketplace_Management.Shared.DTOs.Commands.PageCommand;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GetListVendorCommand extends PageCommand {
    public static GetListVendorCommand fromRequest(GetListVendorRequest request) {
        return GetListVendorCommand.builder().paging(request).build();
    }
}
