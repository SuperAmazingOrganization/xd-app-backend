package pl.vizja.xdbackend.security.token;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TokenProcessor {

    @Value("${app.access-token.secret}")
    private String accessTokenSecret;

    @Value("${app.access-token.expiration-ms}")
    private long accessTokenExpirationMs;

    @Value("${app.refresh-token.expiration-ms}")
    private long refreshTokenExpirationMs;

    public String generateAccessToken(Authentication authentication) {
        return generateToken(authentication, accessTokenExpirationMs, null);
    }

    public String generateRefreshToken(Authentication authentication) {
        Map<String, String> claims = new HashMap<>();
        claims.put("tokenType", "refresh");
        return generateToken(authentication, refreshTokenExpirationMs, claims);
    }

    private String generateToken(Authentication authentication, long expirationMs, Map<String, String> claims) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expirationMs);

        return Jwts
                .builder()
                .header()
                .add("typ", "JWT")
                .and()
                .subject(userPrincipal.getUsername())
                .claims(claims)
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(getSignInKey())
                .compact();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(accessTokenSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isValidToken(String token, UserDetails userDetails) {
        final String username = extractUsernameFromToken(token);
        if(!username.equals(userDetails.getUsername())) {
            return false;
        }
        try {
            Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SignatureException e) {
            throw new IllegalArgumentException("Access token contains bad signature!");
        } catch (MalformedJwtException e) {
            throw new IllegalArgumentException("Access token malformed!");
        } catch (UnsupportedJwtException e) {
            throw new IllegalArgumentException("Access token unsupported!");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Access token claims empty!");
        }
    }

    public String extractUsernameFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            throw new IllegalArgumentException("Access token expired!");
        }
    }

    public boolean isRefreshToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return "refresh".equals(claims.get("tokenType"));
    }
}
