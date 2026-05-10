package pl.vizja.xdbackend.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CustomConstraintValidator {

    private final Validator validator;

    public void validate(Object object) {
        Set<ConstraintViolation<Object>> exceptions = validator.validate(object);
        if(!exceptions.isEmpty()) {
            throw new ConstraintViolationException(exceptions);
        }
    }
}