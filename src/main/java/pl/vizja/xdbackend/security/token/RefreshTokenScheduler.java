package pl.vizja.xdbackend.security.token;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
class RefreshTokenScheduler {

    private final RefreshTokenRepository refreshTokenRepository;

    //every 1hr
    @Scheduled(cron = "0 0 * * * *")
    public void deleteExpiredTokens() {
        refreshTokenRepository.deleteAllExpiredTokens(LocalDateTime.now());
    }
}
