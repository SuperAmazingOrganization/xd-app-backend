package pl.vizja.xdbackend.user.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.vizja.xdbackend.user.UserRepository;

@Service
@RequiredArgsConstructor
public class UniqueEmailValidator implements
        ConstraintValidator<UniqueEmailConstraint, String> {

    private final UserRepository userRepository;

    @Override
    public void initialize(UniqueEmailConstraint email) {
    }

    @Override
    public boolean isValid(String email,
                           ConstraintValidatorContext cxt) {
        return email == null || !userRepository.existsByEmail(email);
    }

}