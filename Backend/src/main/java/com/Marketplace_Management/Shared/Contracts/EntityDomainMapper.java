package com.Marketplace_Management.Shared.Contracts;

public interface EntityDomainMapper<D,E> {
    public D toDomain(E entity);
    public E toEntity(D domain);
}