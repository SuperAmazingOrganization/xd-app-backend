package pl.vizja.xdbackend.post.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pl.vizja.xdbackend.shared.validation.ExistingUserIdConstraint;

public record CreatePostDTO(
        @ExistingUserIdConstraint
        @NotNull(message = "must not be empty")
        Long authorId,

        @Size(min = 1, max = 1000, message = "must be between 1 and 1000 characters long")
        @NotNull(message = "must not be empty")
        String body
) {
}
