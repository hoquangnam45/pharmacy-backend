package com.hoquangnam45.pharmacy.pojo;

import com.hoquangnam45.pharmacy.constant.LoginType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.Objects;

@Getter
@RequiredArgsConstructor
public class CustomAuthenticationPrincipal implements Principal {
    private final String principal;
    private final LoginType type;

    @Override
    public String getName() {
        return principal;
    }

    @Override
    public boolean implies(Subject subject) {
        return Principal.super.implies(subject);
    }
}
