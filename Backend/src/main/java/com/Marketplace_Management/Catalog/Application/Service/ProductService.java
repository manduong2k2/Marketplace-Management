package com.Marketplace_Management.Catalog.Application.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.Marketplace_Management.Catalog.Application.DTO.Commands.Product.CreateProductCommand;
import com.Marketplace_Management.Catalog.Application.DTO.Commands.Product.GetListProductCommand;
import com.Marketplace_Management.Catalog.Application.DTO.Commands.Product.UpdateProductCommand;
import com.Marketplace_Management.Catalog.Application.DTO.Response.ProductResponse;
import com.Marketplace_Management.Catalog.Application.DTO.Response.ProductVariantResponse;
import com.Marketplace_Management.Catalog.Domain.Constants.ProductStatusEnum;
import com.Marketplace_Management.Catalog.Domain.Contract.IProductRepository;
import com.Marketplace_Management.Catalog.Domain.Contract.IProductService;
import com.Marketplace_Management.Catalog.Domain.Events.ProductArchivedEvent;
import com.Marketplace_Management.Catalog.Domain.Events.ProductDeletedEvent;
import com.Marketplace_Management.Catalog.Domain.Models.Product;
import com.Marketplace_Management.Catalog.Domain.Models.ProductOption;
import com.Marketplace_Management.Catalog.Domain.Models.ProductVariant;
import com.Marketplace_Management.Catalog.Infrastructure.Persistence.Entity.ProductEntity;
import com.Marketplace_Management.Shared.Application.Contracts.IFileService;
import com.Marketplace_Management.Shared.Application.DTO.Responses.PaginatedResponse;
import com.Marketplace_Management.Shared.Domain.File;
import com.Marketplace_Management.Shared.Domain.Contracts.IEventPublisher;
import com.Marketplace_Management.Shared.Domain.Contracts.IFileRepository;
import com.Marketplace_Management.Shared.Domain.Contracts.IMapper;
import com.Marketplace_Management.Shared.Infrastructure.Configuration.RabbitMqQueues.ProductQueueConfig;
import com.Marketplace_Management.Shared.Infrastructure.Event.EventOptions;

import jakarta.transaction.Transactional;

@Service
public class ProductService implements IProductService {
    private final IProductRepository productRepository;
    private final IFileRepository fileRepository;
    private final IEventPublisher eventPublisher;
    private final IFileService fileService;
    private final IMapper<Product, ProductEntity> productMapper;

    @Value("${spring.application.base-url}")
    private String baseUrl;

    public ProductService(IProductRepository productRepository, IFileRepository fileRepository,
                        IEventPublisher eventPublisher, IFileService fileService,
                        IMapper<Product, ProductEntity> productMapper) {
        this.productRepository = productRepository;
        this.fileRepository = fileRepository;
        this.eventPublisher = eventPublisher;
        this.fileService = fileService;
        this.productMapper = productMapper;
    }

    @Cacheable(value = "products", key = "#command.page + '_' + #command.size + '_' + #command.search + '_' + #command.categoryIds + '_' + #command.brandId")
    public PaginatedResponse<ProductResponse> getAllProducts(GetListProductCommand command) {
        PaginatedResponse<Product> products = productRepository.findAll(command);
        List<ProductResponse> productResponses = products.getData().stream()
                .map(product -> new ProductResponse(product, baseUrl))
                .toList();
        return new PaginatedResponse<>(
                productResponses,
                products.getCurrentPage(),
                products.getPageSize(),
                products.getTotalElements());
    }

    @Cacheable(value = "product", key = "#ProductId")
    public ProductResponse getProduct(UUID ProductId) {
        Product product = productRepository.findById(ProductId);
        return new ProductResponse(product, baseUrl);
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

        Product product = Product.builder()
                .name(command.getName())
                .description(command.getDescription())
                .brandId(command.getBrandId())
                .status(command.getStatus())
                .categoryIds(command.getCategoryIds())
                .options(productOptions)
                .variants(command.getVariants().stream().map(variant -> 
                    ProductVariant.builder()
                            .name(variant.getName())
                            .code(variant.getCode())
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
                            .options(productOptions.stream().filter(po -> variant.getOptionIds().contains(po.getId())).toList())
                            .build()
                ).toList())
                .build();

        productOptions.forEach(option -> {
            option.setProduct(product);
            option.setId(null);
        });

        if (command.getStatus() != null) {
            product.setStatus(command.getStatus());
        } else {
            product.setStatus(ProductStatusEnum.PUBLISHED);
        }

        Product savedProduct = productRepository.save(product);

        return new ProductResponse(savedProduct, baseUrl);
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

        return new ProductResponse(savedProduct, baseUrl);
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

    public ProductVariantResponse getProductVariant(UUID productId, UUID productVariantId) {
        ProductVariant productVariant = productRepository.findVariantById(productId, productVariantId);
        return new ProductVariantResponse(productVariant, baseUrl);
    }

    public ProductVariantResponse getProductVariant(List<UUID> optionIds) {
        
        return null;
    }
}
