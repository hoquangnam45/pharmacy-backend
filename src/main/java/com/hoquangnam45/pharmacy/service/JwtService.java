package com.hoquangnam45.pharmacy.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hoquangnam45.pharmacy.config.JwtConfig;
import com.hoquangnam45.pharmacy.constant.AuthenticationType;
import com.hoquangnam45.pharmacy.constant.JwtClaim;
import com.hoquangnam45.pharmacy.entity.RefreshToken;
import com.hoquangnam45.pharmacy.pojo.JwtToken;
import com.hoquangnam45.pharmacy.pojo.PrincipalSupplier;
import com.hoquangnam45.pharmacy.repo.RefreshTokenRepo;
import com.hoquangnam45.pharmacy.repo.UserRepo;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class JwtService {
    private final JwtConfig jwtConfig;
    private final Algorithm algorithm;
    private final RefreshTokenRepo refreshTokenRepo;
    private final UserRepo userRepo;
    private final JWTVerifier verifier;

    public JwtService(JwtConfig jwtConfig, RefreshTokenRepo refreshTokenRepo, UserRepo userRepo) {
        this.jwtConfig = jwtConfig;
        this.algorithm = Algorithm.HMAC256(jwtConfig.getSecret());
        this.refreshTokenRepo = refreshTokenRepo;
        this.verifier = JWT.require(algorithm)
                .withIssuer(jwtConfig.getIssuer())
                .build();
        this.userRepo = userRepo;
    }

    public JwtToken generateJwt(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime accessTokenExpiredAt = now.plus(jwtConfig.getAccessTokenExpirationInMin());
        OffsetDateTime refreshTokenExpiredAt = now.plus(jwtConfig.getRefreshTokenExpirationInMin());
        PrincipalSupplier principalSupplier = (PrincipalSupplier) authentication.getPrincipal();
        String accessToken = JWT.create()
                .withIssuer(jwtConfig.getIssuer())
                .withIssuedAt(now.toInstant())
                .withSubject(principalSupplier.getPrincipal())
                .withExpiresAt(accessTokenExpiredAt.toInstant())
                .withClaim(JwtClaim.ROLE, Optional.ofNullable(authentication.getAuthorities())
                        .map(authorities -> authorities.stream().map(GrantedAuthority::getAuthority))
                        .orElseGet(Stream::empty)
                        .collect(Collectors.toList()))
                .sign(algorithm);
        String refreshToken = UUID.randomUUID().toString();
        return new JwtToken(
                Base64.getEncoder().encodeToString(accessToken.getBytes()),
                Base64.getEncoder().encodeToString(refreshToken.getBytes()),
                accessTokenExpiredAt,
                refreshTokenExpiredAt);
    }

    public void invalidateRefreshTokenByAccessToken(String accessToken) {
        refreshTokenRepo.deleteRefreshTokenByAccessToken(accessToken);
    }

    public RefreshToken getRefreshTokenByAccessToken(String accessToken) {
        return refreshTokenRepo.findRefreshTokenRepoByAccessToken(accessToken);
    }

    public JwtToken refreshToken(Authentication authentication) {
        AuthenticationType authenticationType = (AuthenticationType) authentication.getDetails();
        if (authenticationType != AuthenticationType.REFRESH_TOKEN) {
            throw new IllegalStateException("Try to obtain refresh token for non token authentication");
        }
        return generateJwt(authentication);
    }

    public Pair<DecodedJWT, JWTVerificationException> decodeAccessTokenWithoutValidate(String accessToken) {
        try {
            return Pair.of(JWT.decode(accessToken), null);
        } catch (JWTVerificationException verificationException) {
            return Pair.of(null, verificationException);
        }
    }

    public Pair<DecodedJWT, JWTVerificationException> validateAccessToken(String accessToken) {
        try {
            return Pair.of(verifier.verify(accessToken), null);
        } catch (JWTVerificationException verificationException) {
            return Pair.of(null, verificationException);
        }
    }

    public void storeToken(JwtToken newToken) {
        RefreshToken newRefreshToken = new RefreshToken();
        String decodedAccessToken = new String(Base64.getDecoder().decode(newToken.getAccessToken()));
        UUID userId = UUID.fromString(decodeAccessTokenWithoutValidate(decodedAccessToken).getLeft().getSubject());
        newRefreshToken.setAccessToken(newToken.getAccessToken());
        newRefreshToken.setRefreshToken(newToken.getRefreshToken());
        newRefreshToken.setExpiredAt(newToken.getRefreshTokenExpiredAt());
        newRefreshToken.setUser(userRepo.getReferenceById(userId));
        refreshTokenRepo.save(newRefreshToken);
    }
}
