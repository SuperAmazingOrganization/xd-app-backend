package pl.vizja.xdbackend.post;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import pl.vizja.xdbackend.comment.Comment;
import pl.vizja.xdbackend.comment.CommentRepository;
import pl.vizja.xdbackend.comment.CommentSpecification;
import pl.vizja.xdbackend.comment.dto.CommentDTO;
import pl.vizja.xdbackend.comment.dto.CreateCommentDTO;
import pl.vizja.xdbackend.post.dto.CreatePostDTO;
import pl.vizja.xdbackend.post.dto.PostDTO;
import pl.vizja.xdbackend.post.dto.UpdatePostDTO;
import pl.vizja.xdbackend.security.AuthorizationChecker;
import pl.vizja.xdbackend.shared.dto.CreateTagAssociationDTO;
import pl.vizja.xdbackend.shared.dto.CreateUserAssociationDTO;
import pl.vizja.xdbackend.tag.Tag;
import pl.vizja.xdbackend.tag.TagRepository;
import pl.vizja.xdbackend.tag.TagSpecification;
import pl.vizja.xdbackend.tag.dto.TagDTO;
import pl.vizja.xdbackend.user.User;
import pl.vizja.xdbackend.user.UserRepository;
import pl.vizja.xdbackend.user.UserRole;
import pl.vizja.xdbackend.user.UserSpecification;
import pl.vizja.xdbackend.user.dto.UserDTO;
import pl.vizja.xdbackend.validation.CustomConstraintValidator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final CommentRepository commentRepository;
    private final PostSpecification postSpecification;
    private final UserSpecification userSpecification;
    private final TagSpecification tagSpecification;
    private final CommentSpecification commentSpecification;
    private final CustomConstraintValidator constraintValidator;
    private final AuthorizationChecker authorizationChecker;

    List<PostDTO> getPosts(
            String body,
            LocalDateTime addedAtFrom,
            LocalDateTime addedAtTo,
            LocalDateTime updatedAtFrom,
            LocalDateTime updatedAtTo,
            List<Long> tagIds,
            Pageable pageable
    ) {

        Specification<Post> postSpec =
                postSpecification.build(
                        body,
                        addedAtFrom,
                        addedAtTo,
                        updatedAtFrom,
                        updatedAtTo,
                        tagIds
                );

        return postRepository.findAll(postSpec, pageable)
                .stream().map(post -> PostDTO.builder()
                        .id(Optional.of(post.getId()))
                        .authorId(Optional.of(post.getAuthor().getId()))
                        .body(post.getBody())
                        .addedAt(post.getAddedAt())
                        .updatedAt(post.getUpdatedAt())
                        .build()).toList();
    }

    PostDTO createPost(CreatePostDTO createPostDTO) {

        constraintValidator.validate(createPostDTO);

        if(!authorizationChecker.isOwner(createPostDTO.authorId())) {

            throw new AccessDeniedException("User is not the owner!");
        }

        User author = userRepository.findUserById(createPostDTO.authorId());

        Post post = Post.builder()
                .author(author)
                .body(createPostDTO.body())
                .build();
        postRepository.save(post);

        return PostDTO.builder()
                .id(Optional.of(post.getId()))
                .authorId(Optional.of(author.getId()))
                .body(post.getBody())
                .addedAt(post.getAddedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    Long countPosts(
            String body,
            LocalDateTime addedAtFrom,
            LocalDateTime addedAtTo,
            LocalDateTime updatedAtFrom,
            LocalDateTime updatedAtTo,
            List<Long> tagIds
    ) {

        Specification<Post> postSpec =
                postSpecification.build(
                        body,
                        addedAtFrom,
                        addedAtTo,
                        updatedAtFrom,
                        updatedAtTo,
                        tagIds
                );

        return postRepository.count(postSpec);
    }

    PostDTO getPost(
            Long postId
    ) {

        if(!postRepository.existsById(postId)) {

            throw new EntityNotFoundException("Post (id=" + postId + ") was not found!");
        }

        Post post = postRepository.findPostById(postId);

        return PostDTO.builder()
                .id(Optional.empty())
                .authorId(Optional.of(post.getAuthor().getId()))
                .body(post.getBody())
                .addedAt(post.getAddedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    PostDTO updatePost(
            Long postId,
            UpdatePostDTO updatePostDTO
    ) {

        if(!postRepository.existsById(postId)) {

            throw new EntityNotFoundException("Post (id=" + postId + ") was not found!");
        }

        constraintValidator.validate(updatePostDTO);

        Post post = postRepository.findPostById(postId);

        if(!authorizationChecker.isAdmin() &&
                !authorizationChecker.isOwner(post.getAuthor().getId())) {

            throw new AccessDeniedException("User is not an admin or the owner!");
        }

        String body = updatePostDTO.body();
        if(body != null) post.setBody(body);
        post.setUpdatedAt(LocalDateTime.now());

        postRepository.save(post);

        return PostDTO.builder()
                .id(Optional.empty())
                .authorId(Optional.of(post.getAuthor().getId()))
                .body(post.getBody())
                .addedAt(post.getAddedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    void deletePost(
            Long postId
    ) {

        if(!postRepository.existsById(postId)) {

            throw new EntityNotFoundException("Post (id=" + postId + ") was not found!");
        }

        if(!authorizationChecker.isAdmin()) {

            Post post = postRepository.findPostById(postId);
            if(!authorizationChecker.isOwner(post.getAuthor().getId())) {

                throw new AccessDeniedException("User is not an admin or the owner!");
            }
        }

        postRepository.deleteById(postId);
    }

    List<TagDTO> getPostTags(
            Long postId,
            String name,
            Pageable pageable
    ) {

        if(!postRepository.existsById(postId)) {

            throw new EntityNotFoundException("Post (id=" + postId + ") was not found!");
        }

        Specification<Tag> tagSpec =
                tagSpecification.build(null, postId, name);

        return tagRepository.findAll(tagSpec, pageable)
                .stream().map(tag -> TagDTO.builder()
                        .id(Optional.of(tag.getId()))
                        .name(tag.getName())
                        .build()).toList();
    }

    TagDTO createPostTag(
            Long postId,
            CreateTagAssociationDTO createTagAssociationDTO
    ) {

        if(!postRepository.existsById(postId)) {

            throw new EntityNotFoundException("Post (id=" + postId + ") was not found!");
        }

        constraintValidator.validate(createTagAssociationDTO);

        Post post = postRepository.findPostById(postId);

        if(!authorizationChecker.isOwner(post.getAuthor().getId())) {

            throw new AccessDeniedException("User is not an admin or the owner!");
        }

        Tag tag = tagRepository.findTagById(createTagAssociationDTO.tagId());

        post.getTags().add(tag);

        postRepository.save(post);

        return TagDTO.builder()
                .id(Optional.empty())
                .name(tag.getName())
                .build();
    }

    Long countPostTags(
            Long postId,
            String name
    ) {

        if(!postRepository.existsById(postId)) {

            throw new EntityNotFoundException("Post (id=" + postId + ") was not found!");
        }

        Specification<Tag> tagSpec =
                tagSpecification.build(null, postId, name);

        return tagRepository.count(tagSpec);
    }

    void deletePostTag(
            Long postId,
            Long tagId
    ) {

        if(!postRepository.existsById(postId)) {

            throw new EntityNotFoundException("Post (id=" + postId + ") was not found!");
        }

        if(!tagRepository.existsById(tagId)) {

            throw new EntityNotFoundException("Tag (id=" + tagId + ") was not found!");
        }

        Post post = postRepository.findPostById(postId);

        if (!authorizationChecker.isOwner(post.getAuthor().getId())) {

            throw new AccessDeniedException("User is not the owner!");
        }

        post.getTags().removeIf(tag -> tag.getId().equals(tagId));

        postRepository.save(post);
    }

    List<CommentDTO> getPostComments(
            Long postId,
            String body,
            LocalDateTime addedAtFrom,
            LocalDateTime addedAtTo,
            LocalDateTime updatedAtFrom,
            LocalDateTime updatedAtTo,
            Pageable pageable
    ) {

        if(!postRepository.existsById(postId)) {

            throw new EntityNotFoundException("Post (id=" + postId + ") was not found!");
        }

        Specification<Comment> commentSpec =
                commentSpecification.build(postId, null, body, addedAtFrom,
                        addedAtTo, updatedAtFrom, updatedAtTo);

        return commentRepository.findAll(commentSpec, pageable)
                .stream().map(comment -> CommentDTO.builder()
                        .id(Optional.of(comment.getId()))
                        .authorId(Optional.of(comment.getAuthor().getId()))
                        .postId(Optional.empty())
                        .parentId(Optional.of(comment.getParent().getId()))
                        .body(comment.getBody())
                        .addedAt(comment.getAddedAt())
                        .updatedAt(comment.getUpdatedAt())
                        .build()).toList();
    }

    CommentDTO createPostComment(
            Long postId,
            CreateCommentDTO createCommentDTO
    ) {

        if(!postRepository.existsById(postId)) {

            throw new EntityNotFoundException("Post (id=" + postId + ") was not found!");
        }

        constraintValidator.validate(createCommentDTO);

        User author = userRepository.findUserById(createCommentDTO.authorId());

        if(!authorizationChecker.isOwner(author.getId())) {

            throw new AccessDeniedException("User is not the owner!");
        }

        Post post = postRepository.findPostById(postId);

        Comment comment = Comment.builder()
                .author(author)
                .post(post)
                .body(createCommentDTO.body())
                .build();

        commentRepository.save(comment);

        return CommentDTO.builder()
                .id(Optional.of(comment.getId()))
                .authorId(Optional.of(comment.getAuthor().getId()))
                .postId(Optional.empty())
                .parentId(Optional.ofNullable(null))
                .body(comment.getBody())
                .addedAt(comment.getAddedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    Long countPostComments(
            Long postId,
            String body,
            LocalDateTime addedAtFrom,
            LocalDateTime addedAtTo,
            LocalDateTime updatedAtFrom,
            LocalDateTime updatedAtTo
    ) {

        if(!postRepository.existsById(postId)) {

            throw new EntityNotFoundException("Post (id=" + postId + ") was not found!");
        }

        Specification<Comment> commentSpec =
                commentSpecification.build(postId, null, body, addedAtFrom,
                        addedAtTo, updatedAtFrom, updatedAtTo);

        return commentRepository.count(commentSpec);
    }

    List<UserDTO> getPostLikes(
            Long postId,
            UserRole userRole,
            String email,
            String phone,
            String username,
            String description,
            LocalDateTime joinedAtFrom,
            LocalDateTime joinedAtTo,
            List<Long> tagIds,
            Pageable pageable) {

        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException("Post (id=" + postId + ") was not found!");
        }

        boolean isAdmin = authorizationChecker.isAdmin();

        Specification<User> spec = userSpecification.build(
                postId, null, userRole, email, phone, username,
                description, joinedAtFrom, joinedAtTo, tagIds);

        return userRepository.findAll(spec, pageable).stream()
                .map(user -> UserDTO.builder()
                        .id(Optional.of(user.getId()))
                        .role(isAdmin ? Optional.of(user.getRole()) : Optional.empty())
                        .email(isAdmin ? Optional.of(user.getEmail()) : Optional.empty())
                        .phone(isAdmin ? Optional.of(user.getPhone()) : Optional.empty())
                        .username(user.getUsername())
                        .description(user.getDescription())
                        .profilePicUrl(user.getProfilePicUrl())
                        .backgroundPicUrl(user.getBackgroundPicUrl())
                        .joinedAt(user.getJoinedAt())
                        .build()).toList();
    }

    UserDTO createPostLike(
            Long postId,
            CreateUserAssociationDTO
            createUserAssociationDTO
    ) {

        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException("Post (id=" + postId + ") was not found!");
        }

        if (!authorizationChecker.isOwner(createUserAssociationDTO.userId())) {
            throw new AccessDeniedException("User is not the owner!");
        }

        constraintValidator.validate(createUserAssociationDTO);

        Post post = postRepository.findPostById(postId);
        User user = userRepository.findUserById(createUserAssociationDTO.userId());

        post.getLikes().add(user);
        postRepository.save(post);

        boolean isAdmin = authorizationChecker.isAdmin();

        return UserDTO.builder()
                .id(Optional.of(user.getId()))
                .role(isAdmin ? Optional.of(user.getRole()) : Optional.empty())
                .email(isAdmin ? Optional.of(user.getEmail()) : Optional.empty())
                .phone(isAdmin ? Optional.of(user.getPhone()) : Optional.empty())
                .username(user.getUsername())
                .description(user.getDescription())
                .profilePicUrl(user.getProfilePicUrl())
                .backgroundPicUrl(user.getBackgroundPicUrl())
                .joinedAt(user.getJoinedAt())
                .build();
    }

    Long countPostLikes(
            Long postId,
            UserRole userRole,
            String email,
            String phone,
            String username,
            String description,
            LocalDateTime joinedAtFrom,
            LocalDateTime joinedAtTo,
            List<Long> tagIds
    ) {

        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException("Post (id=" + postId + ") was not found!");
        }

        Specification<User> spec = userSpecification.build(
                postId, null, userRole, email, phone, username,
                description, joinedAtFrom, joinedAtTo, tagIds);

        return userRepository.count(spec);
    }

    void deletePostLike(
            Long postId,
            Long likedId
    ) {

        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException("Post (id=" + postId + ") was not found!");
        }

        if (!userRepository.existsById(likedId)) {
            throw new EntityNotFoundException("User (id=" + likedId + ") was not found!");
        }

        if (!authorizationChecker.isOwner(likedId)) {
            throw new AccessDeniedException("User is not the owner!");
        }

        Post post = postRepository.findPostById(postId);
        post.getLikes().removeIf(user -> user.getId().equals(likedId));

        postRepository.save(post);
    }
}
