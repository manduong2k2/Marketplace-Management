package com.Marketplace_Management.Delivery.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "provinces")
@Data
public class ProvinceEntity {

    @Id
    @Column(length = 20)
    private String code;

    @Column(nullable = false)
    private String name;

    private String nameEn;

    @Column(nullable = false)
    private String fullName;

    private String fullNameEn;

    private String codeName;

    @OneToMany(mappedBy = "province", fetch = FetchType.LAZY)
    private List<WardEntity> wards;

    // getters/setters
}
