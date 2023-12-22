package com.hoquangnam45.pharmacy.pojo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RefreshRequest {
    private final String refreshToken;
}
