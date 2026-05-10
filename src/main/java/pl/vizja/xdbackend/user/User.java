package pl.vizja.xdbackend.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import pl.vizja.xdbackend.post.Post;
import pl.vizja.xdbackend.comment.Comment;
import pl.vizja.xdbackend.tag.Tag;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    //https://uibakery.io/regex-library/phone-number
    @Pattern(regexp = "^\\+?[1-9][0-9]{7,14}$")
    @Column(nullable = false, unique = true)
    private String phone;

    @Size(min = 1, max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 60)
    private String password;

    @Size(max = 2000)
    @Column(length = 2000, name = "profile_pic_url")
    private String profilePicUrl;

    @Size(max = 2000)
    @Column(length = 2000, name = "background_pic_url")
    private String backgroundPicUrl;

    @Size(min = 1, max = 500)
    @Column(length = 500)
    private String description;

    @Column(nullable = false, name = "joined_at")
    private LocalDateTime joinedAt;

    @PrePersist
    private void beforeCreation() {
        //admin can only change the role to "ADMIN"
        this.role = UserRole.USER;
        this.joinedAt = LocalDateTime.now();
    }

    //---additional---//
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "user_tags",
            joinColumns = @JoinColumn(name = "user_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "tag_id", nullable = false)
    )
    private Set<Tag> tags;

    //users I follow
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "followed_users",
            joinColumns = @JoinColumn(name = "following_user_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "followed_user_id", nullable = false)
    )
    private Set<User> following;

    //users that follow me
    @ManyToMany(mappedBy = "following", fetch = FetchType.LAZY)
    private Set<User> followed;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private Set<Post> posts;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private Set<Comment> comments;

    @ManyToMany(mappedBy = "likes", fetch = FetchType.LAZY)
    private Set<Post> likedPosts;
}
