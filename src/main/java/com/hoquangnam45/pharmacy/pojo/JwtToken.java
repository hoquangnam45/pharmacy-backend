package com.hoquangnam45.pharmacy.pojo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class JwtToken {
    private final String accessToken;
    private final String refreshToken;
    private final OffsetDateTime accessTokenExpiredAt;
    private final OffsetDateTime refreshTokenExpiredAt;
}
