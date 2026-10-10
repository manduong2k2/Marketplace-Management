package com.Marketplace_Management.Vendor.Contracts;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.Marketplace_Management.Vendor.DTOs.Command.CreateCollectionCommand;
import com.Marketplace_Management.Vendor.DTOs.Command.GetListVendorCommand;
import com.Marketplace_Management.Vendor.DTOs.Command.UpdateCollectionCommand;
import com.Marketplace_Management.Vendor.DTOs.Command.VendorProfileCommand;
import com.Marketplace_Management.Vendor.DTOs.Response.CollectionResponse;
import com.Marketplace_Management.Vendor.DTOs.Response.VendorResponse;

public interface IVendorService {
    List<VendorResponse> getAll(GetListVendorCommand command);

    VendorResponse create(UUID userId, VendorProfileCommand command) throws IOException;

    VendorResponse register(VendorProfileCommand command) throws IOException;

    void update(UUID vendorId, VendorProfileCommand command) throws IOException;

    void active(UUID vendorId);

    void ban(UUID vendorId);

    VendorResponse getByUser(UUID userId);

    VendorResponse getById(UUID vendorId);

    List<CollectionResponse> getCollections(UUID vendorId);

    CollectionResponse createCollection(UUID vendorId, CreateCollectionCommand command);

    CollectionResponse updateCollection(UUID vendorId, UUID collectionId, UpdateCollectionCommand command);

    public void removeCollection(UUID vendorId, UUID collectionId);
}
