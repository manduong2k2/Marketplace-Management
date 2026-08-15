package com.Marketplace_Management.Delivery.Contracts;

import java.util.List;
import java.util.UUID;

import com.Marketplace_Management.Delivery.DTOs.Commands.Address.CreateAddressCommand;
import com.Marketplace_Management.Delivery.DTOs.Commands.Address.GetMyAddressesCommand;
import com.Marketplace_Management.Delivery.DTOs.Commands.Address.UpdateAddressCommand;
import com.Marketplace_Management.Delivery.DTOs.Response.Address.AddressResponse;
import com.Marketplace_Management.Delivery.DTOs.Response.Address.DetailAdressResponse;
import com.Marketplace_Management.Delivery.DTOs.Response.Address.ProvinceResponse;

public interface IAddressService {
    List<ProvinceResponse> getMasterRegions();
    List<AddressResponse> getMyAddresses(UUID userId, GetMyAddressesCommand command);
    DetailAdressResponse getDefaultAddress(UUID userId);
    DetailAdressResponse getAddressById(Long addressId);
    DetailAdressResponse createAddress(UUID userId, CreateAddressCommand command);
    DetailAdressResponse updateAddress(Long addressId, UpdateAddressCommand command);
    void deleteAddress(Long addressId);
}
