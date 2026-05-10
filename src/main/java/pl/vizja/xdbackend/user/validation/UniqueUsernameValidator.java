package pl.vizja.xdbackend.user.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.vizja.xdbackend.user.UserRepository;

@Service
@RequiredArgsConstructor
public class UniqueUsernameValidator implements
        ConstraintValidator<UniqueUsernameConstraint, String> {

    private final UserRepository userRepository;

    @Override
    public void initialize(UniqueUsernameConstraint username) {
    }

    @Override
    public boolean isValid(String username,
                           ConstraintValidatorContext cxt) {
        return username == null || !userRepository.existsByUsername(username);
    }

}