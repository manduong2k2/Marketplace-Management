package com.StoreManagement.Catalog.Application.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.StoreManagement.Catalog.Application.DTO.Commands.Product.CreateProductCommand;
import com.StoreManagement.Catalog.Application.DTO.Commands.Product.GetListProductCommand;
import com.StoreManagement.Catalog.Application.DTO.Commands.Product.UpdateProductCommand;
import com.StoreManagement.Catalog.Application.DTO.Response.PaginatedResponse;
import com.StoreManagement.Catalog.Application.DTO.Response.ProductResponse;
import com.StoreManagement.Catalog.Domain.Constants.ProductStatusEnum;
import com.StoreManagement.Catalog.Domain.Contract.IProductRepository;
import com.StoreManagement.Catalog.Domain.Contract.IProductService;
import com.StoreManagement.Catalog.Domain.Events.ProductArchivedEvent;
import com.StoreManagement.Catalog.Domain.Events.ProductDeletedEvent;
import com.StoreManagement.Catalog.Domain.Events.ProductOutStockEvent;
import com.StoreManagement.Catalog.Domain.Models.Product;
import com.StoreManagement.Catalog.Domain.Models.ProductStatus;
import com.StoreManagement.Catalog.Infrastructure.Persistence.Entity.ProductEntity;
import com.StoreManagement.Shared.Application.Contracts.IFileService;
import com.StoreManagement.Shared.Domain.File;
import com.StoreManagement.Shared.Domain.Contracts.IEventPublisher;
import com.StoreManagement.Shared.Domain.Contracts.IFileRepository;
import com.StoreManagement.Shared.Domain.Contracts.IMapper;
import com.StoreManagement.Shared.Infrastructure.Configuration.RabbitMQQueueConfig;
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

    public PaginatedResponse<ProductResponse> getAllProducts(GetListProductCommand command) {
        PaginatedResponse<Product> products = productRepository.findAll(command);
        List<ProductResponse> productResponses = products.getData().stream()
                .map(product -> new ProductResponse(product, baseUrl))
                .toList();
        return new PaginatedResponse<>(
                productResponses,
                products.getCurrentPage(),
                products.getPageSize(),
                products.getTotalElements()
        );
    }

    public ProductResponse getProduct(UUID ProductId) {
        return productRepository.findById(ProductId)
                .map(product -> new ProductResponse(product, baseUrl))
                .orElseThrow(() -> new RuntimeException("Product " + ProductId + " not found"));
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

        return new ProductResponse(savedProduct, baseUrl);
    }

    @Transactional
    public ProductResponse updateProduct(UUID productId, UpdateProductCommand command) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setName(command.getName());
        product.setDescription(command.getDescription());
        product.setCode(command.getCode());
        product.setPrice(command.getPrice());
        product.setStock(command.getStock());
        product.setStatus(command.getStatus());
        product.setBrandId(command.getBrandId());
        product.setCategoryIds(command.getCategoryIds());

        if(product.isOutOfStock()) {
            eventPublisher.publish(
                new ProductOutStockEvent(product.getId(), "OUT_OF_STOCK"), 
                new EventOptions("product.out_of_stock.queue", false)
            );
        }

        if(product.isArchived()) {
            eventPublisher.publish(
                new ProductArchivedEvent(product.getId()), 
                new EventOptions("product.archived.queue", false)
            );
        }

        Product savedProduct = productRepository.save(product);

        //Delete non-existent images
        for(String imageUrl : product.getFiles().stream().map(file -> file.getUrl()).collect(Collectors.toList())) {
            if(!command.getImageUrls().stream().map(url -> url.replace(baseUrl + "/", "")).collect(Collectors.toList()).contains(imageUrl)) {
                fileService.deleteFile(imageUrl);
                fileRepository.delete(imageUrl);
            }
        }

        //Upload new images
        if(command.getImages() != null && !command.getImages().isEmpty()) {
            for(MultipartFile image : command.getImages()) {
                String imageUrl = fileService.uploadFile(image, "catalog/products/");
                File file = new File(null, imageUrl, "Product", savedProduct.getId());
                fileRepository.save(file);
            }
        }

        return new ProductResponse(savedProduct, baseUrl);
    }

    @Transactional
    public void deleteProduct(UUID productId) {
        productRepository.delete(productId);
        ProductDeletedEvent event = new ProductDeletedEvent(productId);
        eventPublisher.publish(event, new EventOptions(RabbitMQQueueConfig.PRODUCT_DELETED_QUEUE, false));
    }
    
    public List<String> getAllStatus() {
        return Arrays.stream(ProductStatusEnum.values()).map(ProductStatusEnum::name).collect(Collectors.toList());
    }
}
