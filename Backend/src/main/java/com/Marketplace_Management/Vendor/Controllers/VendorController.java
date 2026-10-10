package com.Marketplace_Management.Vendor.Controllers;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.access.prepost.PreAuthorize;

import com.Marketplace_Management.Shared.Annotation.Auth.Authenticated;
import com.Marketplace_Management.Shared.Constants.UserRole;
import com.Marketplace_Management.Shared.Controllers.BaseController;
import com.Marketplace_Management.Shared.Security.SecurityUtils;
import com.Marketplace_Management.Vendor.Contracts.IVendorService;
import com.Marketplace_Management.Vendor.DTOs.Command.CreateCollectionCommand;
import com.Marketplace_Management.Vendor.DTOs.Command.GetListVendorCommand;
import com.Marketplace_Management.Vendor.DTOs.Command.UpdateCollectionCommand;
import com.Marketplace_Management.Vendor.DTOs.Command.VendorProfileCommand;
import com.Marketplace_Management.Vendor.DTOs.Request.CreateCollectionRequest;
import com.Marketplace_Management.Vendor.DTOs.Request.CreateVendorRequest;
import com.Marketplace_Management.Vendor.DTOs.Request.GetListVendorRequest;
import com.Marketplace_Management.Vendor.DTOs.Request.RegisterVendorRequest;
import com.Marketplace_Management.Vendor.DTOs.Request.UpdateCollectionRequest;
import com.Marketplace_Management.Vendor.DTOs.Request.UpdateVendorRequest;
import com.Marketplace_Management.Vendor.DTOs.Response.CollectionResponse;
import com.Marketplace_Management.Vendor.DTOs.Response.VendorResponse;

import jakarta.validation.Valid;



@RestController
@RequestMapping("/api/vendors")
public class VendorController extends BaseController{

    private final IVendorService vendorService;

    public VendorController(IVendorService vendorService) {
        this.vendorService = vendorService;
    }

    @Authenticated
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(@Valid @ModelAttribute GetListVendorRequest request) {
        GetListVendorCommand command = GetListVendorCommand.fromRequest(request);
        List<VendorResponse> vendors = vendorService.getAll(command);

        return objectResponse(vendors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getVendor(@PathVariable UUID id) {
        VendorResponse vendor = vendorService.getById(id);
        return objectResponse(vendor);
    }

    @GetMapping("/{id}/collections")
    public ResponseEntity<Map<String, Object>> getCollections(@PathVariable UUID id) {
        List<CollectionResponse> collections = vendorService.getCollections(id);
        return objectResponse(collections);
    }

    @Authenticated
    @PreAuthorize("@vendorSecurity.canManage(#id)")
    @PostMapping("/{id}/collections")
    public ResponseEntity<Map<String, Object>> createCollection(@PathVariable UUID id, @RequestBody CreateCollectionRequest request) {
        CreateCollectionCommand command = CreateCollectionCommand.fromRequest(request);
        CollectionResponse collection = vendorService.createCollection(id, command);
        return createdResponse(collection);
    }

    @Authenticated
    @PreAuthorize("@vendorSecurity.canManage(#id)")
    @DeleteMapping("/{id}/collections/{collectionId}")
    public ResponseEntity<Map<String, Object>> removeCollection(@PathVariable UUID id, @PathVariable UUID collectionId) {
        vendorService.removeCollection(id, collectionId);
        return successResponse("Collection removed successfully");
    }

    @Authenticated
    @PreAuthorize("@vendorSecurity.canManage(#id)")
    @PutMapping("/{id}/collections/{collectionId}")
    public ResponseEntity<Map<String, Object>> updateCollection(@PathVariable UUID id, @PathVariable UUID collectionId, @RequestBody UpdateCollectionRequest request) {
        UpdateCollectionCommand command = UpdateCollectionCommand.fromRequest(request);
        CollectionResponse collection = vendorService.updateCollection(id, collectionId, command);
        return objectResponse(collection);
    }

    // Creates a vendor for any user (userId in the request): admin only. Users register their own via POST /me
    @Authenticated
    @PreAuthorize("hasAuthority('" + UserRole.ADMIN + "')")
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @ModelAttribute CreateVendorRequest request) throws IOException {
        VendorResponse vendor = vendorService.create(request.getUserId(), VendorProfileCommand.fromRequest(request));

        return createdResponse(vendor);
    }

    @Authenticated
    @PreAuthorize("hasAuthority('" + UserRole.ADMIN + "')")
    @PostMapping("/{id}/activate")
    public ResponseEntity<Map<String, Object>> activate(@PathVariable UUID id) {
        vendorService.active(id);

        return successResponse("Vendor activated successfully");
    }

    @Authenticated
    @PreAuthorize("@vendorSecurity.canManage(#id)")
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable UUID id,
            @Valid @ModelAttribute UpdateVendorRequest request) throws IOException {
        vendorService.update(id, VendorProfileCommand.fromRequest(request));

        return successResponse("Vendor updated successfully");
    }

    @Authenticated
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getByUser() {
        VendorResponse vendor = vendorService.getByUser(SecurityUtils.currentUserId());

        return objectResponse(vendor);
    }

    @Authenticated
    @PostMapping("/me")
    public ResponseEntity<Map<String, Object>> register(@Valid @ModelAttribute RegisterVendorRequest request) throws IOException {
        VendorResponse vendor = vendorService.register(VendorProfileCommand.fromRequest(request));

        return createdResponse(vendor);
    }
}
