package pl.vizja.xdbackend.comment;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import pl.vizja.xdbackend.post.Post;
import pl.vizja.xdbackend.user.User;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "post_comments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @Size(min = 1, max = 500)
    @Column(nullable = false, length = 500)
    private String body;

    @Column(nullable = false, name = "added_at")
    private LocalDateTime addedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    private void beforeCreation() {
        this.addedAt = LocalDateTime.now();
    }

    //additional
    @OneToMany(mappedBy = "parent")
    private Set<Comment> replies;
}
