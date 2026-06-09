package com.StoreManagement.Cart.Application.DTO.Requests;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateItemRequest {

    @Min(value = 0, message = "quantity must be 0 or greater (0 removes the item)")
    private int quantity;
}
