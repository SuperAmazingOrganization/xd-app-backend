package pl.vizja.xdbackend.shared.dto;

import jakarta.validation.constraints.NotNull;
import pl.vizja.xdbackend.shared.validation.ExistingTagIdConstraint;

public record CreateTagAssociationDTO(
        @ExistingTagIdConstraint
        @NotNull(message = "must not be empty")
        Long tagId
) {}
