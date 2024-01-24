package com.hoquangnam45.pharmacy.controller;

import com.google.api.services.oauth2.model.Userinfo;
import com.hoquangnam45.pharmacy.constant.AuthProvider;
import com.hoquangnam45.pharmacy.entity.User;
import com.hoquangnam45.pharmacy.exception.ApiError;
import com.hoquangnam45.pharmacy.pojo.CustomAuthentication;
import com.hoquangnam45.pharmacy.pojo.FacebookUserInfo;
import com.hoquangnam45.pharmacy.pojo.JwtToken;
import com.hoquangnam45.pharmacy.pojo.RedirectResponse;
import com.hoquangnam45.pharmacy.service.IOAuth2Service;
import com.hoquangnam45.pharmacy.service.JwtService;
import com.hoquangnam45.pharmacy.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@RestController
@RequestMapping("oauth2")
@Transactional
public class OAuth2Controller {
    private final IOAuth2Service<Userinfo> googleAuthProvider;
    private final IOAuth2Service<FacebookUserInfo> facebookAuthProvider;
    private final UserService userService;
    private final JwtService jwtService;

    public OAuth2Controller(
            IOAuth2Service<Userinfo> googleAuthProvider,
            IOAuth2Service<FacebookUserInfo> facebookAuthProvider,
            UserService userService,
            JwtService jwtService) {
        this.googleAuthProvider = googleAuthProvider;
        this.facebookAuthProvider = facebookAuthProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @GetMapping("{provider}")
    public ResponseEntity<RedirectResponse> authWithThirdPartyProvider(
            @PathVariable("provider") String provider,
            @RequestParam("state") String state) {
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
            case GOOGLE -> ResponseEntity.ok(new RedirectResponse(googleAuthProvider.getAuthorizationUrl(state)));
            case FACEBOOK -> ResponseEntity.ok(new RedirectResponse(facebookAuthProvider.getAuthorizationUrl(state)));
        };
    }

    @GetMapping("{provider}/callback")
    public ResponseEntity<JwtToken> handleCallback(
            @PathVariable("provider") String provider,
            @RequestParam("authorizationCode") String authorizationCode,
            @RequestParam("state") String state) throws Exception {
        AuthProvider authProvider = AuthProvider.parseProvider(provider);
        if (authProvider == null) {
            throw ApiError.badRequest("Invalid provider");
        }
        return switch (authProvider) {
            case GOOGLE -> {
                // https://developers.google.com/api-client-library/java/google-api-java-client/oauth2?hl=vi
                Userinfo userinfo = googleAuthProvider.getUser(state, authorizationCode);
                // Return additional detail in the error message so that application can automatically
                // fill in additional information for the user in the register form after redirection
                User user = Optional.ofNullable(userService.getUserByEmail(userinfo.getEmail()))
                        .orElseThrow(() -> ApiError.notFound("User not registered yet", userinfo));
                JwtToken jwtToken = jwtService.generateJwt(CustomAuthentication.authenticated(user));
                yield ResponseEntity.ok(jwtToken);
            }
            case FACEBOOK -> {
                FacebookUserInfo userInfo = facebookAuthProvider.getUser(state, authorizationCode);
                if (userInfo.getEmail() == null) {
                    throw ApiError.notFound("Facebook user do not have main email address");
                }
                User user = Optional.of(userInfo.getEmail())
                        .map(userService::getUserByEmail)
                        .orElseThrow(() -> ApiError.notFound("User not registered yet", userInfo));
                JwtToken jwtToken = jwtService.generateJwt(CustomAuthentication.authenticated(user));
                yield ResponseEntity.ok(jwtToken);
            }
        };
    }
}
