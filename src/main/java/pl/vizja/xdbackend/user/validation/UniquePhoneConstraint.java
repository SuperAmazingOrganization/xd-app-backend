package pl.vizja.xdbackend.user.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniquePhoneValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniquePhoneConstraint {
    String message() default "phone like that already exists";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}