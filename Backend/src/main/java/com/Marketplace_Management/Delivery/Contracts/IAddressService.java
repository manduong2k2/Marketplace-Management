package com.Marketplace_Management.Delivery.Contracts;

import java.util.List;
import java.util.UUID;

import com.Marketplace_Management.Delivery.DTOs.Commands.Address.SaveAddressCommand;
import com.Marketplace_Management.Delivery.DTOs.Response.Address.AddressResponse;
import com.Marketplace_Management.Delivery.DTOs.Response.Address.RegionResponse;

public interface IAddressService {
    List<RegionResponse> getProvinces();
    List<RegionResponse> getWards(String provinceId);

    List<AddressResponse> getMyAddresses(UUID userId);
    AddressResponse getDefaultAddress(UUID userId);
    AddressResponse getAddress(UUID userId, Long addressId);
    AddressResponse createAddress(UUID userId, SaveAddressCommand command);
    AddressResponse updateAddress(UUID userId, Long addressId, SaveAddressCommand command);
    AddressResponse setDefaultAddress(UUID userId, Long addressId);
    void deleteAddress(UUID userId, Long addressId);
}
