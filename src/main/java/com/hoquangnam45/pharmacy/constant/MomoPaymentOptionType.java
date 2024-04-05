package com.hoquangnam45.pharmacy.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public enum MomoPaymentOptionType {
    MOMO("momo"),
    PAY_LATER("pay_later");

    private final String value;

    public static MomoPaymentOptionType parseValue(String value) {
        return Stream.of(values())
                .filter(v -> v.getValue().equals(value))
                .findFirst()
                .orElse(null);
    }
}
