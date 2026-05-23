package pl.vizja.xdbackend.security.token;

import lombok.Builder;

@Builder
record TokenPairDTO(String accessToken, String refreshToken) {
}
