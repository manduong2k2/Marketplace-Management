package com.Marketplace_Management.Shared.Errors.Exceptions;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"message", "payload"})
public class DebugException extends RuntimeException {

    private final Object payload;

    public DebugException(String message, Object payload) {
        super(message);
        this.payload = payload;
    }

    public Object getPayload() {
        return payload;
    }
}
