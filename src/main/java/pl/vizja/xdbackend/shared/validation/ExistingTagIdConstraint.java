package pl.vizja.xdbackend.shared.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ExistingTagIdValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistingTagIdConstraint {
    String message() default "tag like that doesn't exist";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}