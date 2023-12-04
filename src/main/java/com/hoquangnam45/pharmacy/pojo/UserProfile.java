package com.hoquangnam45.pharmacy.pojo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
@Builder
public class UserProfile {
    private final UUID id;
    private final String username;
    private final String email;
    private final PhoneNumber phoneNumber;
    private final boolean emailConfirmed;
    private final boolean phoneNumberConfirmed;
}
