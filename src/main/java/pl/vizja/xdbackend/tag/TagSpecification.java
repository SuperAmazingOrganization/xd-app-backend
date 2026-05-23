package pl.vizja.xdbackend.tag;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TagSpecification {

    public Specification<Tag> build(Long userId, Long postId, String name) {
        Specification<Tag> spec = (root, query, cb)
                -> cb.conjunction();
        if (postId != null) spec = spec.and(hasPostId(postId));
        if (userId != null) spec = spec.and(hasUserId(userId));
        if (name != null) spec = spec.and(hasName(name));
        return spec;
    }

    private Specification<Tag> hasPostId(Long postId) {
        return (root, query, cb)
                -> cb.equal(root.join("posts").get("id"), postId);
    }

    private Specification<Tag> hasUserId(Long userId) {
        return (root, query, cb)
                -> cb.equal(root.join("users").get("id"), userId);
    }

    private Specification<Tag> hasName(String name) {
        if (name.isBlank()) return (root, query, cb) -> cb.isNull(root.get("name"));
        return (root, query, cb)
                -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }
}