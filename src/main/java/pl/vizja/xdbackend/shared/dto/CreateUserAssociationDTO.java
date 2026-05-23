package pl.vizja.xdbackend.shared.dto;

import jakarta.validation.constraints.NotNull;
import pl.vizja.xdbackend.shared.validation.ExistingUserIdConstraint;

public record CreateUserAssociationDTO(

        @ExistingUserIdConstraint
        @NotNull(message = "must not be empty")
        Long userId
) {}
