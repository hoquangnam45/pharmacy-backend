package com.hoquangnam45.pharmacy.controller;

import com.hoquangnam45.pharmacy.entity.RefreshToken;
import com.hoquangnam45.pharmacy.entity.User;
import com.hoquangnam45.pharmacy.exception.ApiError;
import com.hoquangnam45.pharmacy.pojo.CustomAuthentication;
import com.hoquangnam45.pharmacy.pojo.GenericResponse;
import com.hoquangnam45.pharmacy.pojo.JwtToken;
import com.hoquangnam45.pharmacy.pojo.LoginRequest;
import com.hoquangnam45.pharmacy.pojo.RefreshRequest;
import com.hoquangnam45.pharmacy.pojo.RegisterRequest;
import com.hoquangnam45.pharmacy.service.JwtService;
import com.hoquangnam45.pharmacy.service.UserService;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@RestController
@RequestMapping("auth")
@Transactional
public class AuthController {
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtService jwtService) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("register")
    public ResponseEntity<JwtToken> register(@RequestBody RegisterRequest registerRequest) {
        if (userService.getUserByEmail(registerRequest) != null) {
            throw ApiError.conflict("Email address is already registered");
        } else if (userService.getUserByUsername(registerRequest) != null) {
            throw ApiError.conflict("Username is already registered");
        }
        User user = userService.createUser(registerRequest);
        Authentication authentication = CustomAuthentication.authenticated(user);
        JwtToken jwtToken = jwtService.generateJwt(authentication);
        jwtService.storeToken(jwtToken);
        return ResponseEntity.ok(jwtToken);
    }

    @PostMapping("login")
    public ResponseEntity<JwtToken> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(CustomAuthentication.unauthenticated(loginRequest));
        if (!authentication.isAuthenticated()) {
            throw ApiError.unauthorized((String) authentication.getDetails());
        }
        JwtToken jwtToken = jwtService.generateJwt(authentication);
        jwtService.storeToken(jwtToken);
        return ResponseEntity.ok(jwtToken);
    }

    @PostMapping("logout")
    public ResponseEntity<GenericResponse> logout(HttpServletRequest servletRequest) {
        String path = servletRequest.getServletPath();
        GenericResponse successfullyLogoutResponse = new GenericResponse(HttpStatus.OK, path, "Logout successfully");
        String accessToken = getAccessTokenAndValidate(servletRequest);
        if (accessToken == null) {
            return ResponseEntity.ok(successfullyLogoutResponse);
        }
        jwtService.invalidateRefreshTokenByAccessToken(accessToken);
        return ResponseEntity.ok(successfullyLogoutResponse);
    }

    @PostMapping("refresh")
    public ResponseEntity<JwtToken> refresh(
            @RequestBody RefreshRequest refreshRequest,
            HttpServletRequest servletRequest) {
        String accessToken = getAccessTokenAndValidate(servletRequest);
        if (accessToken == null) {
            throw ApiError.badRequest("Missing authorization header");
        }
        RefreshToken refreshToken = jwtService.getRefreshTokenByAccessToken(accessToken);
        if (refreshToken == null) {
            throw ApiError.notFound("Stored refresh token associate with your access token is not found");
        }
        if (OffsetDateTime.now(ZoneOffset.UTC).isAfter(refreshToken.getExpiredAt())) {
            jwtService.invalidateRefreshTokenByAccessToken(accessToken);
            throw ApiError.forbidden("Stored refresh token associated with your access token has been expired");
        }
        if (!refreshToken.getRefreshToken().equals(refreshRequest.getRefreshToken())) {
            throw ApiError.forbidden("Stored refresh token associated with your access token does not match with refresh token submitted in request");
        }
        Authentication authentication = CustomAuthentication.authenticated(refreshToken);
        JwtToken newToken = jwtService.refreshToken(authentication);
        jwtService.invalidateRefreshTokenByAccessToken(accessToken);
        jwtService.storeToken(newToken);
        return ResponseEntity.ok().body(newToken);
    }

    private String getAccessTokenAndValidate(HttpServletRequest servletRequest) {
        String authorizationHeader = servletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(authorizationHeader)) {
            // This return null but not throw exception because there's some use case like logout
            // where this method is used but access token can be allowed to be empty without returning
            // error
            return null;
        }
        String[] authorizationHeaderParts = authorizationHeader.split(" ");
        if (authorizationHeaderParts.length != 2) {
            throw new BadCredentialsException("Invalid authorization header");
        }
        String scheme = authorizationHeaderParts[0];
        if (!"Bearer".equals(scheme)) {
            throw new BadCredentialsException("Invalid authorization scheme");
        }
        return authorizationHeaderParts[1];
    }
}
