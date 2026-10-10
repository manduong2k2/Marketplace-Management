package com.Marketplace_Management.Delivery.Models;

import java.util.UUID;
import com.Marketplace_Management.Shared.Models.AggregateRoot;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
public class Address extends AggregateRoot<Long> {
    private UUID    userId;
    private String  detail;
    private Ward    ward;
    private String  wardId;
    private Boolean isDefault;

    public boolean isDefaultAddress() {
        return Boolean.TRUE.equals(isDefault);
    }

    /** "detail, Phường X, Thành phố Y" — what the user sees and what an order stores. */
    public String fullAddress() {
        StringBuilder sb = new StringBuilder(detail == null ? "" : detail);
        if (ward != null) {
            sb.append(", ").append(ward.getFullName());
            if (ward.getProvince() != null) {
                sb.append(", ").append(ward.getProvince().getFullName());
            }
        }
        return sb.toString();
    }
}
