package com.Marketplace_Management.Vendor.Application.Mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.Marketplace_Management.Shared.Domain.Contracts.IMapper;
import com.Marketplace_Management.Vendor.Application.DTO.Command.RegisterVendorCommand;
import com.Marketplace_Management.Vendor.Application.DTO.Request.RegisterVendorRequest;

@Component
public class RegisterVendorCommandMapper implements IMapper<RegisterVendorCommand, RegisterVendorRequest>{
    @Override
    public RegisterVendorCommand toDomain(RegisterVendorRequest request) {
        return new RegisterVendorCommand(
            request.getName(),
            request.getDescription(),
            null,
            null,
            request.getTaxCode(),
            request.getEmail(),
            request.getAddressId() != null ? UUID.fromString(request.getAddressId()) : null,
            request.getPhone()
        );
    }

    @Override
    public RegisterVendorRequest toEntity(RegisterVendorCommand command) {
        return new RegisterVendorRequest(
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
