package com.StoreManagement.Catalog.Application.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.StoreManagement.Catalog.Application.DTO.Commands.Product.CreateProductCommand;
import com.StoreManagement.Catalog.Application.DTO.Commands.Product.UpdateProductCommand;
import com.StoreManagement.Catalog.Application.DTO.Response.ProductResponse;
import com.StoreManagement.Catalog.Domain.Contract.IProductRepository;
import com.StoreManagement.Catalog.Domain.Contract.IProductService;
import com.StoreManagement.Catalog.Domain.Models.Product;
import com.StoreManagement.Catalog.Domain.Models.ProductStatus;
import com.StoreManagement.Catalog.Infrastructure.Persistence.Entity.ProductEntity;
import com.StoreManagement.Shared.Application.Contracts.IFileService;
import com.StoreManagement.Shared.Domain.File;
import com.StoreManagement.Shared.Domain.Contracts.IEventPublisher;
import com.StoreManagement.Shared.Domain.Contracts.IFileRepository;
import com.StoreManagement.Shared.Domain.Contracts.IMapper;
import com.StoreManagement.Shared.Infrastructure.Event.EventOptions;

import jakarta.transaction.Transactional;

@Service
public class ProductService implements IProductService {
    @Autowired
    public IProductRepository productRepository;

    @Autowired
    public IFileRepository fileRepository;

    @Autowired
    public IEventPublisher eventPublisher;

    @Autowired
    public IFileService fileService;

    @Autowired
    public IMapper<Product, ProductEntity> productMapper;

    @Value("${spring.application.base-url}")
    private String baseUrl;

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(product -> new ProductResponse(product, baseUrl))
                .toList();
    }

    public ProductResponse getProduct(UUID ProductId) {
        return productRepository.findById(ProductId)
                .map(product -> new ProductResponse(product, baseUrl))
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Transactional
    public ProductResponse createProduct(CreateProductCommand command) throws IOException {
        Product product = new Product(
            null,
            command.getName(),
            command.getDescription(),
            command.getCode(),
            command.getPrice(),
            command.getStock(),
            command.getBrandId(),
            new ProductStatus(),
            command.getCategoryIds(),
            null
        );
        product.setStatus(command.getStatus());

        Product savedProduct = productRepository.save(product);

        if(command.getImages() != null && !command.getImages().isEmpty()) {
            for(MultipartFile image : command.getImages()) {
                String imageUrl = fileService.uploadFile(image, "catalog/products/");
                File file = new File(null, imageUrl, "Product", savedProduct.getId());
                fileRepository.save(file);
            }
        }

        publishDomainEvents(savedProduct, "Product.created");

        return new ProductResponse(savedProduct, baseUrl);
    }

    @Transactional
    public ProductResponse updateProduct(UUID productId, UpdateProductCommand command) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setName(command.getName());
        product.setDescription(command.getDescription());
        product.setCode(command.getCode());
        product.setStatus(command.getStatus());
        product.setBrandId(command.getBrandId());
        product.setCategoryIds(command.getCategoryIds());

        Product savedProduct = productRepository.save(product);

        publishDomainEvents(savedProduct, "Product.updated");

        return new ProductResponse(savedProduct, baseUrl);
    }

    @Transactional
    public void deleteProduct(UUID productId) {
        productRepository.delete(productId);
    }

    @Async
    private void publishDomainEvents(Product product, String queue) {
        product.getDomainEvents()
                .forEach(event -> eventPublisher.publish(event, new EventOptions(queue, false)));

        product.clearDomainEvents();
    }
}
