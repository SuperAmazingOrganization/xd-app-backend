package pl.vizja.xdbackend.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import pl.vizja.xdbackend.user.UserRole;
import pl.vizja.xdbackend.user.validation.*;

import java.time.LocalDateTime;

@PasswordPairConstraint
public record UpdateUserDTO(
        UserRole role,

        @Email(message = "must be a valid email")
        @UniqueEmailConstraint
        String email,

        @Pattern(regexp = "^\\+?[1-9][0-9]{7,14}$", message = "must be a valid phone number")
        @UniquePhoneConstraint
        String phone,

        @Size(min = 1, max = 50, message = "must be between 1 and 50 characters long")
        @UniqueUsernameConstraint
        String username,

        @OldPasswordConstraint
        String oldPassword,

        @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$",
                message = "must be between 1 and 50 characters long and have a minimum length of 8 characters, " +
                        "include at least one uppercase english letter, one lowercase english letter, " +
                        "one digit, and one special character")
        String newPassword,

        @Size(min = 1, max = 500, message = "must bet between 1 and 500 characters long")
        String description,

        LocalDateTime joinedAt
) {}
