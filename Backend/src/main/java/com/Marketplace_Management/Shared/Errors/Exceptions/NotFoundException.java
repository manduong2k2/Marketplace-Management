package com.Marketplace_Management.Shared.Errors.Exceptions;

public class NotFoundException extends RuntimeException {
    
    public NotFoundException(String message) {
        super(message);
    }
}
