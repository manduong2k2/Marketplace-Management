package com.Marketplace_Management.Catalog.DTOs.Requests.Product;

import java.util.List;
import java.util.UUID;

import com.Marketplace_Management.Shared.Annotation.Rules.Uuid;
import com.Marketplace_Management.Shared.Annotation.Rules.Distinct;
import com.Marketplace_Management.Shared.Annotation.Rules.Exist;
import com.Marketplace_Management.Shared.DTOs.Requests.PageRequest;

import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GetListProductRequest extends PageRequest {
    @Nullable
    @Distinct(message = "Each category ID must be unique")
    private List<
    @Exist(table = "categories", column = "id", message = "Category not found", type = UUID.class)
    @Uuid
    String> categoryIds;

    @Nullable
    @Exist(table = "brands", column = "id", message = "Brand not found", type = UUID.class)
    @Uuid
    private String brandId;

    @Nullable
    @Exist(table = "vendors", column = "id", message = "Vendor not found", type = UUID.class)
    @Uuid
    private String vendorId;

    private String status;

    public GetListProductRequest() {
        super("name", "asc", 10);
    }
}
