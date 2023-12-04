package com.hoquangnam45.pharmacy.pojo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@Getter
@RequiredArgsConstructor
public class RegisterRequest {
    private final String username;
    private final String email;
    private final String password;
}
