package com.Marketplace_Management.Shared.Utils.Helpers;

import java.security.SecureRandom;

public class Helper {

    private static final SecureRandom RANDOM = new SecureRandom();
    
    public static String randomString(int length) {
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        
        if (length < 0) {
            throw new IllegalArgumentException("Length must be >= 0");
        }

        StringBuilder result = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            result.append(CHARACTERS.charAt(
                    RANDOM.nextInt(CHARACTERS.length())
            ));
        }

        return result.toString();
    }
}
