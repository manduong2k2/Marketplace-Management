package com.Marketplace_Management.Order.Domain.Constants;

public enum CartStatusEnum {
    NEW,
    CHECKED_OUT,
    ABANDONED,
    EXPIRED;

    public static boolean isValid(String value) {
        try {
            CartStatusEnum.valueOf(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
