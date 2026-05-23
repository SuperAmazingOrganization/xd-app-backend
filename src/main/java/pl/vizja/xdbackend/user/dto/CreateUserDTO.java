package pl.vizja.xdbackend.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import pl.vizja.xdbackend.user.validation.UniqueEmailConstraint;
import pl.vizja.xdbackend.user.validation.UniquePhoneConstraint;
import pl.vizja.xdbackend.user.validation.UniqueUsernameConstraint;

public record CreateUserDTO(
        @Email(message = "must be a valid email")
        @UniqueEmailConstraint
        @NotNull(message = "must not be empty")
        String email,

        @Pattern(regexp = "^\\+?[1-9][0-9]{7,14}$", message = "must be a valid phone number")
        @UniquePhoneConstraint
        @NotNull(message = "must not be empty")
        String phone,

        @Size(min = 1, max = 50, message = "must be between 1 and 50 characters long")
        @UniqueUsernameConstraint
        @NotNull(message = "must not be empty")
        String username,

        @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$",
                message = "must be between 1 and 50 characters long and have a minimum length of 8 characters, " +
                        "include at least one uppercase english letter, one lowercase english letter, " +
                        "one digit, and one special character")
        @NotNull(message = "must not be empty")
        String password)
{
}
