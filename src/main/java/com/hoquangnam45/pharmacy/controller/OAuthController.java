package com.hoquangnam45.pharmacy.controller;

import com.hoquangnam45.pharmacy.config.OAuthConfig;
import com.hoquangnam45.pharmacy.constant.AuthProvider;
import com.hoquangnam45.pharmacy.entity.User;
import com.hoquangnam45.pharmacy.exception.ApiError;
import com.hoquangnam45.pharmacy.pojo.CustomAuthentication;
import com.hoquangnam45.pharmacy.pojo.JwtToken;
import com.hoquangnam45.pharmacy.service.IAuthProvider;
import com.hoquangnam45.pharmacy.service.JwtService;
import com.hoquangnam45.pharmacy.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("oauth")
@Transactional
public class OAuthController {
    private final IAuthProvider googleAuthProvider;
    private final IAuthProvider facebookAuthProvider;
    private final UserService userService;
    private final OAuthConfig oauthConfig;
    private final JwtService jwtService;

    public OAuthController(IAuthProvider googleAuthProvider, IAuthProvider facebookAuthProvider, UserService userService, OAuthConfig oauthConfig, JwtService jwtService) {
        this.googleAuthProvider = googleAuthProvider;
        this.facebookAuthProvider = facebookAuthProvider;
        this.userService = userService;
        this.oauthConfig = oauthConfig;
        this.jwtService = jwtService;
    }

    @GetMapping("{provider}")
    public ResponseEntity<Void> authWithThirdPartyProvider(
            @PathVariable("provider") String provider,
            HttpServletResponse response) throws IOException {
        boolean authenticated = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::isAuthenticated)
                .orElse(false);
        if (authenticated) {
            return ResponseEntity.noContent().build();
        }
        AuthProvider authProvider = AuthProvider.parseProvider(provider);
        if (authProvider == null) {
            throw ApiError.badRequest("Invalid provider");
        }
        return switch (authProvider) {
            case GOOGLE -> {
                response.sendRedirect(googleAuthProvider.getRedirectUrl());
                yield ResponseEntity.noContent().build();
            }
            case FACEBOOK -> {
                response.sendRedirect(facebookAuthProvider.getRedirectUrl());
                yield ResponseEntity.noContent().build();
            }
        };
    }

    @GetMapping("{provider}/callback")
    private ResponseEntity<JwtToken> handleCallback(
            @PathVariable("provider") String provider,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse) throws IOException {
        AuthProvider authProvider = AuthProvider.parseProvider(provider);
        if (authProvider == null) {
            throw ApiError.badRequest("Invalid provider");
        }
        return switch (authProvider) {
            case GOOGLE -> {
                // https://developers.google.com/api-client-library/java/google-api-java-client/oauth2?hl=vi
                String authorizationCode = Optional.ofNullable(parseQueryString(servletRequest).get("code"))
                        .orElseThrow(() -> ApiError.badRequest("Invalid authorization code"));
                String userEmail = googleAuthProvider.getUser(authorizationCode);
                User user = userService.getUserByEmail(userEmail);
                if (user == null) {
                    // Redirect back to register page of application so that user must be registered first before able
                    // to login with oauth2 using different providers
                    servletResponse.sendRedirect(MessageFormat.format("{0}/register", oauthConfig.getApplicationUrl()));
                    yield ResponseEntity.noContent().build();
                }
                JwtToken jwtToken = jwtService.generateJwt(CustomAuthentication.authenticated(user));
                yield ResponseEntity.ok(jwtToken);
            }
            case FACEBOOK -> {
                String authorizationCode = Optional.ofNullable(parseQueryString(servletRequest).get("authorizationCode"))
                        .orElseThrow(() -> ApiError.badRequest("Invalid authorization code"));
                String userEmail = facebookAuthProvider.getUser(authorizationCode);
                User user = userService.getUserByEmail(userEmail);
                if (user == null) {
                    // Redirect back to register page of application so that user must be registered first before able
                    // to login with oauth2 using different providers
                    servletResponse.sendRedirect(MessageFormat.format("{0}/register", oauthConfig.getApplicationUrl()));
                    yield ResponseEntity.noContent().build();
                }
                JwtToken jwtToken = jwtService.generateJwt(CustomAuthentication.authenticated(user));
                yield ResponseEntity.ok(jwtToken);
            }
        };
    }

    private static Map<String, String> parseQueryString(HttpServletRequest servletRequest) {
        return Optional.ofNullable(servletRequest.getQueryString())
                .map(query -> query.split("&"))
                .stream()
                .flatMap(Stream::of)
                .map(pair -> pair.split("="))
                .filter(pair -> pair.length == 2)
                .collect(Collectors.toMap(
                        pair -> pair[0].trim(),
                        pair -> pair[1].trim()));
    }
}
