package com.StoreManagement.Order.Domain.Constants;

public final class Message {
    public static final String CART_ITEM_REMOVED = "Item removed from cart successfully";
    
    private Message() {
        throw new AssertionError("Utility class should not be instantiated");
    }
}
