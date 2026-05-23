package pl.vizja.xdbackend.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import pl.vizja.xdbackend.user.UserRole;

import java.time.LocalDateTime;
import java.util.Optional;

@Builder
public record UserDTO(
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        Optional<Long> id,

        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        Optional<UserRole> role,

        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        Optional<String> email,

        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        Optional<String> phone,

        String username,

        String profilePicUrl,

        String backgroundPicUrl,

        String description,

        LocalDateTime joinedAt
) {}
