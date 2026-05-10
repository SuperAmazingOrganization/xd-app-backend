package pl.vizja.xdbackend.comment;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import pl.vizja.xdbackend.comment.dto.CommentDTO;
import pl.vizja.xdbackend.comment.dto.CreateCommentDTO;
import pl.vizja.xdbackend.comment.dto.UpdateCommentDTO;
import pl.vizja.xdbackend.security.AuthorizationChecker;
import pl.vizja.xdbackend.user.User;
import pl.vizja.xdbackend.user.UserRepository;
import pl.vizja.xdbackend.validation.CustomConstraintValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CommentSpecification commentSpecification;
    private final AuthorizationChecker authorizationChecker;
    private final CustomConstraintValidator constraintValidator;

    CommentDTO getComment(
            Long commentId
    ) {

        if(commentRepository.existsById(commentId)) {

            throw new EntityNotFoundException("Comment (id=" + commentId + ") was not found!");
        }

        Comment comment = commentRepository.findCommentById(commentId);
        Comment parent = comment.getParent();
        Long parentId;
        if(parent == null) {
            parentId = null;
        } else {
            parentId = parent.getId();
        }

        return CommentDTO.builder()
                .id(Optional.empty())
                .authorId(Optional.of(comment.getAuthor().getId()))
                .postId(Optional.of(comment.getPost().getId()))
                .parentId(Optional.of(parentId))
                .body(comment.getBody())
                .addedAt(comment.getAddedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    CommentDTO updateComment(
            Long commentId,
            UpdateCommentDTO updateCommentDTO
    ) {

        if(commentRepository.existsById(commentId)) {

            throw new EntityNotFoundException("Comment (id=" + commentId + ") was not found!");
        }

        Comment comment = commentRepository.findCommentById(commentId);

        if(!authorizationChecker.isAdmin() &&
                !authorizationChecker.isOwner(comment.getAuthor().getId())) {

            throw new AccessDeniedException("User is not an admin or the owner!");
        }

        Comment parent = comment.getParent();
        Long parentId;
        if(parent == null) {
            parentId = null;
        } else {
            parentId = parent.getId();
        }

        String body = updateCommentDTO.body();
        if(body != null) comment.setBody(body);
        commentRepository.save(comment);

        return CommentDTO.builder()
                .id(Optional.empty())
                .authorId(Optional.of(comment.getAuthor().getId()))
                .postId(Optional.of(comment.getPost().getId()))
                .parentId(Optional.of(parentId))
                .body(comment.getBody())
                .addedAt(comment.getAddedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    void deleteComment(
            Long commentId
    ) {

        if(commentRepository.existsById(commentId)) {

            throw new EntityNotFoundException("Comment (id=" + commentId + ") was not found!");
        }

        if(!authorizationChecker.isAdmin()) {

            Comment comment = commentRepository.findCommentById(commentId);
            if(!authorizationChecker.isOwner(comment.getAuthor().getId())) {

                throw new AccessDeniedException("User is not an admin or the owner!");
            }
        }

        commentRepository.deleteById(commentId);
    }

    List<CommentDTO> getCommentReplies(
            Long commentId,
            String body,
            LocalDateTime addedAtFrom,
            LocalDateTime addedAtTo,
            LocalDateTime updatedAtFrom,
            LocalDateTime updatedAtTo,
            Pageable pageable
    ) {

        if(commentRepository.existsById(commentId)) {

            throw new EntityNotFoundException("Comment (id=" + commentId + ") was not found!");
        }

        Specification<Comment> commentSpec =
                commentSpecification.build(null, commentId, body, addedAtFrom,
                        addedAtTo, updatedAtFrom, updatedAtTo);

        return commentRepository.findAll(commentSpec, pageable)
                .stream().map(comment -> CommentDTO.builder()
                        .id(Optional.of(comment.getId()))
                        .authorId(Optional.of(comment.getAuthor().getId()))
                        .postId(Optional.of(comment.getPost().getId()))
                        .parentId(Optional.empty())
                        .body(comment.getBody())
                        .addedAt(comment.getAddedAt())
                        .updatedAt(comment.getUpdatedAt())
                        .build()).toList();
    }

    CommentDTO createCommentReply(
            Long commentId,
            CreateCommentDTO createCommentDTO
    ) {

        if(commentRepository.existsById(commentId)) {

            throw new EntityNotFoundException("Comment (id=" + commentId + ") was not found!");
        }

        constraintValidator.validate(createCommentDTO);

        User author = userRepository.findUserById(createCommentDTO.authorId());

        if(!authorizationChecker.isOwner(author.getId())) {

            throw new AccessDeniedException("User is not the owner!");
        }

        Comment parentComment = commentRepository.findCommentById(commentId);

        Comment comment = Comment.builder()
                .author(author)
                .post(parentComment.getPost())
                .parent(parentComment)
                .body(createCommentDTO.body())
                .build();

        commentRepository.save(comment);

        return CommentDTO.builder()
                .id(Optional.of(comment.getId()))
                .authorId(Optional.of(comment.getAuthor().getId()))
                .postId(Optional.of(comment.getPost().getId()))
                .parentId(Optional.empty())
                .body(comment.getBody())
                .addedAt(comment.getAddedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    Long countCommentReplies(
            Long commentId,
            String body,
            LocalDateTime addedAtFrom,
            LocalDateTime addedAtTo,
            LocalDateTime updatedAtFrom,
            LocalDateTime updatedAtTo
    ) {

        if(commentRepository.existsById(commentId)) {

            throw new EntityNotFoundException("Comment (id=" + commentId + ") was not found!");
        }

        Specification<Comment> commentSpec =
                commentSpecification.build(null, commentId, body, addedAtFrom,
                        addedAtTo, updatedAtFrom, updatedAtTo);

        return commentRepository.count(commentSpec);
    }
}
