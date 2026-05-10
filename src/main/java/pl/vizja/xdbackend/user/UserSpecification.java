package pl.vizja.xdbackend.user;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class UserSpecification {

    public Specification<User> build(Long likedPostId, Long followingId, UserRole userRole, String email, String phone,
                                     String username, String description, LocalDateTime joinedAtFrom,
                                     LocalDateTime joinedAtTo, List<Long> tagIds) {
        Specification<User> spec = (root, query, cb) -> cb.conjunction();
        if (userRole != null) spec = spec.and(hasRole(userRole));
        if (email != null) spec = spec.and(hasEmail(email));
        if (phone != null) spec = spec.and(hasPhone(phone));
        if (username != null) spec = spec.and(hasUsername(username));
        if (description != null) spec = spec.and(hasDescription(description));
        if (joinedAtFrom != null) spec = spec.and(joinedAfter(joinedAtFrom));
        if (joinedAtTo != null) spec = spec.and(joinedBefore(joinedAtTo));
        if (tagIds != null && !tagIds.isEmpty()) spec = spec.and(hasTags(tagIds));
        if (followingId != null) spec = spec.and(hasFollowing(followingId));
        if (likedPostId != null) spec = spec.and(hasLikedPost(likedPostId));
        return spec;
    }

    private Specification<User> hasRole(UserRole role) {
        return (root, query, cb)
                -> cb.equal(root.get("role"), role);
    }

    private Specification<User> hasEmail(String email) {
        if (email.isBlank()) return (root, query, cb)
                -> cb.isNull(root.get("email"));
        return (root, query, cb)
                -> cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    private Specification<User> hasPhone(String phone) {
        if (phone.isBlank()) return (root, query, cb)
                -> cb.isNull(root.get("phone"));
        return (root, query, cb)
                -> cb.like(root.get("phone"), "%" + phone + "%");
    }

    private Specification<User> hasUsername(String username) {
        if (username.isBlank()) return (root, query, cb)
                -> cb.isNull(root.get("username"));
        return (root, query, cb)
                -> cb.like(cb.lower(root.get("username")), "%" + username.toLowerCase() + "%");
    }

    private Specification<User> hasDescription(String description) {
        if (description.isBlank()) return (root, query, cb)
                -> cb.isNull(root.get("description"));
        return (root, query, cb)
                -> cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%");
    }

    private Specification<User> joinedAfter(LocalDateTime date) {
        return (root, query, cb)
                -> cb.greaterThanOrEqualTo(root.get("joinedAt"), date);
    }

    private Specification<User> joinedBefore(LocalDateTime date) {
        return (root, query, cb)
                -> cb.lessThanOrEqualTo(root.get("joinedAt"), date);
    }

    private Specification<User> hasTags(List<Long> tagIds) {
        return (root, query, cb)
                -> {
            query.distinct(true);
            return cb.and(tagIds.stream()
                    .map(id -> root.join("tags", JoinType.INNER).get("id").in(id))
                    .toArray(Predicate[]::new));
        };
    }

    private Specification<User> hasFollowing(Long followingId) {
        return (root, query, cb)
                -> cb.equal(root.join("following").get("id"), followingId);
    }

    private Specification<User> hasLikedPost(Long postId) {
        return (root, query, cb)
                -> cb.equal(root.join("likedPosts").get("id"), postId);
    }
}