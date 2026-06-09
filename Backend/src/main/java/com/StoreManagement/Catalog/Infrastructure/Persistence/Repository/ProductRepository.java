package com.StoreManagement.Catalog.Infrastructure.Persistence.Repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.StoreManagement.Catalog.Application.DTO.Commands.Product.GetListProductCommand;
import com.StoreManagement.Catalog.Application.DTO.Response.PaginatedResponse;
import com.StoreManagement.Catalog.Domain.Contract.IProductRepository;
import com.StoreManagement.Catalog.Domain.Models.Product;
import com.StoreManagement.Catalog.Infrastructure.Persistence.Entity.CategoryEntity;
import com.StoreManagement.Catalog.Infrastructure.Persistence.Entity.ProductEntity;
import com.StoreManagement.Shared.Domain.Contracts.IMapper;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.EntityManager;

@Repository
public class ProductRepository implements IProductRepository {
    private final ProductJpaRepository jpaRepository;
    private final IMapper<Product, ProductEntity> productMapper;

    @Autowired
    private EntityManager entityManager;

    public ProductRepository(ProductJpaRepository jpaRepository, IMapper<Product, ProductEntity> productMapper,
            EntityManager entityManager) {
        this.jpaRepository = jpaRepository;
        this.productMapper = productMapper;
        this.entityManager = entityManager;
    }

    public PaginatedResponse<Product> findAll(GetListProductCommand command) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // ========== MAIN QUERY ==========
        CriteriaQuery<ProductEntity> query = cb.createQuery(ProductEntity.class);
        Root<ProductEntity> root = query.from(ProductEntity.class);

        // ========== COUNT QUERY ==========
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ProductEntity> countRoot = countQuery.from(ProductEntity.class);

        // ================= SHARED LOGIC =================
        List<Predicate> predicates = new ArrayList<>();
        List<Predicate> countPredicates = new ArrayList<>();

        // category
        if (command.getCategoryIds() != null && !command.getCategoryIds().isEmpty()) {

            Join<ProductEntity, CategoryEntity> categoryJoin = root.join("categories");
            Join<ProductEntity, CategoryEntity> countCategoryJoin = countRoot.join("categories");

            predicates.add(categoryJoin.get("id").in(command.getCategoryIds()));
            countPredicates.add(countCategoryJoin.get("id").in(command.getCategoryIds()));

            query.distinct(true);
            countQuery.distinct(true);
        }

        // search
        if (command.getSearch() != null && !command.getSearch().trim().isEmpty()) {

            String search = "%" + command.getSearch().toLowerCase() + "%";

            Predicate searchPredicate = cb.or(
                    cb.like(cb.lower(root.get("name")), search),
                    cb.like(cb.lower(root.get("description")), search));

            predicates.add(searchPredicate);
            countPredicates.add(searchPredicate);
        }

        // brand
        if (command.getBrandId() != null) {

            Predicate brandPredicate = cb.equal(root.get("brand").get("id"), command.getBrandId());

            predicates.add(brandPredicate);
            countPredicates.add(brandPredicate);
        }

        // ================= APPLY WHERE =================
        query.where(predicates.toArray(new Predicate[0]));
        countQuery.where(countPredicates.toArray(new Predicate[0]));

        countQuery.select(cb.countDistinct(countRoot));

        // ================= SORT =================
        String sortBy = (command.getSortBy() == null || command.getSortBy().isBlank())
                ? "updatedAt"
                : command.getSortBy();

        boolean desc = "desc".equalsIgnoreCase(command.getSortOrder());

        query.orderBy(desc ? cb.desc(root.get(sortBy)) : cb.asc(root.get(sortBy)));

        // ================= EXECUTE =================
        Long total = entityManager.createQuery(countQuery).getSingleResult();

        List<ProductEntity> entities = entityManager.createQuery(query)
                .setFirstResult(command.getPage() * command.getSize())
                .setMaxResults(command.getSize())
                .getResultList();

        List<Product> products = entities.stream()
                .map(productMapper::toDomain)
                .toList();

        return new PaginatedResponse<>(
                products,
                command.getPage(),
                command.getSize(),
                total);
    }

    @Override
    public Product save(Product Product) {
        ProductEntity productEntity = productMapper.toEntity(Product);
        ProductEntity saved = jpaRepository.save(productEntity);
        if (saved != null && saved.getVariants() != null && !saved.getVariants().isEmpty()) {
            saved.getVariants().forEach(
                    variant -> {
                        if (variant != null && variant.getFiles() != null && !variant.getFiles().isEmpty()) {
                            variant.getFiles().forEach(file -> file.setEntityId(variant.getId()));
                        }
                    });
        }
        return productMapper.toDomain(saved);
    }

    @Override
    public Product findById(UUID id) {
        return jpaRepository.findById(id).map(productMapper::toDomain).orElse(null);
    }

    @Override
    public List<Product> findByName(String name) {
        return jpaRepository.findByName(name).stream()
                .map(productMapper::toDomain)
                .toList();
    }

    @Override
    public Product update(Product Product) {
        ProductEntity productEntity = productMapper.toEntity(Product);
        ProductEntity updated = jpaRepository.save(productEntity);
        return productMapper.toDomain(updated);
    }

    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }
}