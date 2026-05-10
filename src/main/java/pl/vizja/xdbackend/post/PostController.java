package pl.vizja.xdbackend.post;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.vizja.xdbackend.comment.dto.CommentDTO;
import pl.vizja.xdbackend.comment.dto.CreateCommentDTO;
import pl.vizja.xdbackend.post.dto.CreatePostDTO;
import pl.vizja.xdbackend.post.dto.PostDTO;
import pl.vizja.xdbackend.post.dto.UpdatePostDTO;
import pl.vizja.xdbackend.shared.dto.CreateTagAssociationDTO;
import pl.vizja.xdbackend.shared.dto.CreateUserAssociationDTO;
import pl.vizja.xdbackend.tag.dto.TagDTO;
import pl.vizja.xdbackend.user.UserRole;
import pl.vizja.xdbackend.user.dto.UserDTO;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Tag(name = "Posts")
class PostController {

    private final PostService postService;

    @Operation(summary = "Get posts")
    @GetMapping
    ResponseEntity<List<PostDTO>> getPosts(
            @RequestParam @Nullable String body,
            @RequestParam @Nullable LocalDateTime addedAtFrom,
            @RequestParam @Nullable LocalDateTime addedAtTo,
            @RequestParam @Nullable LocalDateTime updatedAtFrom,
            @RequestParam @Nullable LocalDateTime updatedAtTo,
            @RequestParam @Nullable List<Long> tagIds,
            @Nullable Pageable pageable
    ) {

        return ResponseEntity.ok(postService.getPosts(
                body,
                addedAtFrom,
                addedAtTo,
                updatedAtFrom,
                updatedAtTo,
                tagIds,
                pageable
        ));
    }

    @Operation(summary = "Create a post [OWNER]")
    @PostMapping
    ResponseEntity<PostDTO> createPost(
            @RequestBody CreatePostDTO createPostDTO
            ) {

        return ResponseEntity.ok(postService.createPost(createPostDTO));
    }

    @Operation(summary = "Count posts")
    @GetMapping("/count")
    ResponseEntity<Long> countPosts(
            @RequestParam @Nullable String body,
            @RequestParam @Nullable LocalDateTime addedAtFrom,
            @RequestParam @Nullable LocalDateTime addedAtTo,
            @RequestParam @Nullable LocalDateTime updatedAtFrom,
            @RequestParam @Nullable LocalDateTime updatedAtTo,
            @RequestParam @Nullable List<Long> tagIds
    ) {

        return ResponseEntity.ok(postService.countPosts(
                body,
                addedAtFrom,
                addedAtTo,
                updatedAtFrom,
                updatedAtTo,
                tagIds
        ));
    }

    @Operation(summary = "Get a post")
    @GetMapping("/{postId}")
    ResponseEntity<PostDTO> getPost(
            @PathVariable Long postId
    ) {

        return ResponseEntity.ok(postService.getPost(postId));
    }

    @Operation(summary = "Update a post [OWNER / ADMIN]")
    @PatchMapping("/{postId}")
    ResponseEntity<PostDTO> updatePost(
            @PathVariable Long postId,
            @RequestBody UpdatePostDTO updatePostDTO
            ) {

        return ResponseEntity.ok(postService.updatePost(postId, updatePostDTO));
    }

    @Operation(summary = "Delete a post [OWNER / ADMIN]")
    @DeleteMapping("/{postId}")
    ResponseEntity<Void> deletePost(
            @PathVariable Long postId
    ) {

        postService.deletePost(postId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Get post tags")
    @GetMapping("/{postId}/tags")
    ResponseEntity<List<TagDTO>> getPostTags(
            @PathVariable Long postId,
            @RequestParam @Nullable String name,
            @Nullable Pageable pageable
    ) {

        return ResponseEntity.ok(postService.getPostTags(
                postId, name, pageable
        ));
    }

    @Operation(summary = "Add a post tag [OWNER]")
    @PostMapping("/{postId}/tags")
    ResponseEntity<TagDTO> createPostTag(
            @PathVariable Long postId,
            @RequestBody CreateTagAssociationDTO
                    createTagAssociationDTO
            ) {

        return ResponseEntity.ok(postService.createPostTag(
                postId, createTagAssociationDTO
        ));
    }

    @Operation(summary = "Count post tags")
    @GetMapping("/{postId}/tags/count")
    ResponseEntity<Long> countPostTags(
            @PathVariable Long postId,
            @RequestParam @Nullable String name
    ) {

        return ResponseEntity.ok(postService.countPostTags(
                postId, name
        ));
    }

    @Operation(summary = "Delete a post tag [OWNER]")
    @DeleteMapping("/{postId}/tags/{tagId}")
    ResponseEntity<Void> deletePostTag(
            @PathVariable Long postId,
            @PathVariable Long tagId
    ) {

        postService.deletePostTag(postId, tagId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Get post comments")
    @GetMapping("/{postId}/comments")
    ResponseEntity<List<CommentDTO>> getPostComments(
            @PathVariable Long postId,
            @RequestParam @Nullable String body,
            @RequestParam @Nullable LocalDateTime addedAtFrom,
            @RequestParam @Nullable LocalDateTime addedAtTo,
            @RequestParam @Nullable LocalDateTime updatedAtFrom,
            @RequestParam @Nullable LocalDateTime updatedAtTo,
            @Nullable Pageable pageable
    ) {

        return ResponseEntity.ok(postService.getPostComments(
                postId, body, addedAtFrom, addedAtTo,
                updatedAtFrom, updatedAtTo,
                pageable
        ));
    }

    @Operation(summary = "Add a post comment [OWNER]")
    @PostMapping("/{postId}/comments")
    ResponseEntity<CommentDTO> getPostComments(
            @PathVariable Long postId,
            @RequestBody CreateCommentDTO createCommentDTO
    ) {

        return ResponseEntity.ok(postService.createPostComment(
                postId, createCommentDTO
        ));
    }

    @Operation(summary = "Count post comments")
    @GetMapping("/{postId}/comments/count")
    ResponseEntity<Long> getPostComments(
            @PathVariable Long postId,
            @RequestParam @Nullable String body,
            @RequestParam @Nullable LocalDateTime addedAtFrom,
            @RequestParam @Nullable LocalDateTime addedAtTo,
            @RequestParam @Nullable LocalDateTime updatedAtFrom,
            @RequestParam @Nullable LocalDateTime updatedAtTo
    ) {

        return ResponseEntity.ok(postService.countPostComments(
                postId,
                body,
                addedAtFrom,
                addedAtTo,
                updatedAtFrom,
                updatedAtTo
        ));
    }

    @Operation(summary = "Get post likes")
    @GetMapping("/{postId}/likes")
    ResponseEntity<List<UserDTO>> getPostLikes(
            @PathVariable Long postId,
            @RequestParam @Nullable UserRole userRole,
            @RequestParam @Nullable String email,
            @RequestParam @Nullable String phone,
            @RequestParam @Nullable String username,
            @RequestParam @Nullable String description,
            @RequestParam @Nullable LocalDateTime joinedAtFrom,
            @RequestParam @Nullable LocalDateTime joinedAtTo,
            @RequestParam @Nullable List<Long> tagIds,
            Pageable pageable
    ) {

        return ResponseEntity.ok(postService.getPostLikes(postId, userRole, email, phone, username,
                description, joinedAtFrom, joinedAtTo, tagIds, pageable));
    }

    @Operation(summary = "Like a post")
    @PostMapping("/{postId}/likes")
    ResponseEntity<UserDTO> createPostLike(
            @PathVariable Long postId,
            @RequestBody CreateUserAssociationDTO createUserAssociationDTO
    ) {

        return ResponseEntity.ok(postService.createPostLike(postId, createUserAssociationDTO));
    }

    @Operation(summary = "Count post likes")
    @GetMapping("/{postId}/likes/count")
    ResponseEntity<Long> countPostLikes(
            @PathVariable Long postId,
            @RequestParam @Nullable UserRole userRole,
            @RequestParam @Nullable String email,
            @RequestParam @Nullable String phone,
            @RequestParam @Nullable String username,
            @RequestParam @Nullable String description,
            @RequestParam @Nullable LocalDateTime joinedAtFrom,
            @RequestParam @Nullable LocalDateTime joinedAtTo,
            @RequestParam @Nullable List<Long> tagIds
    ) {
        return ResponseEntity.ok(postService.countPostLikes(postId, userRole, email, phone,
                username, description, joinedAtFrom, joinedAtTo, tagIds));
    }

    @Operation(summary = "Unlike a post")
    @DeleteMapping("/{postId}/likes/{likedId}")
    ResponseEntity<Void> deletePostLike(
            @PathVariable Long postId,
            @PathVariable Long likedId
    ) {

        postService.deletePostLike(postId, likedId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
