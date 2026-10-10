package com.Marketplace_Management.Catalog.DTOs.Requests.Category;

import com.Marketplace_Management.Shared.DTOs.Requests.PageRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GetListCategoryRequest extends PageRequest {
    public GetListCategoryRequest() {
        super("name", "asc", 10);
    }
}
