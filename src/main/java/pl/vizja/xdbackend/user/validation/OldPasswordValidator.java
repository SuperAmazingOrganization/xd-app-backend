package pl.vizja.xdbackend.user.validation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.vizja.xdbackend.user.User;
import pl.vizja.xdbackend.user.UserRepository;

@RequiredArgsConstructor
public class OldPasswordValidator implements ConstraintValidator<OldPasswordConstraint, String> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final HttpServletRequest request;

    @Override
    public boolean isValid(String oldPassword, ConstraintValidatorContext context) {
        if (oldPassword == null) return true;
        Long userId = Long.parseLong(request.getRequestURI().split("/")[2]);
        User user = userRepository.findUserById(userId);
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }
}