package pl.vizja.xdbackend.user.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.vizja.xdbackend.user.UserRepository;

@Service
@RequiredArgsConstructor
public class UniquePhoneValidator implements
        ConstraintValidator<UniquePhoneConstraint, String> {

    private final UserRepository userRepository;

    @Override
    public void initialize(UniquePhoneConstraint phoneNumber) {
    }

    @Override
    public boolean isValid(String phoneNumber,
                           ConstraintValidatorContext cxt) {
        return phoneNumber == null || !userRepository.existsByPhone(phoneNumber);
    }

}