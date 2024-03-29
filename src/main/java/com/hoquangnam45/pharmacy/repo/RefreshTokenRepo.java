package com.hoquangnam45.pharmacy.repo;

import com.hoquangnam45.pharmacy.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken, UUID> {
    void deleteRefreshTokenByAccessToken(String accessToken);

    @Query("SELECT r FROM RefreshToken r JOIN FETCH r.user WHERE r.accessToken=:accessToken")
    RefreshToken findRefreshTokenRepoByAccessToken(String accessToken);
}
