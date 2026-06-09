package com.StoreManagement.Vendor.Application.Mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.StoreManagement.Shared.Domain.Contracts.IMapper;
import com.StoreManagement.Vendor.Application.DTO.Command.UpdateVendorCommand;
import com.StoreManagement.Vendor.Application.DTO.Request.UpdateVendorRequest;

@Component
public class UpdateVendorCommandMapper implements IMapper<UpdateVendorCommand, UpdateVendorRequest> {

    @Override
    public UpdateVendorCommand toDomain(UpdateVendorRequest request) {
        return new UpdateVendorCommand(
            request.getName(),
            request.getDescription(),
            request.getLogo(),
            request.getBanner(),
            request.getTaxCode(),
            request.getEmail(),
            request.getAddressId() != null ? UUID.fromString(request.getAddressId()) : null,
            request.getPhone()
        );
    }

    @Override
    public UpdateVendorRequest toEntity(UpdateVendorCommand command) {
        return new UpdateVendorRequest(
            null,
            command.getName(),
            command.getDescription(),
            command.getLogo(),
            command.getBanner(),
            command.getTaxCode(),
            command.getEmail(),
            command.getAddressId() != null ? command.getAddressId().toString() : null,
            command.getPhone()
        );
    }
}
