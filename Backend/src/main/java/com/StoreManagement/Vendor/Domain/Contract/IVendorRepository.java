package com.StoreManagement.Vendor.Domain.Contract;

import com.StoreManagement.Vendor.Domain.Model.Vendor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IVendorRepository {
    Vendor save(Vendor vendor);

    List<Vendor> findAll();

    Optional<Vendor> findById(UUID id);

    Optional<Vendor> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);
}
