package pl.vizja.xdbackend.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
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
                .message("Błąd walidacji")
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
                .message("Nie znaleziono ścieżki")
                .build(), HttpStatus.NOT_FOUND);
    }

    //wrong method for given route
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ExceptionDTO> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ignored
    ) {
        return new ResponseEntity<>(ExceptionDTO.builder()
                .code(405)
                .message("Nieobsługiwana metoda HTTP")
                .build(), HttpStatus.METHOD_NOT_ALLOWED);
    }

    //missing request body
    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ExceptionDTO> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ignored
    ) {
        return new ResponseEntity<>(ExceptionDTO.builder()
                .code(400)
                .message("Brak wymaganego ciała żądania")
                .build(), HttpStatus.BAD_REQUEST);
    }

    //wrong path variable type
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<ExceptionDTO> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ignored
    ) {
        return new ResponseEntity<>(ExceptionDTO.builder()
                .code(400)
                .message("Nieprawidłowy typ parametru")
                .build(), HttpStatus.BAD_REQUEST);
    }

    //bad login credentials
    @ExceptionHandler(AuthenticationException.class)
    ResponseEntity<ExceptionDTO> handleAuthenticationException(
            AuthenticationException ignored
    ) {
        return new ResponseEntity<>(ExceptionDTO.builder()
                .code(401)
                .message("Nieprawidłowy login lub hasło")
                .build(), HttpStatus.UNAUTHORIZED);
    }

    //access denied
    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ExceptionDTO> handleAccessDeniedException(
            AccessDeniedException ignored
    ) {
        return new ResponseEntity<>(ExceptionDTO.builder()
                .code(403)
                .message("Brak dostępu")
                .build(), HttpStatus.FORBIDDEN);
    }

    //illegal argument
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ExceptionDTO> handleIllegalArgumentException(
            IllegalArgumentException e
    ) {
        return new ResponseEntity<>(ExceptionDTO.builder()
                .code(400)
                .message(e.getMessage())
                .build(), HttpStatus.BAD_REQUEST);
    }
}


