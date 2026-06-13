package com.Marketplace_Management.Vendor.Contracts;

import java.util.List;
import java.util.UUID;

import com.Marketplace_Management.Vendor.DTOs.Command.CreateVendorCommand;
import com.Marketplace_Management.Vendor.DTOs.Command.RegisterVendorCommand;
import com.Marketplace_Management.Vendor.DTOs.Command.UpdateVendorCommand;
import com.Marketplace_Management.Vendor.DTOs.Response.VendorResponse;

public interface IVendorService {
    List<VendorResponse> getAll();

    VendorResponse create(CreateVendorCommand command);

    VendorResponse register(RegisterVendorCommand command);

    void update(UUID vendorId, UpdateVendorCommand command);

    void active(UUID vendorId);

    void ban(UUID vendorId);

    VendorResponse getByUser(UUID userId);
}
