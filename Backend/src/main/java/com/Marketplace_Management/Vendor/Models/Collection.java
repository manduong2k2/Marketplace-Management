package com.Marketplace_Management.Vendor.Models;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Collection {
    private UUID id;
    private UUID vendorId;
    private String name;
    private Integer displayOrder;

}
