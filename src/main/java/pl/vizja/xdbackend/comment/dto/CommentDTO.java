package pl.vizja.xdbackend.comment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Optional;

@Builder
public record CommentDTO(
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        Optional<Long> id,

        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        Optional<Long> authorId,

        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        Optional<Long> postId,

        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        Optional<Long> parentId,

        String body,

        LocalDateTime addedAt,

        LocalDateTime updatedAt
) {}
