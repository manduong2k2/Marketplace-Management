package com.Marketplace_Management.Delivery.Services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.Marketplace_Management.Delivery.Contracts.IAddressRepository;
import com.Marketplace_Management.Delivery.Contracts.IAddressService;
import com.Marketplace_Management.Delivery.DTOs.Commands.Address.CreateAddressCommand;
import com.Marketplace_Management.Delivery.DTOs.Commands.Address.GetMyAddressesCommand;
import com.Marketplace_Management.Delivery.DTOs.Commands.Address.UpdateAddressCommand;
import com.Marketplace_Management.Delivery.DTOs.Response.Address.AddressResponse;
import com.Marketplace_Management.Delivery.DTOs.Response.Address.DetailAdressResponse;
import com.Marketplace_Management.Delivery.DTOs.Response.Address.ProvinceResponse;
import com.Marketplace_Management.Delivery.DTOs.Response.Address.WardResponse;
import com.Marketplace_Management.Delivery.Models.Address;
import com.Marketplace_Management.Delivery.Models.Province;
import com.Marketplace_Management.Delivery.Models.Ward;
import com.Marketplace_Management.Shared.Errors.Exceptions.BadRequestException;
import com.Marketplace_Management.Shared.Errors.Exceptions.ResourceNotFoundException;

@Service
public class AddressService implements IAddressService {
    private final IAddressRepository addressRepository;

    public AddressService(IAddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public List<ProvinceResponse> getMasterRegions() {
        List<Province> provinces = addressRepository.getMasterRegions();
        return provinces.stream()
                .map(this::mapToProvinceResponse)
                .collect(Collectors.toList());
    }

    public List<AddressResponse> getMyAddresses(UUID userId, GetMyAddressesCommand command) {
        List<Address> addresses = addressRepository.getMyAddresses(userId);
        return addresses.stream()
                .map(this::mapToAddressResponse)
                .collect(Collectors.toList());
    }

    public DetailAdressResponse getDefaultAddress(UUID userId) {
        Address address = addressRepository.getMyDefaultAddress(userId);
        if(address == null) {
            throw new ResourceNotFoundException("Default address not found");
        }
        return mapToDetailAddressResponse(address);
    }

    public DetailAdressResponse getAddressById(Long addressId) {
        Address address = addressRepository.getAddressById(addressId);
        if (address == null) {
            return null;
        }
        return mapToDetailAddressResponse(address);
    }

    public DetailAdressResponse createAddress(UUID userId, CreateAddressCommand command) {
        Address defAddress = addressRepository.getMyDefaultAddress(userId);
        boolean isDefault = command.getIsDefault() != null ? command.getIsDefault() : defAddress == null;

        if (isDefault) {
            Address defaultAddress = addressRepository.getMyDefaultAddress(userId);
            if (defaultAddress != null) {
                defaultAddress.setIsDefault(false);
                addressRepository.upsirtAddress(defaultAddress);
            }
        }

        Address address = Address.builder()
                .userId(userId)
                .title(command.getTitle())
                .streetName(command.getStreetName())
                .houseNumber(command.getHouseNumber())
                .detail(command.getDetail())
                .wardId(command.getWardId())
                .isDefault(isDefault)
                .build();

        Address created = addressRepository.createAddress(address);
        return mapToDetailAddressResponse(created);
    }

    public DetailAdressResponse updateAddress(Long addressId, UpdateAddressCommand command) {
        Address address = addressRepository.getAddressById(addressId);

        if (address == null) {
            throw new ResourceNotFoundException("Address not found");
        }

        Address defaultAddress = addressRepository.getMyDefaultAddress(address.getUserId());

        if (command.getIsDefault() && defaultAddress != null) {
            defaultAddress.setIsDefault(false);
            addressRepository.upsirtAddress(defaultAddress);
        }

        if (defaultAddress != null && defaultAddress.getId().equals(addressId)) {
            command.setIsDefault(true);
        }

        var newAddress = Address.builder()
                .id(address.getId())
                .userId(address.getUserId())
                .title(command.getTitle())
                .streetName(command.getStreetName())
                .houseNumber(command.getHouseNumber())
                .detail(command.getDetail())
                .wardId(command.getWardId())
                .isDefault(command.getIsDefault())
                .build();
        Address updatedAddress = addressRepository.upsirtAddress(newAddress);
        return mapToDetailAddressResponse(updatedAddress);
    }

    public void deleteAddress(Long addressId) {
        Address address = addressRepository.getAddressById(addressId);
        if (address == null) {
            throw new ResourceNotFoundException("Address not found");
        }
        if(address.getIsDefault()) {
            throw new BadRequestException("Cannot delete default address");
        }
        addressRepository.deleteAddress(addressId);
    }

    private AddressResponse mapToAddressResponse(Address address) {
        Ward ward = address.getWard();
        String wardName = ward != null ? ward.getName() : null;
        String provinceName = ward != null && ward.getProvince() != null ? ward.getProvince().getName() : null;

        return AddressResponse.builder()
                .id(address.getId())
                .userId(address.getUserId())
                .title(address.getTitle())
                .ward(wardName)
                .province(provinceName)
                .streetName(address.getStreetName())
                .houseNumber(address.getHouseNumber())
                .detail(address.getDetail())
                .isDefault(address.getIsDefault())
                .build();
    }

    private ProvinceResponse mapToProvinceResponse(Province province) {
        List<WardResponse> wardResponses = province.getWards() != null
                ? province.getWards().stream()
                        .map(this::mapToWardResponse)
                        .collect(Collectors.toList())
                : null;

        return ProvinceResponse.builder()
                .id(province.getId())
                .name(province.getName())
                .fullName(province.getFullName())
                .wards(wardResponses)
                .build();
    }

    private WardResponse mapToWardResponse(Ward ward) {
        WardResponse wardResponse = new WardResponse();
        wardResponse.setId(ward.getId());
        wardResponse.setName(ward.getName());
        wardResponse.setFullName(ward.getFullName());
        return wardResponse;
    }

    private DetailAdressResponse mapToDetailAddressResponse(Address address) {
        return DetailAdressResponse.builder()
                .id(address.getId())
                .userId(address.getUserId())
                .streetName(address.getStreetName())
                .houseNumber(address.getHouseNumber())
                .detail(address.getDetail())
                .isDefault(address.getIsDefault())
                .ward(DetailAdressResponse.WardResponse.builder()
                        .id(address.getWard().getId())
                        .name(address.getWard().getName())
                        .fullName(address.getWard().getFullName())
                        .province(DetailAdressResponse.WardResponse.ProvinceResponse.builder()
                                .id(address.getWard().getProvince().getId())
                                .name(address.getWard().getProvince().getName())
                                .fullName(address.getWard().getProvince().getFullName())
                                .build())
                        .build())
                .title(address.getTitle())
                .build();
    }
}
