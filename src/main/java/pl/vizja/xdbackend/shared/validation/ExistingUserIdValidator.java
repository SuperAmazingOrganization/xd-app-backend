package pl.vizja.xdbackend.shared.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.vizja.xdbackend.user.UserRepository;

@Service
@RequiredArgsConstructor
public class ExistingUserIdValidator implements
        ConstraintValidator<ExistingUserIdConstraint, Long> {

    private final UserRepository userRepository;

    @Override
    public void initialize(ExistingUserIdConstraint authorId) {
    }

    @Override
    public boolean isValid(Long authorId,
                           ConstraintValidatorContext cxt) {
        return userRepository.existsById(authorId);
    }

}