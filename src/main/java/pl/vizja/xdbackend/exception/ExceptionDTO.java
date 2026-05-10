package pl.vizja.xdbackend.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class ExceptionDTO {

    protected int code;
    protected String message;
}
