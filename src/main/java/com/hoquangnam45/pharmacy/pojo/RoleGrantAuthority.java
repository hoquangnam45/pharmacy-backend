package com.hoquangnam45.pharmacy.pojo;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public class RoleGrantAuthority implements GrantedAuthority {
    private final String roleName;
    @Override
    public String getAuthority() {
        return roleName;
    }
}
