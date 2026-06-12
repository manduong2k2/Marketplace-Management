package com.Marketplace_Management.Vendor.Domain.Contract;

import java.util.List;
import java.util.UUID;

import com.Marketplace_Management.Vendor.Application.DTO.Command.CreateVendorCommand;
import com.Marketplace_Management.Vendor.Application.DTO.Command.RegisterVendorCommand;
import com.Marketplace_Management.Vendor.Application.DTO.Command.UpdateVendorCommand;
import com.Marketplace_Management.Vendor.Application.DTO.Response.VendorResponse;

public interface IVendorService {
    List<VendorResponse> getAll();

    VendorResponse create(CreateVendorCommand command);

    VendorResponse register(RegisterVendorCommand command);

    void update(UUID vendorId, UpdateVendorCommand command);

    void active(UUID vendorId);

    void ban(UUID vendorId);

    VendorResponse getByUser(UUID userId);
}
