package com.Marketplace_Management.Vendor.Services;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.Marketplace_Management.Shared.Contracts.IEventPublisher;
import com.Marketplace_Management.Shared.Contracts.IFileService;
import com.Marketplace_Management.Shared.Contracts.EntityDomainMapper;
import com.Marketplace_Management.Shared.Events.EventOptions;
import com.Marketplace_Management.Shared.Security.SecurityUtils;
import com.Marketplace_Management.Vendor.Contracts.IVendorRepository;
import com.Marketplace_Management.Vendor.Contracts.IVendorService;
import com.Marketplace_Management.Vendor.DTOs.Command.CreateCollectionCommand;
import com.Marketplace_Management.Vendor.DTOs.Command.CreateVendorCommand;
import com.Marketplace_Management.Vendor.DTOs.Command.RegisterVendorCommand;
import com.Marketplace_Management.Vendor.DTOs.Command.UpdateCollectionCommand;
import com.Marketplace_Management.Vendor.DTOs.Command.UpdateVendorCommand;
import com.Marketplace_Management.Vendor.DTOs.Response.CollectionResponse;
import com.Marketplace_Management.Vendor.DTOs.Response.VendorResponse;
import com.Marketplace_Management.Vendor.Entities.CollectionEntity;
import com.Marketplace_Management.Vendor.Models.Collection;
import com.Marketplace_Management.Vendor.Models.Vendor;
import com.Marketplace_Management.Vendor.Models.VendorStatus;
import com.Marketplace_Management.Vendor.Repositories.CollectionJpaRepository;

import jakarta.transaction.Transactional;

@Service
public class VendorService implements IVendorService {

    private final IVendorRepository vendorRepository;
    private final CollectionJpaRepository collectionJpaRepository;
    private final IEventPublisher eventPublisher;
    private final IFileService fileService;
    private final EntityDomainMapper<Collection, CollectionEntity> collectionMapper;

    @Value ("${spring.application.base-url}")
    private String baseUrl;

    public VendorService(IVendorRepository vendorRepository, CollectionJpaRepository collectionJpaRepository, IEventPublisher eventPublisher, IFileService fileService, EntityDomainMapper<Collection, CollectionEntity> collectionMapper) {
        this.vendorRepository = vendorRepository;
        this.collectionJpaRepository = collectionJpaRepository;
        this.eventPublisher = eventPublisher;
        this.fileService = fileService;
        this.collectionMapper = collectionMapper;
    }

    public List<VendorResponse> getAll() {
        return vendorRepository.findAll().stream()
                .map(VendorResponse::new)
                .map(vendor -> vendor.withUrl(baseUrl))
                .toList();
    }
    
    public VendorResponse getById(UUID vendorId) {
        return vendorRepository.findById(vendorId)
                .map(VendorResponse::new)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor not found"));
    }

    public List<CollectionResponse> getCollections(UUID vendorId) {
        var collections = collectionJpaRepository.findByVendorId(vendorId);
        return collections.stream()
                .map(collectionMapper::toDomain)
                .map(CollectionResponse::new)
                .toList();
    }

    public CollectionResponse createCollection(UUID vendorId, CreateCollectionCommand command) {
        Collection collection = Collection.builder()
                .vendorId(vendorId)
                .name(command.getName())
                .displayOrder(command.getDisplayOrder())
                .build();
        CollectionEntity entity = collectionMapper.toEntity(collection);
        CollectionEntity saved = collectionJpaRepository.save(entity);
        return new CollectionResponse(collectionMapper.toDomain(saved));
    }
    
    public CollectionResponse updateCollection(UUID vendorId, UUID collectionId, UpdateCollectionCommand command) {
        Collection collection = Collection.builder()
                .id(collectionId)
                .vendorId(vendorId)
                .name(command.getName())
                .displayOrder(command.getDisplayOrder())
                .build();
        CollectionEntity entity = collectionMapper.toEntity(collection);
        CollectionEntity saved = collectionJpaRepository.save(entity);
        return new CollectionResponse(collectionMapper.toDomain(saved));
    }

    @Transactional
    public void removeCollection(UUID vendorId, UUID collectionId) {
        collectionJpaRepository.deleteById(collectionId);
    }

    @Transactional
    public VendorResponse create(CreateVendorCommand command) throws IOException{
        if (vendorRepository.existsByUserId(command.getUserId()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vendor of this user already exists");

        String logoUrl = fileService.uploadFile(command.getLogo(), "vendors/logo");
        String bannerUrl = fileService.uploadFile(command.getBanner(), "vendors/banner");

        Vendor vendor = new Vendor(
                null,
                command.getUserId(),
                command.getName(),
                VendorStatus.PENDING,
                command.getDescription(),
                logoUrl,
                bannerUrl,
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
    public VendorResponse register(RegisterVendorCommand command) throws IOException {
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

        return vendor != null ? new VendorResponse(vendor).withUrl(baseUrl) : null;
    }

    @Async
    private void publishDomainEvents(Vendor vendor, String queue) {
        vendor.getDomainEvents()
                .forEach(event -> eventPublisher.publish(event, new EventOptions(queue, false)));

        vendor.clearDomainEvents();
    }
}
