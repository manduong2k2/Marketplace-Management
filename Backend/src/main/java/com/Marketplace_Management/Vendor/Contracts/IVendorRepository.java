package com.Marketplace_Management.Vendor.Contracts;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.Marketplace_Management.Vendor.Models.Vendor;

public interface IVendorRepository {
    Vendor save(Vendor vendor);

    List<Vendor> findAll();

    Optional<Vendor> findById(UUID id);

    Optional<Vendor> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);
}
