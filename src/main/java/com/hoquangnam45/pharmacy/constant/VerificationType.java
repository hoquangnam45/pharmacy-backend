package com.hoquangnam45.pharmacy.constant;

import java.util.stream.Stream;

// NOTE: This should not exist if the verification code is not saved to the same db as otp
public enum VerificationType {
    EMAIL,
    PHONE;

    public static VerificationType parseValue(String value) {
        return Stream.of(values())
                .filter(v -> v.toString().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElse(null);
    }
}
