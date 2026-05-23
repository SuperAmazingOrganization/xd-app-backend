package pl.vizja.xdbackend.tag.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.Optional;

@Builder
public record TagDTO(
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        Optional<Long> id,

        String name
) {}
