package pl.vizja.xdbackend.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import pl.vizja.xdbackend.user.validation.UniqueEmailConstraint;
import pl.vizja.xdbackend.user.validation.UniquePhoneConstraint;
import pl.vizja.xdbackend.user.validation.UniqueUsernameConstraint;

public record CreateUserDTO(
        @Email(message = "Nieprawidłowy format email")
        @UniqueEmailConstraint
        @NotNull(message = "Email nie może być pusty")
        String email,

        @Pattern(regexp = "^\\+?[1-9][0-9]{7,14}$", message = "must be a valid phone number")
        @UniquePhoneConstraint
        String phone,

        @Size(min = 1, max = 50, message = "Nazwa użytkownika musi mieć 1-50 znaków")
        @UniqueUsernameConstraint
        @NotNull(message = "Nazwa użytkownika nie może być pusta")
        String username,

        @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$",
                message = "Hasło musi mieć min. 8 znaków, zawierać dużą literę, małą literę, cyfrę i znak specjalny")
        @NotNull(message = "Hasło nie może być puste")
        String password)
{
}
