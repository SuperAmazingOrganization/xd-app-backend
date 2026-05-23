package pl.vizja.xdbackend.security.token;

import jakarta.validation.constraints.NotNull;

record DeleteRefreshTokenDTO(
        @NotNull(message = "refreshToken cannot be empty")
        String refreshToken) {
}
