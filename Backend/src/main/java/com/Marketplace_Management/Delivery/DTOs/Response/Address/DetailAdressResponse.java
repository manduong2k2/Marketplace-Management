package com.Marketplace_Management.Delivery.DTOs.Response.Address;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DetailAdressResponse {
    private Long id;
    private UUID userId;
    private String title;
    private String streetName;
    private String houseNumber;
    private String detail;
    private Boolean isDefault;
    private WardResponse ward;

    @Data
    @Builder
    public static class WardResponse {
        private String id;
        private String name;
        private String fullName;
        private ProvinceResponse province;

        @Data
        @Builder
        public static class ProvinceResponse {
            private String id;
            private String name;
            private String fullName;
        }
    }
}
