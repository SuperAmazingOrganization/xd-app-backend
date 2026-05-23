package pl.vizja.xdbackend.security;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import pl.vizja.xdbackend.user.User;
import pl.vizja.xdbackend.user.UserRepository;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    //log in using email, username or phone number
    @Override
    public UserDetails loadUserByUsername(String identifier) {
        if(!userRepository.existsByUsernameOrPhoneOrEmail(identifier, identifier, identifier)) {
            throw new EntityNotFoundException("User (" + identifier + ") was not found!");
        }
        User user = userRepository.findByUsernameOrPhoneOrEmail(identifier, identifier, identifier);
        return new org.springframework.security.core.userdetails.User(
                //id instead of username
            Long.toString(user.getId()),
            user.getPassword(),
                getAuthority(user)
        );
    }

    private Collection<? extends GrantedAuthority> getAuthority(User user) {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(user.getRole().name());
        return List.of(grantedAuthority);
    }

    public UserDetails loadUserById(Long id) {
        if(!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User (id=" + id + ") was not found!");
        }
        User user = userRepository.findUserById(id);
        return new org.springframework.security.core.userdetails.User(
                Long.toString(user.getId()),
                user.getPassword(),
                getAuthority(user)
        );
    }
}
