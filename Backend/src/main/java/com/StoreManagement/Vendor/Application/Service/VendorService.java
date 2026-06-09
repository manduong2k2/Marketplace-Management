package com.StoreManagement.Vendor.Application.Service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.StoreManagement.Shared.Domain.Contracts.IEventPublisher;
import com.StoreManagement.Shared.Infrastructure.Event.EventOptions;
import com.StoreManagement.Shared.Infrastructure.Security.SecurityUtils;
import com.StoreManagement.Vendor.Application.DTO.Command.CreateVendorCommand;
import com.StoreManagement.Vendor.Application.DTO.Command.RegisterVendorCommand;
import com.StoreManagement.Vendor.Application.DTO.Command.UpdateVendorCommand;
import com.StoreManagement.Vendor.Application.DTO.Response.VendorResponse;
import com.StoreManagement.Vendor.Domain.Contract.IVendorRepository;
import com.StoreManagement.Vendor.Domain.Contract.IVendorService;
import com.StoreManagement.Vendor.Domain.Model.Vendor;
import com.StoreManagement.Vendor.Domain.Model.VendorStatus;

import jakarta.transaction.Transactional;

@Service
public class VendorService implements IVendorService {

    @Autowired
    public IVendorRepository vendorRepository;
    @Autowired
    public IEventPublisher eventPublisher;

    public List<VendorResponse> getAll() {
        return vendorRepository.findAll().stream()
                .map(VendorResponse::new)
                .toList();
    }

    @Transactional
    public VendorResponse create(CreateVendorCommand command) {
        if (vendorRepository.existsByUserId(command.getUserId()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vendor of this user already exists");

        Vendor vendor = new Vendor(
                null,
                command.getUserId(),
                command.getName(),
                VendorStatus.PENDING,
                command.getDescription(),
                null,
                null,
                command.getTaxCode(),
                command.getEmail(),
                command.getAddressId(),
                command.getPhone()
        );

        vendor = vendorRepository.save(vendor);

        publishDomainEvents(vendor, "vendor.created");

        return new VendorResponse(vendor);
    }

    @Transactional
    public VendorResponse register(RegisterVendorCommand command) {
        UUID userId = SecurityUtils.currentUserId();

        CreateVendorCommand commandWithUser = new CreateVendorCommand(
                userId,
                command.getName(),
                command.getDescription(),
                command.getLogo(),
                command.getBanner(),
                command.getTaxCode(),
                command.getEmail(),
                command.getAddressId(),
                command.getPhone()
        );

        return this.create(commandWithUser);
    }

    @Transactional
    public void active(UUID vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor not found"));

        vendor.activate();

        vendor = vendorRepository.save(vendor);

        publishDomainEvents(vendor, "vendor.activated");
    }

    @Transactional
    public void ban(UUID vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor not found"));

        vendor.ban();

        vendor = vendorRepository.save(vendor);

        publishDomainEvents(vendor, "vendor.banned");
    }

    @Transactional
    public void update(UUID vendorId, UpdateVendorCommand command) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor not found"));

        Vendor updated = new Vendor(
                vendor.getId(),
                vendor.getUserId(),
                command.getName(),
                vendor.getStatus(),
                command.getDescription(),
                null,
                null,
                command.getTaxCode(),
                command.getEmail(),
                command.getAddressId(),
                command.getPhone()
        );

        updated = vendorRepository.save(updated);

        publishDomainEvents(updated, "vendor.updated");
    }

    public VendorResponse getByUser(UUID userId) {

        Vendor vendor = vendorRepository.findByUserId(userId)
                .orElse(null);

        return vendor != null ? new VendorResponse(vendor) : null;
    }

    @Async
    private void publishDomainEvents(Vendor vendor, String queue) {
        vendor.getDomainEvents()
                .forEach(event -> eventPublisher.publish(event, new EventOptions(queue, false)));

        vendor.clearDomainEvents();
    }
}
