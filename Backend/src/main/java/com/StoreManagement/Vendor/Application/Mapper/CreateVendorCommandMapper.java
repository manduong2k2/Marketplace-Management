package com.StoreManagement.Vendor.Application.Mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.StoreManagement.Shared.Domain.Contracts.IMapper;
import com.StoreManagement.Vendor.Application.DTO.Command.CreateVendorCommand;
import com.StoreManagement.Vendor.Application.DTO.Request.CreateVendorRequest;

@Component
public class CreateVendorCommandMapper implements IMapper<CreateVendorCommand, CreateVendorRequest> {

    @Override
    public CreateVendorCommand toDomain(CreateVendorRequest request) {
        return new CreateVendorCommand(
            request.getUserId(),
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
    public CreateVendorRequest toEntity(CreateVendorCommand command) {
        return new CreateVendorRequest(
            command.getUserId(),
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
