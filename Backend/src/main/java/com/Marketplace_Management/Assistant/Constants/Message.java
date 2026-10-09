package com.Marketplace_Management.Assistant.Constants;

public final class Message {
    public static final String ASSISTANT_UNAVAILABLE = "The assistant is temporarily unavailable. Please try again later.";
    public static final String ASSISTANT_BUSY = "The assistant is receiving too many requests. Please try again in a moment.";
    public static final String ASSISTANT_BLOCKED = "The assistant could not answer this message. Please rephrase and try again.";

    private Message() {
        throw new AssertionError("Utility class");
    }
}
