package pl.vizja.xdbackend.user.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = OldPasswordValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OldPasswordConstraint {
    String message() default "old password must be the same as the old one";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}