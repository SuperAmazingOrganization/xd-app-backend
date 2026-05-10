package pl.vizja.xdbackend.post.dto;

import jakarta.validation.constraints.Size;

public record UpdatePostDTO(
        @Size(min = 1, max = 1000, message = "must be between 1 and 1000 characters long")
        String body
) {
}
