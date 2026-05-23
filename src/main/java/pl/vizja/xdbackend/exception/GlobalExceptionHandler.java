package pl.vizja.xdbackend.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//ACCESS DENIED IS NOT HANDLED HERE!!! (SPRING SECURITY LIMITATION)

@RestControllerAdvice
class GlobalExceptionHandler {

    //validation exceptions (DTOs)
    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ExceptionDTO> handleConstraintViolationException
            (ConstraintViolationException e) {

        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        List<ValidationExceptionDTO.FieldExceptionDTO> fieldExceptions = new ArrayList<>();

        for(ConstraintViolation<?> cv : constraintViolations) {
            fieldExceptions.add(
                    ValidationExceptionDTO.FieldExceptionDTO
                            .builder()
                            .field(cv.getPropertyPath().toString())
                            .message(cv.getMessage())
                            .build()
            );
        }
        return new ResponseEntity<>(ValidationExceptionDTO
                .builder()
                .code(400)
                .message("Validation exception!")
                .exceptions(fieldExceptions)
                .build(), HttpStatus.BAD_REQUEST);
    }

    //not found entities
    @ExceptionHandler(EntityNotFoundException.class)
    ResponseEntity<ExceptionDTO> handleEntityNotFoundException(EntityNotFoundException e) {
        return new ResponseEntity<>(ExceptionDTO.builder()
                .code(404)
                .message(e.getMessage())
                .build(), HttpStatus.NOT_FOUND);
    }

    //wrong route
    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<ExceptionDTO> handleNoResourceFoundException(NoResourceFoundException ignored) {
        return new ResponseEntity<>(ExceptionDTO.builder()
                .code(404)
                .message("No such route!")
                .build(), HttpStatus.NOT_FOUND);
    }

    //wrong method for given route
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ExceptionDTO> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ignored
    ) {
        return new ResponseEntity<>(ExceptionDTO.builder()
                .code(405)
                .message("No such method for this route!")
                .build(), HttpStatus.METHOD_NOT_ALLOWED);
    }

    //missing request body
    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ExceptionDTO> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ignored
    ) {
        return new ResponseEntity<>(ExceptionDTO.builder()
                .code(400)
                .message("Missing request body!")
                .build(), HttpStatus.BAD_REQUEST);
    }

    //wrong path variable type
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<ExceptionDTO> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ignored
    ) {
        return new ResponseEntity<>(ExceptionDTO.builder()
                .code(400)
                .message("Invalid path variable type")
                .build(), HttpStatus.BAD_REQUEST);
    }
}


