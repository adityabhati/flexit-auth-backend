package com.flexit.user_management.repository;

import com.flexit.user_management.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {

    RefreshToken findByRefreshToken(String refreshToken);

    @Query(value = """
           INSERT INTO refresh_token (user_id, refresh_token, expiry_date)
           VALUES (:userId, :refreshToken, :expiryDate)
           ON CONFLICT (user_id) DO UPDATE
           SET refresh_token = EXCLUDED.refresh_token,
               expiry_date = EXCLUDED.expiry_date
           RETURNING *
           """, nativeQuery = true)
    RefreshToken updateInsertRefreshToken(@Param("userId") Long userId, @Param("refreshToken") String refreshToken, @Param("expiryDate") Long expiryDate);
}