package com.hoquangnam45.pharmacy.component;

import com.hoquangnam45.pharmacy.constant.AuthenticationType;
import com.hoquangnam45.pharmacy.entity.User;
import com.hoquangnam45.pharmacy.pojo.CustomAuthentication;
import com.hoquangnam45.pharmacy.pojo.CustomAuthenticationPrincipal;
import com.hoquangnam45.pharmacy.service.UserService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordAuthenticationProvider implements AuthenticationProvider {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public PasswordAuthenticationProvider(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        CustomAuthentication customAuthentication = (CustomAuthentication) authentication;
        if (customAuthentication.isAuthenticated() || customAuthentication.getDetails() != AuthenticationType.PASSWORD) {
            return null;
        } else {
            CustomAuthenticationPrincipal principal = (CustomAuthenticationPrincipal) customAuthentication.getPrincipal();
            User user = userService.getUser(principal);
            if (user == null) {
                return CustomAuthentication.rejectAuthentication(customAuthentication, "User not found");
            } else if (!passwordEncoder.matches((String) customAuthentication.getCredentials(), user.getPassword())) {
                return CustomAuthentication.rejectAuthentication(customAuthentication, "Password not matches");
            } else {
                return CustomAuthentication.authenticated(user);
            }
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication == CustomAuthentication.class;
    }
}
