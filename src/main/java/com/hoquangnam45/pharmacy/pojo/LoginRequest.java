package com.hoquangnam45.pharmacy.pojo;

import com.hoquangnam45.pharmacy.constant.LoginType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@Getter
@RequiredArgsConstructor
public class LoginRequest {
    private final String user;
    private final String password;
    private final LoginType type;
}
