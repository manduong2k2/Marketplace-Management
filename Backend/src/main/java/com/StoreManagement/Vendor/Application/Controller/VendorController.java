package com.StoreManagement.Vendor.Application.Controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.StoreManagement.Shared.Application.Annotation.Auth.Authenticated;
import com.StoreManagement.Shared.Application.Controllers.BaseController;
import com.StoreManagement.Shared.Infrastructure.Security.SecurityUtils;
import com.StoreManagement.Vendor.Application.DTO.Command.CreateVendorCommand;
import com.StoreManagement.Vendor.Application.DTO.Command.RegisterVendorCommand;
import com.StoreManagement.Vendor.Application.DTO.Command.UpdateVendorCommand;
import com.StoreManagement.Vendor.Application.DTO.Request.CreateVendorRequest;
import com.StoreManagement.Vendor.Application.DTO.Request.RegisterVendorRequest;
import com.StoreManagement.Vendor.Application.DTO.Request.UpdateVendorRequest;
import com.StoreManagement.Vendor.Application.DTO.Response.VendorResponse;
import com.StoreManagement.Vendor.Application.Mapper.CreateVendorCommandMapper;
import com.StoreManagement.Vendor.Application.Mapper.RegisterVendorCommandMapper;
import com.StoreManagement.Vendor.Application.Mapper.UpdateVendorCommandMapper;
import com.StoreManagement.Vendor.Domain.Contract.IVendorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/vendors")
public class VendorController extends BaseController{

    @Autowired
    private IVendorService vendorService;
    @Autowired
    private CreateVendorCommandMapper createMapper;
    @Autowired
    private UpdateVendorCommandMapper updateMapper;
    @Autowired
    private RegisterVendorCommandMapper registerMapper;

    @Authenticated
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        List<VendorResponse> vendors = vendorService.getAll();

        return objectResponse(vendors);
    }

    @Authenticated
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @ModelAttribute CreateVendorRequest request) {
        CreateVendorCommand command = createMapper.toDomain(request);
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
        UpdateVendorCommand command = updateMapper.toDomain(request);
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
        RegisterVendorCommand command = registerMapper.toDomain(request);
        VendorResponse vendor = vendorService.register(command);

        return createdResponse(vendor);
    }
}
