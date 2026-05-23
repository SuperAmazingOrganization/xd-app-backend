package pl.vizja.xdbackend.tag;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import pl.vizja.xdbackend.post.Post;
import pl.vizja.xdbackend.user.User;

import java.util.Set;

@Entity
@Table(name = "tags")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 1, max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    //additional
    @ManyToMany(mappedBy = "tags")
    private Set<Post> posts;

    @ManyToMany(mappedBy = "tags")
    private Set<User> users;
}
