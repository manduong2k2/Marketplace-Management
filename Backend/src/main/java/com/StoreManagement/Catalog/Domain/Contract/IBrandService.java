package com.StoreManagement.Catalog.Domain.Contract;

import java.util.List;
import java.util.UUID;

import com.StoreManagement.Catalog.Application.DTO.Requests.Brand.CreateBrandRequest;
import com.StoreManagement.Catalog.Application.DTO.Requests.Brand.UpdateBrandRequest;
import com.StoreManagement.Catalog.Application.DTO.Response.BrandResponse;

public interface IBrandService {
    public List<BrandResponse> getAllBrands();

    public BrandResponse createBrand(CreateBrandRequest request);

    public BrandResponse getBrand(UUID id);

    public BrandResponse updateBrand(UUID id, UpdateBrandRequest request);

    public void deleteBrand(UUID id);

}
