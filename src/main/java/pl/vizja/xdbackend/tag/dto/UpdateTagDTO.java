package pl.vizja.xdbackend.tag.dto;

import jakarta.validation.constraints.Size;

public record UpdateTagDTO(
        @Size(min = 1, max = 50, message = "must be between 1 and 50 characters long")
        String name
) {}
