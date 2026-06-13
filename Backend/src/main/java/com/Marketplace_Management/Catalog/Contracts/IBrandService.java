package com.Marketplace_Management.Catalog.Contracts;

import java.io.IOException;
import java.util.UUID;

import com.Marketplace_Management.Catalog.DTO.Commands.Brand.CreateBrandCommand;
import com.Marketplace_Management.Catalog.DTO.Commands.Brand.GetListBrandCommand;
import com.Marketplace_Management.Catalog.DTO.Commands.Brand.UpdateBrandCommand;
import com.Marketplace_Management.Catalog.DTO.Response.BrandResponse;
import com.Marketplace_Management.Shared.Application.DTO.Responses.PaginatedResponse;

public interface IBrandService {
    public PaginatedResponse<BrandResponse> getAllBrands(GetListBrandCommand command);

    public BrandResponse createBrand(CreateBrandCommand command) throws IOException;

    public BrandResponse getBrand(UUID id);

    public BrandResponse updateBrand(UUID id, UpdateBrandCommand command) throws IOException;

    public void deleteBrand(UUID id);

}
