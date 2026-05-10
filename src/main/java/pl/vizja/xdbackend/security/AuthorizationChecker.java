package pl.vizja.xdbackend.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationChecker {

    public boolean isAdmin() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    public boolean isOwner(Long ownerId) {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal().equals(ownerId);
    }
}
