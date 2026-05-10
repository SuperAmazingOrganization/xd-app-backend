package pl.vizja.xdbackend.security.token;

import jakarta.validation.constraints.NotNull;

record CreateTokenPairDTO(
    @NotNull(message = "identifier (phone / email / username) cannot be empty")
    String identifier,

    @NotNull(message = "password cannot be empty")
    String password
) {}
