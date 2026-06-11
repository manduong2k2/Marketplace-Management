package com.StoreManagement.Catalog.Domain.Contract;

import java.io.IOException;
import java.util.UUID;

import com.StoreManagement.Catalog.Application.DTO.Commands.Brand.CreateBrandCommand;
import com.StoreManagement.Catalog.Application.DTO.Commands.Brand.GetListBrandCommand;
import com.StoreManagement.Catalog.Application.DTO.Commands.Brand.UpdateBrandCommand;
import com.StoreManagement.Catalog.Application.DTO.Response.BrandResponse;
import com.StoreManagement.Shared.Application.DTO.Responses.PaginatedResponse;

public interface IBrandService {
    public PaginatedResponse<BrandResponse> getAllBrands(GetListBrandCommand command);

    public BrandResponse createBrand(CreateBrandCommand command) throws IOException;

    public BrandResponse getBrand(UUID id);

    public BrandResponse updateBrand(UUID id, UpdateBrandCommand command) throws IOException;

    public void deleteBrand(UUID id);

}
