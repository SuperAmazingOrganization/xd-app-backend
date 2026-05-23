package pl.vizja.xdbackend.user.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import pl.vizja.xdbackend.user.dto.UpdateUserDTO;

public class PasswordPairValidator implements ConstraintValidator<PasswordPairConstraint, UpdateUserDTO> {

    @Override
    public boolean isValid(UpdateUserDTO dto, ConstraintValidatorContext context) {
        String oldPassword = dto.oldPassword();
        String newPassword = dto.newPassword();

        if (oldPassword == null && newPassword == null) return true;

        context.disableDefaultConstraintViolation();

        if (oldPassword == null) {
            context.buildConstraintViolationWithTemplate("oldPassword is required when newPassword is present")
                    .addPropertyNode("oldPassword")
                    .addConstraintViolation();
            return false;
        }

        if (newPassword == null) {
            context.buildConstraintViolationWithTemplate("newPassword is required when oldPassword is present")
                    .addPropertyNode("newPassword")
                    .addConstraintViolation();
            return false;
        }

        if (oldPassword.equals(newPassword)) {
            context.buildConstraintViolationWithTemplate("newPassword must be different from oldPassword")
                    .addPropertyNode("newPassword")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}