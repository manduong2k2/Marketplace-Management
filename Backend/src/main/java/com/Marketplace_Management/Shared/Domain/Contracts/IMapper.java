package com.Marketplace_Management.Shared.Domain.Contracts;

public interface IMapper<D,E> {
    public D toDomain(E entity);
    public E toEntity(D domain);
}