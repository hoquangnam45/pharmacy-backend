package com.hoquangnam45.pharmacy.component;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hoquangnam45.pharmacy.constant.AuthenticationType;
import com.hoquangnam45.pharmacy.constant.JwtClaim;
import com.hoquangnam45.pharmacy.entity.RefreshToken;
import com.hoquangnam45.pharmacy.pojo.CustomAuthentication;
import com.hoquangnam45.pharmacy.pojo.RoleGrantAuthority;
import com.hoquangnam45.pharmacy.service.JwtService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final JwtService jwtService;

    public JwtAuthenticationProvider(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        CustomAuthentication customAuthentication = (CustomAuthentication) authentication;
        if (customAuthentication.isAuthenticated() || customAuthentication.getDetails() != AuthenticationType.ACCESS_TOKEN) {
            return null;
        } else {
            String accessToken = (String) authentication.getCredentials();
            Pair<DecodedJWT, JWTVerificationException> result = jwtService.validateAccessToken(accessToken);
            if (result.getRight() != null) {
                return CustomAuthentication.rejectAuthentication(customAuthentication, result.getRight().getMessage());
            } else {
                return CustomAuthentication.authenticated(result.getLeft(), accessToken);
            }
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication == CustomAuthentication.class;
    }
}
