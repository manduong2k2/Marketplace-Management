package com.Marketplace_Management.Vendor.Controllers;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Marketplace_Management.Shared.Annotation.Auth.Authenticated;
import com.Marketplace_Management.Shared.Controllers.BaseController;
import com.Marketplace_Management.Shared.Security.SecurityUtils;
import com.Marketplace_Management.Vendor.Contracts.IVendorService;
import com.Marketplace_Management.Vendor.DTOs.Command.CreateVendorCommand;
import com.Marketplace_Management.Vendor.DTOs.Command.RegisterVendorCommand;
import com.Marketplace_Management.Vendor.DTOs.Command.UpdateVendorCommand;
import com.Marketplace_Management.Vendor.DTOs.Request.CreateVendorRequest;
import com.Marketplace_Management.Vendor.DTOs.Request.RegisterVendorRequest;
import com.Marketplace_Management.Vendor.DTOs.Request.UpdateVendorRequest;
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
    public ResponseEntity<Map<String, Object>> getAll() {
        List<VendorResponse> vendors = vendorService.getAll();

        return objectResponse(vendors);
    }

    @Authenticated
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @ModelAttribute CreateVendorRequest request) {
        CreateVendorCommand command = CreateVendorCommand.fromRequest(request);
        VendorResponse vendor = vendorService.create(command);

        return createdResponse(vendor);
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<Map<String, Object>> activate(@PathVariable UUID id) {
        vendorService.active(id);

        return simpleResponse("Vendor activated successfully");
    }

    @Authenticated
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable UUID id,
            @Valid @RequestBody UpdateVendorRequest request) {
        UpdateVendorCommand command = UpdateVendorCommand.fromRequest(request);
        vendorService.update(id, command);

        return simpleResponse("Vendor updated successfully");
    }

    @Authenticated
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getByUser() {
        VendorResponse vendor = vendorService.getByUser(SecurityUtils.currentUserId());

        return objectResponse(vendor);
    }

    @Authenticated
    @PostMapping("/me")
    public ResponseEntity<Map<String, Object>> register(@Valid @ModelAttribute RegisterVendorRequest request) {
        RegisterVendorCommand command = RegisterVendorCommand.fromRequest(request);
        VendorResponse vendor = vendorService.register(command);

        return createdResponse(vendor);
    }
}
