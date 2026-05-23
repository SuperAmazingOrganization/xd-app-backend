package pl.vizja.xdbackend.post.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Optional;

@Builder
public record PostDTO(
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        Optional<Long> id,

        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        Optional<Long> authorId,

        String body,

        LocalDateTime addedAt,

        LocalDateTime updatedAt
) {}
