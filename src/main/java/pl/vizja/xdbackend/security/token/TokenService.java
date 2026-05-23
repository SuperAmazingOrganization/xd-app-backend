package pl.vizja.xdbackend.security.token;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pl.vizja.xdbackend.security.CustomUserDetailsService;
import pl.vizja.xdbackend.validation.CustomConstraintValidator;
import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class TokenService {

    private final CustomConstraintValidator constraintValidator;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomUserDetailsService userDetailsService;
    private final TokenProcessor tokenProcessor;

    @Value("${app.refresh-token.expiration-ms}")
    private long refreshTokenExpirationMs;

    TokenPairDTO createTokenPair(CreateTokenPairDTO createTokenPairDTO) {
        constraintValidator.validate(createTokenPairDTO);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        createTokenPairDTO.identifier(),
                        createTokenPairDTO.password()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        TokenPairDTO tokenPairDTO = generateTokenPair(authentication);
        refreshTokenRepository.save(RefreshToken.builder()
                .token(tokenPairDTO.refreshToken())
                .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpirationMs/1000))
                .build());

        return tokenPairDTO;
    }

    TokenPairDTO replaceTokenPair(ReplaceTokenPairDTO replaceTokenPairDTO) {

        constraintValidator.validate(replaceTokenPairDTO);
        final String refreshToken = replaceTokenPairDTO.refreshToken();

        if (!tokenProcessor.isRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Not a refresh token!");
        }

        if(!refreshTokenRepository.existsByToken(refreshToken)) {
            throw new EntityNotFoundException("Token (token="
                    + replaceTokenPairDTO.refreshToken() + ") was not found!");
        }
        RefreshToken existingToken = refreshTokenRepository.findByToken(refreshToken);

        String userId = tokenProcessor.extractUsernameFromToken(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserById(Long.parseLong(userId));

        if (userDetails == null) {
            throw new EntityNotFoundException("User (id=" + userId + ") was not found!");
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );

        TokenPairDTO tokenPairDTO = generateTokenPair(authentication);

        existingToken.setToken(tokenPairDTO.refreshToken());
        existingToken.setExpiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpirationMs/1000));
        refreshTokenRepository.save(existingToken);

        return tokenPairDTO;
    }

    void deleteRefreshToken(DeleteRefreshTokenDTO deleteRefreshTokenDTO) {

        final String refreshToken = deleteRefreshTokenDTO.refreshToken();
        if(!refreshTokenRepository.existsByToken(refreshToken)) {
            throw new EntityNotFoundException("Token (token="
                    + deleteRefreshTokenDTO.refreshToken() + ") was not found!");
        }
        refreshTokenRepository.deleteByToken(refreshToken);
    }

    private TokenPairDTO generateTokenPair(Authentication authentication) {
        String accessToken = tokenProcessor.generateAccessToken(authentication);
        String refreshToken = tokenProcessor.generateRefreshToken(authentication);
        return TokenPairDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

}
