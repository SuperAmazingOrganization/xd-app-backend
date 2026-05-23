package pl.vizja.xdbackend.comment;

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
import pl.vizja.xdbackend.comment.dto.UpdateCommentDTO;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Tag(name = "Comments")
class CommentController {

    private final CommentService commentService;

    @Operation(summary = "Get a comment")
    @GetMapping("/{commentId}")
    ResponseEntity<CommentDTO> getComment(
            @PathVariable Long commentId
    ) {

        return ResponseEntity.ok(commentService.getComment(commentId));
    }

    @Operation(summary = "Update a comment [OWNER / ADMIN]")
    @PatchMapping("/{commentId}")
    ResponseEntity<CommentDTO> updateComment(
            @PathVariable Long commentId,
            @RequestBody UpdateCommentDTO updateCommentDTO
            ) {

        return ResponseEntity.ok(commentService
                .updateComment(commentId, updateCommentDTO));
    }

    @Operation(summary = "Delete a comment [OWNER/ADMIN]")
    @DeleteMapping("/{commentId}")
    ResponseEntity<CommentDTO> deleteComment(
            @PathVariable Long commentId
    ) {

        commentService.deleteComment(commentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Get comment replies")
    @GetMapping("/{commentId}/replies")
    ResponseEntity<List<CommentDTO>> getCommentReplies(
            @PathVariable Long commentId,
            @RequestParam @Nullable String body,
            @RequestParam @Nullable LocalDateTime addedAtFrom,
            @RequestParam @Nullable LocalDateTime addedAtTo,
            @RequestParam @Nullable LocalDateTime updatedAtFrom,
            @RequestParam @Nullable LocalDateTime updatedAtTo,
            @Nullable Pageable pageable
    ) {

        return ResponseEntity.ok(commentService.getCommentReplies(
                commentId, body, addedAtFrom, addedAtTo,
                updatedAtFrom, updatedAtTo, pageable));
    }

    @Operation(summary = "Create a comment reply [OWNER]")
    @PostMapping("/{commentId}/replies")
    ResponseEntity<CommentDTO> getComment(
            @PathVariable Long commentId,
            @RequestBody CreateCommentDTO createCommentDTO
            ) {

        return ResponseEntity.ok(commentService.
                createCommentReply(commentId, createCommentDTO));
    }

    @Operation(summary = "Count comment replies")
    @GetMapping("/{commentId}/replies/count")
    ResponseEntity<Long> getComment(
            @PathVariable Long commentId,
            @RequestParam @Nullable String body,
            @RequestParam @Nullable LocalDateTime addedAtFrom,
            @RequestParam @Nullable LocalDateTime addedAtTo,
            @RequestParam @Nullable LocalDateTime updatedAtFrom,
            @RequestParam @Nullable LocalDateTime updatedAtTo
    ) {

        return ResponseEntity.ok(commentService.countCommentReplies(
                commentId, body, addedAtFrom, addedAtTo,
                updatedAtFrom, updatedAtTo));
    }
}
