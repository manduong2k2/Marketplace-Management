package com.Marketplace_Management.Vendor.Services;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.Marketplace_Management.Shared.Configuration.RabbitMqQueues.VendorQueueConfig;
import com.Marketplace_Management.Shared.Contracts.EntityDomainMapper;
import com.Marketplace_Management.Shared.Contracts.IEventPublisher;
import com.Marketplace_Management.Shared.Contracts.IFileService;
import com.Marketplace_Management.Shared.Errors.Exceptions.ResourceNotFoundException;
import com.Marketplace_Management.Shared.Events.DomainEventDispatcher;
import com.Marketplace_Management.Shared.Events.EventOptions;
import com.Marketplace_Management.Shared.Security.SecurityUtils;
import com.Marketplace_Management.Vendor.Contracts.IVendorRepository;
import com.Marketplace_Management.Vendor.Contracts.IVendorService;
import com.Marketplace_Management.Vendor.DTOs.Command.CreateCollectionCommand;
import com.Marketplace_Management.Vendor.DTOs.Command.GetListVendorCommand;
import com.Marketplace_Management.Vendor.DTOs.Command.UpdateCollectionCommand;
import com.Marketplace_Management.Vendor.DTOs.Command.VendorProfileCommand;
import com.Marketplace_Management.Vendor.DTOs.Response.CollectionResponse;
import com.Marketplace_Management.Vendor.DTOs.Response.VendorResponse;
import com.Marketplace_Management.Vendor.Entities.CollectionEntity;
import com.Marketplace_Management.Vendor.Events.VendorActivatedEvent;
import com.Marketplace_Management.Vendor.Models.Collection;
import com.Marketplace_Management.Vendor.Models.Vendor;
import com.Marketplace_Management.Vendor.Models.VendorStatus;
import com.Marketplace_Management.Vendor.Repositories.CollectionJpaRepository;

import jakarta.transaction.Transactional;

@Service
public class VendorService implements IVendorService {
    private static final String VENDOR_NOT_FOUND = "Vendor not found";

    private final IVendorRepository vendorRepository;
    private final CollectionJpaRepository collectionJpaRepository;
    private final DomainEventDispatcher domainEvents;
    private final IEventPublisher eventPublisher;
    private final IFileService fileService;
    private final EntityDomainMapper<Collection, CollectionEntity> collectionMapper;

    @Value ("${spring.application.base-url}")
    private String baseUrl;

    public VendorService(IVendorRepository vendorRepository, CollectionJpaRepository collectionJpaRepository,
            DomainEventDispatcher domainEvents, IEventPublisher eventPublisher, IFileService fileService,
            EntityDomainMapper<Collection, CollectionEntity> collectionMapper) {
        this.vendorRepository = vendorRepository;
        this.collectionJpaRepository = collectionJpaRepository;
        this.domainEvents = domainEvents;
        this.eventPublisher = eventPublisher;
        this.fileService = fileService;
        this.collectionMapper = collectionMapper;
    }

    public List<VendorResponse> getAll(GetListVendorCommand command) {
        return vendorRepository.findAll(command).stream()
                .map(vendor -> vendor.withUrl(baseUrl))
                .toList();
    }

    public VendorResponse getById(UUID vendorId) {
        return new VendorResponse(requireVendor(vendorId)).withUrl(baseUrl);
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
        return saveCollection(collection);
    }

    public CollectionResponse updateCollection(UUID vendorId, UUID collectionId, UpdateCollectionCommand command) {
        requireCollectionOfVendor(vendorId, collectionId);
        Collection collection = Collection.builder()
                .id(collectionId)
                .vendorId(vendorId)
                .name(command.getName())
                .displayOrder(command.getDisplayOrder())
                .build();
        return saveCollection(collection);
    }

    @Transactional
    public void removeCollection(UUID vendorId, UUID collectionId) {
        requireCollectionOfVendor(vendorId, collectionId);
        collectionJpaRepository.deleteById(collectionId);
    }

    /** Admin: creates the vendor of any user. */
    @Transactional
    public VendorResponse create(UUID userId, VendorProfileCommand command) throws IOException {
        if (vendorRepository.existsByUserId(userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vendor of this user already exists");
        }

        Vendor vendor = Vendor.builder()
                .userId(userId)
                .name(command.getName())
                .status(VendorStatus.PENDING)
                .description(command.getDescription())
                .logo(uploadIfPresent(command.getLogo(), "vendors/logo"))
                .banner(uploadIfPresent(command.getBanner(), "vendors/banner"))
                .taxCode(command.getTaxCode())
                .email(command.getEmail())
                .addressId(command.getAddressId())
                .phone(command.getPhone())
                .build();

        vendor = vendorRepository.save(vendor);
        domainEvents.dispatch(vendor, "vendor.created");
        return new VendorResponse(vendor);
    }

    /** The current user registers their own vendor (pending until an admin activates it). */
    @Transactional
    public VendorResponse register(VendorProfileCommand command) throws IOException {
        return create(SecurityUtils.currentUserId(), command);
    }

    @Transactional
    public void active(UUID vendorId) {
        Vendor vendor = requireVendor(vendorId);
        vendor.activate();
        vendor = vendorRepository.save(vendor);
        domainEvents.dispatch(vendor, "vendor.activated");
        // Auth grants the VENDOR role to the owner
        eventPublisher.publish(new VendorActivatedEvent(vendor), new EventOptions(VendorQueueConfig.VENDOR_ACTIVATED_QUEUE, false));
    }

    @Transactional
    public void ban(UUID vendorId) {
        Vendor vendor = requireVendor(vendorId);
        vendor.ban();
        vendor = vendorRepository.save(vendor);
        domainEvents.dispatch(vendor, "vendor.banned");
    }

    /** Updates the profile; logo / banner are replaced only when a new file is uploaded. */
    @Transactional
    public void update(UUID vendorId, VendorProfileCommand command) throws IOException {
        Vendor vendor = requireVendor(vendorId);

        vendor.setName(command.getName());
        vendor.setDescription(command.getDescription());
        vendor.setTaxCode(command.getTaxCode());
        vendor.setEmail(command.getEmail());
        vendor.setAddressId(command.getAddressId());
        vendor.setPhone(command.getPhone());
        String logo = uploadIfPresent(command.getLogo(), "vendors/logo");
        if (logo != null) {
            vendor.setLogo(logo);
        }
        String banner = uploadIfPresent(command.getBanner(), "vendors/banner");
        if (banner != null) {
            vendor.setBanner(banner);
        }

        vendor = vendorRepository.save(vendor);
        domainEvents.dispatch(vendor, "vendor.updated");
    }

    public VendorResponse getByUser(UUID userId) {
        return vendorRepository.findByUserId(userId)
                .map(vendor -> new VendorResponse(vendor).withUrl(baseUrl))
                .orElse(null);
    }

    private Vendor requireVendor(UUID vendorId) {
        return vendorRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException(VENDOR_NOT_FOUND));
    }

    private String uploadIfPresent(MultipartFile file, String folder) throws IOException {
        return file != null && !file.isEmpty() ? fileService.uploadFile(file, folder) : null;
    }

    private CollectionResponse saveCollection(Collection collection) {
        CollectionEntity saved = collectionJpaRepository.save(collectionMapper.toEntity(collection));
        return new CollectionResponse(collectionMapper.toDomain(saved));
    }

    // A collection id from the URL must belong to the vendor in the URL (which the caller is allowed to manage)
    private void requireCollectionOfVendor(UUID vendorId, UUID collectionId) {
        boolean owned = collectionJpaRepository.findById(collectionId)
                .filter(c -> c.getVendor() != null && vendorId.equals(c.getVendor().getId()))
                .isPresent();
        if (!owned) {
            throw new ResourceNotFoundException("Collection not found");
        }
    }
}
