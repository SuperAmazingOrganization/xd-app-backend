package pl.vizja.xdbackend.exception;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@JsonPropertyOrder({"code", "message", "exceptions"})
class ValidationExceptionDTO extends ExceptionDTO {

    private List<FieldExceptionDTO> exceptions;

    @Getter
    @Setter
    @Builder
    static class FieldExceptionDTO {

        private String field;
        private String message;
    }
}
