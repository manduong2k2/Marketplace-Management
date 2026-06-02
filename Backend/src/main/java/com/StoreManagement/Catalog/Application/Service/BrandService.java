package com.StoreManagement.Catalog.Application.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.StoreManagement.Catalog.Application.DTO.Commands.Brand.CreateBrandCommand;
import com.StoreManagement.Catalog.Application.DTO.Commands.Brand.GetListBrandCommand;
import com.StoreManagement.Catalog.Application.DTO.Commands.Brand.UpdateBrandCommand;
import com.StoreManagement.Catalog.Application.DTO.Response.BrandResponse;
import com.StoreManagement.Catalog.Application.DTO.Response.PaginatedResponse;
import com.StoreManagement.Catalog.Domain.Contract.IBrandRepository;
import com.StoreManagement.Catalog.Domain.Contract.IBrandService;
import com.StoreManagement.Catalog.Domain.Models.Brand;
import com.StoreManagement.Shared.Application.Contracts.IFileService;
import com.StoreManagement.Shared.Domain.Contracts.IEventPublisher;
import com.StoreManagement.Shared.Infrastructure.Event.EventOptions;

import jakarta.transaction.Transactional;

@Service
public class BrandService implements IBrandService {
    @Autowired
    public IBrandRepository brandRepository;
    @Autowired
    public IEventPublisher eventPublisher; 
    @Autowired
    public IFileService fileService;
    
    @Value("${spring.application.base-url:http://localhost:8080}")
    private String baseUrl;

    public PaginatedResponse<BrandResponse> getAllBrands(GetListBrandCommand command) {
        PaginatedResponse<Brand> brands = brandRepository.findAll(command);
        List<BrandResponse> brandResponses = brands.getData().stream()
                .map(brand -> new BrandResponse(brand, baseUrl))
                .toList();
        return new PaginatedResponse<>(
                brandResponses,
                brands.getCurrentPage(),
                brands.getPageSize(),
                brands.getTotalElements()
        );
    }

    public BrandResponse getBrand(UUID brandId) {
        return brandRepository.findById(brandId)
                .map(brand -> new BrandResponse(brand, baseUrl))
                .orElseThrow(() -> new RuntimeException("Brand not found"));
    }

    @Transactional
    public BrandResponse createBrand(CreateBrandCommand command) throws java.io.IOException {
        Brand brand = new Brand(
                null,
                command.getName(),
                null,
                command.getDescription()
        );

        brand = brandRepository.save(brand);

        if(command.getImage() != null) {
            String imageUrl = fileService.uploadFile(command.getImage(), "catalog/brands/");
            brand.setImage(imageUrl);
            brand = brandRepository.save(brand);
        }

        publishDomainEvents(brand, "brand.created");

        return new BrandResponse(brand, baseUrl);
    }

    @Transactional
    public BrandResponse updateBrand(UUID brandId, UpdateBrandCommand command) throws IOException {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        brand.setName(command.getName());
        brand.setDescription(command.getDescription());

        if(command.getImage() != null) {
            String currentImage = brand.getImage();
            String imageUrl = fileService.uploadFile(command.getImage(), "catalog/brands/");
            brand.setImage(imageUrl);
            if(currentImage != null) {
                fileService.deleteFile(currentImage);
            }
        }

        brand = brandRepository.save(brand);

        publishDomainEvents(brand, "brand.updated");
        
        return new BrandResponse(brand, baseUrl);
    }

    @Transactional
    public void deleteBrand(UUID brandId) {
        brandRepository.delete(brandId);
    }

    @Async
    private void publishDomainEvents(Brand brand, String queue) {
        brand.getDomainEvents()
                .forEach(event -> eventPublisher.publish(event, new EventOptions(queue, false)));

        brand.clearDomainEvents();
    }
}
