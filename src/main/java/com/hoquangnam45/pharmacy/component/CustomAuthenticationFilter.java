package com.hoquangnam45.pharmacy.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoquangnam45.pharmacy.constant.LoginType;
import com.hoquangnam45.pharmacy.pojo.CustomAuthentication;
import com.hoquangnam45.pharmacy.pojo.GenericResponse;
import com.hoquangnam45.pharmacy.pojo.LoginRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

public class CustomAuthenticationFilter extends OncePerRequestFilter {
    private final AuthenticationManager authenticationManager;
    private final ObjectMapper objectMapper;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper) {
        this.authenticationManager = authenticationManager;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }
        String authorizationStr = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(authorizationStr)) {
            filterChain.doFilter(request, response);
            return;
        }
        String[] authorizationParts = authorizationStr.split(" ");
        if (authorizationParts.length != 2) {
            filterChain.doFilter(request, response);
            return;
        }
        String scheme = authorizationParts[0];
        Authentication authentication = switch (scheme) {
            case "Bearer": {
                String accessToken = new String(Base64.getDecoder().decode(authorizationParts[1]));
                Authentication unauthenticated = CustomAuthentication.unauthenticatedAccessToken(accessToken);
                yield authenticationManager.authenticate(unauthenticated);
            }
            case "Basic": {
                String usernamePassword = new String(Base64.getDecoder().decode(authorizationParts[1]));
                String[] usernamePasswordParts = usernamePassword.split(":");
                if (usernamePasswordParts.length != 2) {
                    yield null;
                }
                String username = usernamePasswordParts[0];
                String password = usernamePasswordParts[1];
                Authentication unauthenticated = CustomAuthentication.unauthenticated(new LoginRequest(username, password, LoginType.USERNAME));
                yield authenticationManager.authenticate(unauthenticated);
            }
            default:
                yield null;
        };
        if (authentication != null) {
            if (!authentication.isAuthenticated()) {
                response.setStatus(401);
                response.setHeader("Content-Type", "application/json");
                response.getOutputStream().write(objectMapper.writeValueAsBytes(new GenericResponse(
                        401,
                        request.getServletPath(),
                        (String) authentication.getDetails())));
                return;
            } else {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }
}
