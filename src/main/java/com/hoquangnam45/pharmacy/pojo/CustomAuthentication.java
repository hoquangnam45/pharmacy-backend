package com.hoquangnam45.pharmacy.pojo;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.hoquangnam45.pharmacy.constant.AuthenticationType;
import com.hoquangnam45.pharmacy.constant.JwtClaim;
import com.hoquangnam45.pharmacy.entity.RefreshToken;
import com.hoquangnam45.pharmacy.entity.User;
import com.hoquangnam45.pharmacy.entity.UserPermission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class CustomAuthentication implements Authentication {
    private final Object principal;
    private final Object credentials;
    private boolean authenticated;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Object details;

    public static CustomAuthentication unauthenticated(LoginRequest loginRequest) {
        return new CustomAuthentication(new CustomAuthenticationPrincipal(loginRequest.getUser(), loginRequest.getType()),
            loginRequest.getPassword(),
            false,
            Collections.emptySet(),
            AuthenticationType.PASSWORD);
    }

    public static CustomAuthentication unauthenticatedAccessToken(String accessToken) {
        return new CustomAuthentication(accessToken,
                accessToken,
                false,
                Collections.emptySet(),
                AuthenticationType.ACCESS_TOKEN);
    }

    public static CustomAuthentication authenticated(User user) {
        return new CustomAuthentication((PrincipalSupplier) () -> user.getId().toString(),
            user.getPassword(),
            true,
            Optional.ofNullable(user.getPermissions())
                    .stream()
                    .flatMap(Collection::stream)
                    .map(UserPermission::getRole)
                    .map(role -> (GrantedAuthority) role::toString)
                    .collect(Collectors.toSet()),
            AuthenticationType.PASSWORD);
    }

    public static CustomAuthentication authenticated(RefreshToken refreshToken) {
        User user = refreshToken.getUser();
        return new CustomAuthentication((PrincipalSupplier) () -> user.getId().toString(),
            refreshToken.getRefreshToken(),
            true,
            user.getPermissions().stream()
                    .map(UserPermission::getRole)
                    .map(role -> (GrantedAuthority) role::toString)
                    .collect(Collectors.toSet()),
            AuthenticationType.REFRESH_TOKEN);
    }

    public static CustomAuthentication authenticated(DecodedJWT decodedJWT, String accessToken) {
        return new CustomAuthentication(
                (PrincipalSupplier) decodedJWT::getSubject,
                accessToken,
                true,
                decodedJWT.getClaim(JwtClaim.ROLE).asList(String.class).stream()
                        .map(RoleGrantAuthority::new)
                        .collect(Collectors.toList()),
                AuthenticationType.ACCESS_TOKEN);
    }

    public static CustomAuthentication rejectAuthentication(CustomAuthentication authentication, String rejectReason) {
        return new CustomAuthentication(
                authentication.getPrincipal(),
                authentication.getCredentials(),
                false,
                authentication.getAuthorities(),
                rejectReason);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getDetails() {
        return details;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        if (principal instanceof PrincipalSupplier) {
            return ((PrincipalSupplier) principal).getPrincipal();
        } else if (principal instanceof Principal) {
            return ((Principal) principal).getName();
        } else if (principal instanceof String) {
            return (String) principal;
        } else {
            return principal.toString();
        }
    }
}
