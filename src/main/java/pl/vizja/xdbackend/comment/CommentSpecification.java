package pl.vizja.xdbackend.comment;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class CommentSpecification {

    public Specification<Comment> build(Long postId, Long parentId, String body, LocalDateTime addedAtFrom, LocalDateTime addedAtTo,
                                        LocalDateTime updatedAtFrom, LocalDateTime updatedAtTo) {
        Specification<Comment> spec = (root, query, cb) -> cb.conjunction();
        if (postId != null) spec = spec.and(hasPostId(postId));
        if (parentId != null) spec = spec.and(hasParentId(parentId));
        if (body != null) spec = spec.and(hasBody(body));
        if (addedAtFrom != null) spec = spec.and(addedAfter(addedAtFrom));
        if (addedAtTo != null) spec = spec.and(addedBefore(addedAtTo));
        if (updatedAtFrom != null) spec = spec.and(updatedAfter(updatedAtFrom));
        if (updatedAtTo != null) spec = spec.and(updatedBefore(updatedAtTo));
        return spec;
    }

    private Specification<Comment> hasParentId(Long parentId) {
        return (root, query, cb)
                -> cb.equal(root.get("parent").get("id"), parentId);
    }

    private Specification<Comment> hasBody(String body) {
        if (body.isBlank()) return (root, query, cb)
                -> cb.isNull(root.get("body"));
        return (root, query, cb)
                -> cb.like(cb.lower(root.get("body")), "%" + body.toLowerCase() + "%");
    }

    private Specification<Comment> addedAfter(LocalDateTime date) {
        return (root, query, cb)
                -> cb.greaterThanOrEqualTo(root.get("addedAt"), date);
    }

    private Specification<Comment> addedBefore(LocalDateTime date) {
        return (root, query, cb)
                -> cb.lessThanOrEqualTo(root.get("addedAt"), date);
    }

    private Specification<Comment> updatedAfter(LocalDateTime date) {
        return (root, query, cb)
                -> cb.greaterThanOrEqualTo(root.get("updatedAt"), date);
    }

    private Specification<Comment> updatedBefore(LocalDateTime date) {
        return (root, query, cb)
                -> cb.lessThanOrEqualTo(root.get("updatedAt"), date);
    }


    private Specification<Comment> hasPostId(Long postId) {
        return (root, query, cb)
                -> cb.equal(root.get("post").get("id"), postId);
    }
}