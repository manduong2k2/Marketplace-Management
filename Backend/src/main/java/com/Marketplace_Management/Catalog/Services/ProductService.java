package com.Marketplace_Management.Catalog.Services;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.Marketplace_Management.Catalog.Constants.ProductStatusEnum;
import com.Marketplace_Management.Catalog.Contracts.IProductRepository;
import com.Marketplace_Management.Catalog.Contracts.IProductService;
import com.Marketplace_Management.Catalog.DTOs.Commands.Product.CreateProductCommand;
import com.Marketplace_Management.Catalog.DTOs.Commands.Product.GetListProductCommand;
import com.Marketplace_Management.Catalog.DTOs.Commands.Product.UpdateProductCommand;
import com.Marketplace_Management.Catalog.DTOs.Response.ProductResponse;
import com.Marketplace_Management.Catalog.DTOs.Response.ProductVariantResponse;
import com.Marketplace_Management.Catalog.DTOs.Response.Short.ProductShortResponse;
import com.Marketplace_Management.Catalog.Entities.ProductEntity;
import com.Marketplace_Management.Catalog.Events.ProductArchivedEvent;
import com.Marketplace_Management.Catalog.Events.ProductDeletedEvent;
import com.Marketplace_Management.Catalog.Models.Product;
import com.Marketplace_Management.Catalog.Models.ProductOption;
import com.Marketplace_Management.Catalog.Models.ProductVariant;
import com.Marketplace_Management.Shared.Configuration.RabbitMqQueues.ProductQueueConfig;
import com.Marketplace_Management.Shared.Contracts.IEventPublisher;
import com.Marketplace_Management.Shared.Contracts.IFileRepository;
import com.Marketplace_Management.Shared.Contracts.IFileService;
import com.Marketplace_Management.Shared.Contracts.EntityDomainMapper;
import com.Marketplace_Management.Shared.DTOs.Responses.PaginatedResponse;
import com.Marketplace_Management.Shared.Errors.Exceptions.ResourceNotFoundException;
import com.Marketplace_Management.Shared.Events.EventOptions;
import com.Marketplace_Management.Shared.Models.File;
import com.Marketplace_Management.Shared.Security.SecurityUtils;
import com.Marketplace_Management.Vendor.Contracts.IVendorService;
import com.Marketplace_Management.Vendor.DTOs.Response.VendorResponse;

import jakarta.transaction.Transactional;

@Service
public class ProductService implements IProductService {
    private final IProductRepository productRepository;
    private final IEventPublisher eventPublisher;
    private final IFileService fileService;
    private final IVendorService vendorService;

    @Value("${spring.application.base-url}")
    private String baseUrl;

    public ProductService(IProductRepository productRepository, IFileRepository fileRepository,
                        IEventPublisher eventPublisher, IFileService fileService,
                        EntityDomainMapper<Product, ProductEntity> productMapper, IVendorService vendorService) {
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
        this.fileService = fileService;
        this.vendorService = vendorService;
    }

    //@Cacheable(value = "products", key = "#command.page + '_' + #command.size + '_' + #command.search + '_' + #command.categoryIds + '_' + #command.brandId")
    public PaginatedResponse<ProductShortResponse> getAllProducts(GetListProductCommand command) {
        
        PaginatedResponse<ProductShortResponse> products = productRepository.findAll(command);

        return new PaginatedResponse<>(
                products.getData(),
                products.getCurrentPage(),
                products.getPageSize(),
                products.getTotalElements());
    }

    @Cacheable(value = "product", key = "#ProductId")
    public ProductResponse getProduct(UUID ProductId) {
        Product product = productRepository.findById(ProductId);

        if (product == null) {
            throw new ResourceNotFoundException("Product not found");
        }

        return new ProductResponse(product).withUrl(baseUrl);
    }

    @Transactional
    public ProductResponse createProduct(CreateProductCommand command) throws IOException {
        List<ProductOption> productOptions = command.getOptions().stream().map(option -> 
            ProductOption.builder()
                    .id(option.getTempId())
                    .name(option.getName())
                    .value(option.getValue())
                    .build()
        ).toList();

        if(command.getVendorId() == null) {
            VendorResponse vendor = vendorService.getByUser(SecurityUtils.currentUserId());
            if(vendor == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vendor not found");
            }
            command.setVendorId(vendor.getId());
        }

        Product product = Product.builder()
                .name(command.getName())
                .description(command.getDescription())
                .brandId(command.getBrandId())
                .status(command.getStatus())
                .categoryIds(command.getCategoryIds())
                .options(new java.util.HashSet<>(productOptions))
                .status(command.getStatus() != null ? command.getStatus() : ProductStatusEnum.PUBLISHED.name())
                .vendorId(command.getVendorId())
                .variants(command.getVariants().stream().map(variant -> 
                    ProductVariant.builder()
                            .name(variant.getName())
                            .sku(variant.getSku())
                            .price(variant.getPrice())
                            .stock(variant.getStock())
                            .files(variant.getImages() != null ? variant.getImages().stream().map(img -> {
                                try {
                                    String imageUrl = fileService.uploadFile(img, "catalog/product_variants");
                                    File file = new File(null, imageUrl, "ProductVariant");
                                    return file;
                                } catch (IOException e) {
                                    throw new RuntimeException("Failed to upload file", e);
                                }
                            }).toList() : null)
                            .options(productOptions.stream().filter(po -> variant.getOptionIds().contains(po.getId())).collect(java.util.stream.Collectors.toSet()))
                            .build()
                ).collect(java.util.stream.Collectors.toSet()))
                .build();

        productOptions.forEach(option -> {
            option.setProduct(product);
            option.setId(null);
        });

        Product savedProduct = productRepository.save(product);

        return new ProductResponse(savedProduct).withUrl(baseUrl);
    }

    @Transactional
    public ProductResponse updateProduct(UUID productId, UpdateProductCommand command) throws IOException {
        Product product = productRepository.findById(productId);

        if(command.getName() != null) product.setName(command.getName());
        if(command.getDescription() != null) product.setDescription(command.getDescription());
        if(command.getStatus() != null) product.setStatus(command.getStatus());
        if(command.getBrandId() != null) product.setBrandId(command.getBrandId());
        if(command.getCategoryIds() != null) product.setCategoryIds(command.getCategoryIds());

        Product savedProduct = productRepository.update(product);

        if (product.isArchived()) {
            eventPublisher.publish(
                    new ProductArchivedEvent(product.getId()),
                    new EventOptions("product.archived.queue", false));
        }

        return new ProductResponse(savedProduct).withUrl(baseUrl);
    }

    @Transactional
    public void deleteProduct(UUID productId) {
        productRepository.delete(productId);
        ProductDeletedEvent event = new ProductDeletedEvent(productId);
        eventPublisher.publish(event, new EventOptions(ProductQueueConfig.PRODUCT_DELETED_QUEUE, false));
    }

    public List<String> getAllStatus() {
        return Arrays.stream(ProductStatusEnum.values()).map(ProductStatusEnum::name).collect(Collectors.toList());
    }

    public ProductVariantResponse getProductVariant(UUID productVariantId) {
        ProductVariant productVariant = productRepository.findVariantById(productVariantId);
        return productVariant != null ? new ProductVariantResponse(productVariant).withUrl(baseUrl) : null;
    }

    public ProductVariantResponse getProductVariant(List<UUID> optionIds) {
        
        return null;
    }
}
