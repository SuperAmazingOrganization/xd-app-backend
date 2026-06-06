package pl.vizja.xdbackend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import pl.vizja.xdbackend.user.User;
import pl.vizja.xdbackend.user.UserRepository;
import pl.vizja.xdbackend.user.UserRole;

@Component
@Profile({"h2", "h2file"})
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        seed("alice",   "alice@example.com",   "Alice1234!@#$", "Hi, I'm Alice. Demo account.",   "+48501111111");
        seed("bob",     "bob@example.com",     "Bob12345!@#$",  "Bob here. Coffee enthusiast.",   "+48502222222");
        seed("charlie", "charlie@example.com", "Charlie1!@#$", "Charlie — backend dev.",          "+48503333333");
        seed("diana",   "diana@example.com",   "Diana1234!@#$","Designer. UX, typography, cats.", "+48504444444");
    }

    private void seed(String username, String email, String rawPassword, String description, String phone) {
        User u = User.builder()
                .username(username)
                .email(email)
                .phone(phone)
                .password(passwordEncoder.encode(rawPassword))
                .description(description)
                .build();
        userRepository.save(u);
    }
}
