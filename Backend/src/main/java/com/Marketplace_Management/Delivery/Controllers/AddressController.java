package com.Marketplace_Management.Delivery.Controllers;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Marketplace_Management.Delivery.Contracts.IAddressService;
import com.Marketplace_Management.Delivery.DTOs.Commands.Address.CreateAddressCommand;
import com.Marketplace_Management.Delivery.DTOs.Commands.Address.GetMyAddressesCommand;
import com.Marketplace_Management.Delivery.DTOs.Commands.Address.UpdateAddressCommand;
import com.Marketplace_Management.Delivery.DTOs.Requests.Address.CreateAddressRequest;
import com.Marketplace_Management.Delivery.DTOs.Requests.Address.GetMyAddressesRequest;
import com.Marketplace_Management.Delivery.DTOs.Requests.Address.UpdateAddressRequest;
import com.Marketplace_Management.Delivery.DTOs.Response.Address.AddressResponse;
import com.Marketplace_Management.Delivery.DTOs.Response.Address.DetailAdressResponse;
import com.Marketplace_Management.Delivery.DTOs.Response.Address.ProvinceResponse;
import com.Marketplace_Management.Shared.Annotation.Auth.Authenticated;
import com.Marketplace_Management.Shared.Security.SecurityUtils;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final IAddressService addressService;

    public AddressController(IAddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("/master-regions")
    public ResponseEntity<HashMap<String, Object>> getMasterRegions() {
        List<ProvinceResponse> provinces = addressService.getMasterRegions();
        HashMap<String, Object> response = new HashMap<>();
        response.put("data", provinces);
        return ResponseEntity.ok(response);
    }

    @Authenticated
    @GetMapping("/mine")
    public ResponseEntity<HashMap<String, Object>> getMyAddresses(@Valid GetMyAddressesRequest request) {
        UUID userId = SecurityUtils.currentUserId();
        GetMyAddressesCommand command = GetMyAddressesCommand.fromRequest(request);
        List<AddressResponse> addresses = addressService.getMyAddresses(userId, command);
        HashMap<String, Object> response = new HashMap<>();
        response.put("data", addresses);
        return ResponseEntity.ok(response);
    }

    @Authenticated
    @GetMapping("/default")
    public ResponseEntity<HashMap<String, Object>> getDefaultAddress() {
        UUID userId = SecurityUtils.currentUserId();
        DetailAdressResponse address = addressService.getDefaultAddress(userId);
        HashMap<String, Object> response = new HashMap<>();
        response.put("data", address);
        return ResponseEntity.ok(response);
    }

    @Authenticated
    @GetMapping("/{addressId}")
    public ResponseEntity<HashMap<String, Object>> getAddressById(@PathVariable Long addressId) {
        DetailAdressResponse address = addressService.getAddressById(addressId);
        HashMap<String, Object> response = new HashMap<>();
        response.put("data", address);
        return ResponseEntity.ok(response);
    }

    @Authenticated
    @PostMapping
    public ResponseEntity<HashMap<String, Object>> createAddress(@Valid @RequestBody CreateAddressRequest request) {
        UUID userId = SecurityUtils.currentUserId();
        CreateAddressCommand command = CreateAddressCommand.fromRequest(request);
        DetailAdressResponse address = addressService.createAddress(userId, command);
        HashMap<String, Object> response = new HashMap<>();
        response.put("data", address);
        return ResponseEntity.status(201).body(response);
    }

    @Authenticated
    @PutMapping("/{addressId}")
    public ResponseEntity<HashMap<String, Object>> updateAddress(
            @PathVariable Long addressId,
            @Valid @RequestBody UpdateAddressRequest request) {
        UpdateAddressCommand command = UpdateAddressCommand.fromRequest(request);
        DetailAdressResponse address = addressService.updateAddress(addressId, command);
        HashMap<String, Object> response = new HashMap<>();
        response.put("data", address);
        return ResponseEntity.ok(response);
    }

    @Authenticated
    @DeleteMapping("/{addressId}")
    public ResponseEntity<HashMap<String, Object>> deleteAddress(@PathVariable Long addressId) {
        addressService.deleteAddress(addressId);
        HashMap<String, Object> response = new HashMap<>();
        response.put("message", "Address deleted successfully");
        return ResponseEntity.ok(response);
    }
}
