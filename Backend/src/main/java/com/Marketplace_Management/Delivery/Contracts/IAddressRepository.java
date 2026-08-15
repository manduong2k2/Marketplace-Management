package com.Marketplace_Management.Delivery.Contracts;

import java.util.List;
import java.util.UUID;

import com.Marketplace_Management.Delivery.Models.Address;
import com.Marketplace_Management.Delivery.Models.Province;

public interface IAddressRepository {
    List<Province> getMasterRegions();
    List<Address> getMyAddresses(UUID userId);
    Address getAddressById(Long addressId);
    Address createAddress(Address address);
    Address upsirtAddress(Address address);
    void deleteAddress(Long addressId);
    Address getMyDefaultAddress(UUID userId);
}
