package com.Marketplace_Management.Shared.Utils.Debugger;

import com.Marketplace_Management.Shared.Errors.Exceptions.DebugException;

public class Debugger {

    public static <T> T debug(T object) {
        throw new DebugException(null, object);
    }

    public static <T> T debug(String message, T object) {
        throw new DebugException(message, object);
    }
}