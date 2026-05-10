package pl.vizja.xdbackend.comment.dto;

import jakarta.validation.constraints.Size;

public record UpdateCommentDTO(
        @Size(min = 1, max = 500, message = "must be between 1 and 500 characters long")
        String body
) {}
