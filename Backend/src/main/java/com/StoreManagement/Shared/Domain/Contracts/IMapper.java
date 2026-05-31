package com.StoreManagement.Shared.Domain.Contracts;

public interface IMapper<D,E> {
    public D toDomain(E entity);
    public E toEntity(D domain);
}