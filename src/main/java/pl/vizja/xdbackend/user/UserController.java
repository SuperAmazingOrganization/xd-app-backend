package pl.vizja.xdbackend.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.vizja.xdbackend.shared.dto.CreateTagAssociationDTO;
import pl.vizja.xdbackend.tag.dto.TagDTO;
import pl.vizja.xdbackend.shared.dto.CreateUserAssociationDTO;
import pl.vizja.xdbackend.user.dto.CreateUserDTO;
import pl.vizja.xdbackend.user.dto.UpdateUserDTO;
import pl.vizja.xdbackend.user.dto.UserDTO;
import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Users")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
class UserController {

    private final UserService userService;

    @Operation(summary = "Get all users")
    @GetMapping
    ResponseEntity<List<UserDTO>> getUsers(
            @RequestParam @Nullable UserRole userRole,
            @RequestParam @Nullable String email,
            @RequestParam @Nullable String phone,
            @RequestParam @Nullable String username,
            @RequestParam @Nullable String description,
            @RequestParam @Nullable LocalDateTime joinedAtFrom,
            @RequestParam @Nullable LocalDateTime joinedAtTo,
            @RequestParam @Nullable List<Long> tagIds,
            @Nullable Pageable pageable
    ) {

        return ResponseEntity.ok(userService.getUsers(userRole, email, phone,
                username, description, joinedAtFrom, joinedAtTo, tagIds, pageable));
    }

    @Operation(summary = "Create a user")
    @PostMapping
    ResponseEntity<UserDTO> createUser(
            @RequestBody CreateUserDTO createUserDTO
    ) {

        return ResponseEntity.ok(userService.createUser(createUserDTO));
    }

    @Operation(summary = "Count users")
    @GetMapping("/count")
    ResponseEntity<Long> countUsers(
            @RequestParam @Nullable UserRole userRole,
            @RequestParam @Nullable String email,
            @RequestParam @Nullable String phone,
            @RequestParam @Nullable String username,
            @RequestParam @Nullable String description,
            @RequestParam @Nullable LocalDateTime joinedAtFrom,
            @RequestParam @Nullable LocalDateTime joinedAtTo,
            @RequestParam @Nullable List<Long> tagIds
    ) {

        return ResponseEntity.ok(userService.countUsers(userRole, email, phone, username,
                description, joinedAtFrom, joinedAtTo, tagIds));
    }

    @Operation(summary = "Get a user")
    @GetMapping("/{userId}")
    ResponseEntity<UserDTO> getUser(
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(userService.getUser(userId));
    }

    @Operation(summary = "Update a user")
    @PatchMapping("/{userId}")
    ResponseEntity<UserDTO> updateUser(
            @PathVariable Long userId,
            @RequestBody UpdateUserDTO updateUserDTO
    ) {

        return ResponseEntity.ok(userService.updateUser(userId, updateUserDTO));
    }

    @Operation(summary = "Delete a user")
    @DeleteMapping("/{userId}")
    ResponseEntity<Void> deleteUser(
            @PathVariable Long userId
    ) {

        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Get user tags")
    @GetMapping("/{userId}/tags")
    ResponseEntity<List<TagDTO>> getUserTags(
            @PathVariable Long userId,
            @RequestParam @Nullable String name,
            @Nullable Pageable pageable
    ) {

        return ResponseEntity.ok(userService.getUserTags(userId, name, pageable));
    }

    @Operation(summary = "Create a user tag")
    @PostMapping("/{userId}/tags")
    ResponseEntity<TagDTO> createUserTag(
            @PathVariable Long userId,
            @RequestBody CreateTagAssociationDTO createTagAssociationDTO
    ) {

        return ResponseEntity.ok(userService.createUserTag(userId, createTagAssociationDTO));
    }

    @Operation(summary = "Count user tags")
    @GetMapping("/{userId}/tags/count")
    ResponseEntity<Long> countUserTags(
            @PathVariable Long userId,
            @RequestParam @Nullable String name
    ) {

        return ResponseEntity.ok(userService.countUserTags(userId, name));
    }

    @Operation(summary = "Delete a user tag")
    @DeleteMapping("/{userId}/tags/{tagId}")
    ResponseEntity<Void> deleteUserTag(
            @PathVariable Long userId,
            @PathVariable Long tagId
    ) {

        userService.deleteUserTag(userId, tagId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Get users the user follows")
    @GetMapping("/{userId}/following")
    ResponseEntity<List<UserDTO>> getUserFollowing(
            @PathVariable Long userId,
            @RequestParam @Nullable UserRole role,
            @RequestParam @Nullable String email,
            @RequestParam @Nullable String phone,
            @RequestParam @Nullable String username,
            @RequestParam @Nullable String description,
            @RequestParam @Nullable LocalDateTime joinedAtFrom,
            @RequestParam @Nullable LocalDateTime joinedAtTo,
            @RequestParam @Nullable List<Long> tagIds,
            Pageable pageable
    ) {

        return ResponseEntity.ok(userService.getUserFollowing(userId, role, email, phone,
                username, description, joinedAtFrom, joinedAtTo, tagIds, pageable));
    }

    @Operation(summary = "Follow a user")
    @PostMapping("/{userId}/following")
    ResponseEntity<UserDTO> createUserFollowing(
            @PathVariable Long userId,
            @RequestBody CreateUserAssociationDTO
                    createUserAssociationDTO
    ) {

        return ResponseEntity.ok(userService.createUserFollowing(userId,
                createUserAssociationDTO));
    }

    @Operation(summary = "Count users the user follows")
    @GetMapping("/{userId}/following/count")
    ResponseEntity<Long> countUserFollowing(
            @PathVariable Long userId,
            @RequestParam @Nullable UserRole role,
            @RequestParam @Nullable String email,
            @RequestParam @Nullable String phone,
            @RequestParam @Nullable String username,
            @RequestParam @Nullable String description,
            @RequestParam @Nullable LocalDateTime joinedAtFrom,
            @RequestParam @Nullable LocalDateTime joinedAtTo,
            @RequestParam @Nullable List<Long> tagIds
    ) {

        return ResponseEntity.ok(userService.countUserFollowing(userId, role, email, phone,
                username, description, joinedAtFrom, joinedAtTo, tagIds));
    }

    @Operation(summary = "Unfollow a user")
    @DeleteMapping("/{userId}/following/{followingUserId}")
    ResponseEntity<Void> deleteUserFollowing(
            @PathVariable Long userId,
            @PathVariable Long followingUserId
    ) {

        userService.deleteUserFollowing(userId, followingUserId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}