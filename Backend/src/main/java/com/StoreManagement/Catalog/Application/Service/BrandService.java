package com.StoreManagement.Catalog.Application.Service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.StoreManagement.Catalog.Application.DTO.Requests.Brand.CreateBrandRequest;
import com.StoreManagement.Catalog.Application.DTO.Requests.Brand.UpdateBrandRequest;
import com.StoreManagement.Catalog.Application.DTO.Response.BrandResponse;
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

    public List<BrandResponse> getAllBrands() {
        return brandRepository.findAll().stream()
                .map(brand -> new BrandResponse(brand, baseUrl))
                .toList();
    }

    public BrandResponse getBrand(UUID brandId) {
        return brandRepository.findById(brandId)
                .map(brand -> new BrandResponse(brand, baseUrl))
                .orElseThrow(() -> new RuntimeException("Brand not found"));
    }

    @Transactional
    public BrandResponse createBrand(CreateBrandRequest request) throws java.io.IOException {
        Brand brand = new Brand(
                null,
                request.getName(),
                null,
                request.getDescription()
        );

        brand = brandRepository.save(brand);

        if(request.getImage() != null) {
            String imageUrl = fileService.uploadFile(request.getImage(), "catalog/brands/");
            brand.setImage(imageUrl);
            brand = brandRepository.save(brand);
        }

        publishDomainEvents(brand, "brand.created");

        return new BrandResponse(brand, baseUrl);
    }

    @Transactional
    public BrandResponse updateBrand(UUID brandId, UpdateBrandRequest request) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        brand.setName(request.getName());
        brand.setDescription(request.getDescription());

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
