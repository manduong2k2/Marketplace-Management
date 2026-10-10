package com.Marketplace_Management.Delivery.Controllers;

import java.util.Map;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Marketplace_Management.Delivery.Contracts.IAddressService;
import com.Marketplace_Management.Delivery.DTOs.Commands.Address.SaveAddressCommand;
import com.Marketplace_Management.Delivery.DTOs.Requests.Address.SaveAddressRequest;
import com.Marketplace_Management.Shared.Annotation.Auth.Authenticated;
import com.Marketplace_Management.Shared.Controllers.BaseController;
import com.Marketplace_Management.Shared.Security.SecurityUtils;

@RestController
@RequestMapping("/api/addresses")
public class AddressController extends BaseController {

    private final IAddressService addressService;

    public AddressController(IAddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("/provinces")
    public ResponseEntity<Map<String, Object>> getProvinces() {
        return objectResponse(addressService.getProvinces());
    }

    @GetMapping("/provinces/{provinceId}/wards")
    public ResponseEntity<Map<String, Object>> getWards(@PathVariable String provinceId) {
        return objectResponse(addressService.getWards(provinceId));
    }

    @Authenticated
    @GetMapping("/mine")
    public ResponseEntity<Map<String, Object>> getMyAddresses() {
        return objectResponse(addressService.getMyAddresses(SecurityUtils.currentUserId()));
    }

    @Authenticated
    @GetMapping("/default")
    public ResponseEntity<Map<String, Object>> getDefaultAddress() {
        return objectResponse(addressService.getDefaultAddress(SecurityUtils.currentUserId()));
    }

    @Authenticated
    @GetMapping("/{addressId}")
    public ResponseEntity<Map<String, Object>> getAddress(@PathVariable Long addressId) {
        return objectResponse(addressService.getAddress(SecurityUtils.currentUserId(), addressId));
    }

    @Authenticated
    @PostMapping
    public ResponseEntity<Map<String, Object>> createAddress(@Valid @RequestBody SaveAddressRequest request) {
        UUID userId = SecurityUtils.currentUserId();
        return createdResponse(addressService.createAddress(userId, SaveAddressCommand.fromRequest(request)));
    }

    @Authenticated
    @PutMapping("/{addressId}")
    public ResponseEntity<Map<String, Object>> updateAddress(
            @PathVariable Long addressId, @Valid @RequestBody SaveAddressRequest request) {
        UUID userId = SecurityUtils.currentUserId();
        return objectResponse(addressService.updateAddress(userId, addressId, SaveAddressCommand.fromRequest(request)));
    }

    @Authenticated
    @PatchMapping("/{addressId}/default")
    public ResponseEntity<Map<String, Object>> setDefaultAddress(@PathVariable Long addressId) {
        return objectResponse(addressService.setDefaultAddress(SecurityUtils.currentUserId(), addressId));
    }

    @Authenticated
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Map<String, Object>> deleteAddress(@PathVariable Long addressId) {
        addressService.deleteAddress(SecurityUtils.currentUserId(), addressId);
        return successResponse("Address deleted successfully");
    }
}
