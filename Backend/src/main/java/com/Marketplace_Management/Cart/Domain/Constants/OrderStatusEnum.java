package com.Marketplace_Management.Cart.Domain.Constants;

public enum OrderStatusEnum {
    PENDING("PENDING"),
    PAID("PAID"),
    PROCESSING("PROCESSING"),
    SHIPPING("SHIPPING"),
    COMPLETED("COMPLETED"),
    CANCELLED("CANCELLED"),
    EXPIRED("EXPIRED");

    private final String value;

    OrderStatusEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static boolean isValid(String value) {
        try {
            OrderStatusEnum.valueOf(value);
            return true;

        } catch (IllegalArgumentException e) {

            return false;
        }
    }
}
