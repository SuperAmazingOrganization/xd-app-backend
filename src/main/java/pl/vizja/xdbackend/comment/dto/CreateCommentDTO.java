package pl.vizja.xdbackend.comment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pl.vizja.xdbackend.shared.validation.ExistingUserIdConstraint;

public record CreateCommentDTO(
        @ExistingUserIdConstraint
        @NotNull(message = "must not be empty")
        Long authorId,

        @Size(min = 1, max = 500, message = "must be between 1 and 500 characters long")
        @NotNull(message = "must not be empty")
        String body
) {}
