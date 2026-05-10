package pl.vizja.xdbackend.post;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
class PostSpecification {

    public Specification<Post> build(String body, LocalDateTime addedAtFrom, LocalDateTime addedAtTo,
                                     LocalDateTime updatedAtFrom, LocalDateTime updatedAtTo, List<Long> tagIds) {
        Specification<Post> spec = (root, query, cb)
                -> cb.conjunction();
        if (body != null) spec = spec.and(hasBody(body));
        if (addedAtFrom != null) spec = spec.and(addedAfter(addedAtFrom));
        if (addedAtTo != null) spec = spec.and(addedBefore(addedAtTo));
        if (updatedAtFrom != null) spec = spec.and(updatedAfter(updatedAtFrom));
        if (updatedAtTo != null) spec = spec.and(updatedBefore(updatedAtTo));
        if (tagIds != null && !tagIds.isEmpty()) spec = spec.and(hasTags(tagIds));
        return spec;
    }

    private Specification<Post> hasBody(String body) {
        if (body.isBlank()) return (root, query, cb)
                -> cb.isNull(root.get("body"));
        return (root, query, cb)
                -> cb.like(cb.lower(root.get("body")), "%" + body.toLowerCase() + "%");
    }

    private Specification<Post> addedAfter(LocalDateTime date) {
        return (root, query, cb)
                -> cb.greaterThanOrEqualTo(root.get("addedAt"), date);
    }

    private Specification<Post> addedBefore(LocalDateTime date) {
        return (root, query, cb)
                -> cb.lessThanOrEqualTo(root.get("addedAt"), date);
    }

    private Specification<Post> updatedAfter(LocalDateTime date) {
        return (root, query, cb)
                -> cb.greaterThanOrEqualTo(root.get("updatedAt"), date);
    }

    private Specification<Post> updatedBefore(LocalDateTime date) {
        return (root, query, cb)
                -> cb.lessThanOrEqualTo(root.get("updatedAt"), date);
    }

    private Specification<Post> hasTags(List<Long> tagIds) {
        return (root, query, cb)
                -> {
            query.distinct(true);
            return cb.and(tagIds.stream()
                    .map(id -> root.join("tags", JoinType.INNER).get("id").in(id))
                    .toArray(Predicate[]::new));
        };
    }
}