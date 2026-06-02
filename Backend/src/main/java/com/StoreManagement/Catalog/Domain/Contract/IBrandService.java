package com.StoreManagement.Catalog.Domain.Contract;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.StoreManagement.Catalog.Application.DTO.Commands.Brand.CreateBrandCommand;
import com.StoreManagement.Catalog.Application.DTO.Commands.Brand.UpdateBrandCommand;
import com.StoreManagement.Catalog.Application.DTO.Response.BrandResponse;

public interface IBrandService {
    public List<BrandResponse> getAllBrands();

    public BrandResponse createBrand(CreateBrandCommand command) throws IOException;

    public BrandResponse getBrand(UUID id);

    public BrandResponse updateBrand(UUID id, UpdateBrandCommand command) throws IOException;

    public void deleteBrand(UUID id);

}
