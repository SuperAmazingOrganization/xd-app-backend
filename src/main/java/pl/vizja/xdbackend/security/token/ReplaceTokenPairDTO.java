package pl.vizja.xdbackend.security.token;

import jakarta.validation.constraints.NotNull;

record ReplaceTokenPairDTO(
        @NotNull(message = "refreshToken cannot be empty")
        String refreshToken) {
}
