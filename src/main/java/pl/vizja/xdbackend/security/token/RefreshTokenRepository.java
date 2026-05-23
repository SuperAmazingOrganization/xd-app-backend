package pl.vizja.xdbackend.security.token;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    boolean existsByToken(String refreshToken);
    RefreshToken findByToken(String token);
    void deleteByToken(String token);

    @Modifying
    @Transactional
    @NativeQuery("DELETE FROM refresh_tokens rt WHERE rt.expiresAt < :now")
    void deleteAllExpiredTokens(@Param("now") LocalDateTime now);
}
