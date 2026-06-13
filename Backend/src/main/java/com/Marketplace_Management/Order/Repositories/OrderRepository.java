package com.Marketplace_Management.Order.Repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.Marketplace_Management.Order.Contracts.IOrderRepository;
import com.Marketplace_Management.Order.DTOs.Commands.ListOrderCommand;
import com.Marketplace_Management.Order.DTOs.Responses.HistoryResponse;
import com.Marketplace_Management.Order.Entities.OrderEntity;
import com.Marketplace_Management.Order.Models.Order;
import com.Marketplace_Management.Shared.Contracts.IMapper;
import com.Marketplace_Management.Shared.DTOs.Responses.PaginatedResponse;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository
public class OrderRepository implements IOrderRepository {

    private final OrderJpaRepository jpaRepository;
    private final IMapper<Order, OrderEntity> mapper;
    private final EntityManager entityManager;

    public OrderRepository(OrderJpaRepository jpaRepository, IMapper<Order, OrderEntity> mapper, EntityManager entityManager) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
        this.entityManager = entityManager;
    }

    @Override
    public PaginatedResponse<HistoryResponse> findAll(ListOrderCommand command) {
        return findByUserIdWithFilters(null, command);
    }

    @Override
    public PaginatedResponse<HistoryResponse> findByUserIdWithFilters(UUID userId, ListOrderCommand command) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<HistoryResponse> query = cb.createQuery(HistoryResponse.class);
        Root<OrderEntity> root = query.from(OrderEntity.class);

        query.select(cb.construct(
                HistoryResponse.class,
                root.get("id").as(UUID.class),
                root.get("userId").as(UUID.class),
                root.get("status"),
                root.get("total").as(Double.class),
                root.get("name"),
                root.get("phone"),
                root.get("address"),
                root.get("note"),
                root.get("createdAt"),
                root.get("updatedAt")));

        List<Predicate> predicates = new ArrayList<>();
        if (userId != null) {
            predicates.add(cb.equal(root.get("userId"), userId));
        }

        if (command.getSearch() != null && !command.getSearch().isEmpty()) {
            String searchTerm = "%" + command.getSearch().toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("name")), searchTerm),
                    cb.like(cb.lower(root.get("phone")), searchTerm),
                    cb.like(cb.lower(root.get("note")), searchTerm)));
        }

        if (command.getStatus() != null && !command.getStatus().isEmpty()) {
            predicates.add(cb.equal(root.get("status"), command.getStatus()));
        }

        if (command.getTotalMin() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("total"), command.getTotalMin()));
        }

        if (command.getTotalMax() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("total"), command.getTotalMax()));
        }

        if (command.getDateFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), command.getDateFrom()));
        }

        if (command.getDateTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), command.getDateTo()));
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        if (command.getSortBy() != null && command.getSortOrder() != null) {
            if (command.getSortOrder().equalsIgnoreCase("asc")) {
                query.orderBy(cb.asc(root.get(command.getSortBy())));
            } else {
                query.orderBy(cb.desc(root.get(command.getSortBy())));
            }
        }

        int page = command.getPage();
        int size = command.getSize();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<OrderEntity> countRoot = countQuery.from(OrderEntity.class);
        countQuery.select(cb.count(countRoot));
        countQuery.where(cb.and(predicates.toArray(new Predicate[0])));
        Long totalElements = entityManager.createQuery(countQuery).getSingleResult();

        List<HistoryResponse> data = entityManager.createQuery(query)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();

        return new PaginatedResponse<>(data, page, size, totalElements);
    }

    @Override
    public Order create(Order order) {
        OrderEntity entity = mapper.toEntity(order);
        OrderEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Order updateStatus(UUID id, String status) {
        OrderEntity entity = jpaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
        entity.setStatus(status);
        OrderEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }
}
