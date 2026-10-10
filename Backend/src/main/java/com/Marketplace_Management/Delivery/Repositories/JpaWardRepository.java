package com.Marketplace_Management.Delivery.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Marketplace_Management.Delivery.Entities.WardEntity;

public interface JpaWardRepository extends JpaRepository<WardEntity, String> {
    List<WardEntity> findByProvinceId(String provinceId);
}
