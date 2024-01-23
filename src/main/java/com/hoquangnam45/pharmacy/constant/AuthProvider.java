package com.hoquangnam45.pharmacy.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
@Getter
public enum AuthProvider {
    GOOGLE("google"),
    FACEBOOK("facebook");

    private final String providerName;

    public static AuthProvider parseProvider(String provider) {
        return Stream.of(values())
                .filter(v -> v.providerName.equals(provider))
                .findFirst()
                .orElse(null);
    }
}
