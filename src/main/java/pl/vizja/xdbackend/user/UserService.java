package pl.vizja.xdbackend.user;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.vizja.xdbackend.security.AuthorizationChecker;
import pl.vizja.xdbackend.shared.dto.CreateTagAssociationDTO;
import pl.vizja.xdbackend.tag.Tag;
import pl.vizja.xdbackend.tag.TagRepository;
import pl.vizja.xdbackend.tag.TagSpecification;
import pl.vizja.xdbackend.tag.dto.TagDTO;
import pl.vizja.xdbackend.shared.dto.CreateUserAssociationDTO;
import pl.vizja.xdbackend.user.dto.CreateUserDTO;
import pl.vizja.xdbackend.user.dto.UpdateUserDTO;
import pl.vizja.xdbackend.user.dto.UserDTO;
import pl.vizja.xdbackend.validation.CustomConstraintValidator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final UserSpecification userSpecification;
    private final TagSpecification tagSpecification;
    private final AuthorizationChecker authorizationChecker;
    private final CustomConstraintValidator constraintValidator;
    private final PasswordEncoder passwordEncoder;

    List<UserDTO> getUsers(
            UserRole userRole,
            String email,
            String phone,
            String username,
            String description,
            LocalDateTime joinedAtFrom,
            LocalDateTime joinedAtTo,
            List<Long> tagIds,
            Pageable pageable
    ) {

        boolean isAdmin = authorizationChecker.isAdmin();

        if (userRole != null || email != null || phone != null) {
            if (!isAdmin) {
                throw new AccessDeniedException("User is not an admin!");
            }
        }

        Specification<User> spec = userSpecification.build(
                null, null, userRole, email, phone, username, description, joinedAtFrom, joinedAtTo, tagIds);

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

    UserDTO createUser(
            CreateUserDTO createUserDTO
    ) {

        constraintValidator.validate(createUserDTO);

        String phone = createUserDTO.phone();
        if (phone != null && phone.isBlank()) phone = null;

        User user = User.builder()
                .email(createUserDTO.email())
                .phone(phone)
                .username(createUserDTO.username())
                .password(passwordEncoder.encode(createUserDTO.password()))
                .build();

        userRepository.save(user);

        return UserDTO.builder()
                .id(Optional.of(user.getId()))
                .role(Optional.empty())
                .email(Optional.of(user.getEmail()))
                .phone(Optional.ofNullable(user.getPhone()))
                .username(user.getUsername())
                .profilePicUrl(user.getProfilePicUrl())
                .backgroundPicUrl(user.getBackgroundPicUrl())
                .description(user.getDescription())
                .joinedAt(user.getJoinedAt())
                .build();
    }

    Long countUsers(
            UserRole userRole,
            String email,
            String phone,
            String username,
            String description,
            LocalDateTime joinedAtFrom,
            LocalDateTime joinedAtTo,
            List<Long> tagIds
    ) {

        boolean isAdmin = authorizationChecker.isAdmin();

        if (userRole != null || email != null || phone != null) {
            if (!isAdmin) {
                throw new AccessDeniedException("User is not an admin!");
            }
        }

        Specification<User> spec = userSpecification.build(
                null, null, userRole, email, phone, username, description, joinedAtFrom, joinedAtTo, tagIds);

        return userRepository.count(spec);
    }

    public UserDTO getUser(
            Long userId
    ) {

        if(!userRepository.existsById(userId)) {

            throw new EntityNotFoundException("User (id=" + userId + ") was not found!");
        }

        User user = userRepository.findUserById(userId);

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

    UserDTO updateUser(
            Long userId,
            UpdateUserDTO updateUserDTO
    ) {

        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User (id=" + userId + ") was not found!");
        }

        boolean isAdmin = authorizationChecker.isAdmin();

        if (!isAdmin && !authorizationChecker.isOwner(userId)) {
            throw new AccessDeniedException("User is not an admin or the owner!");
        }

        constraintValidator.validate(updateUserDTO);

        User user = userRepository.findUserById(userId);

        if (updateUserDTO.role() != null) {
            if (!isAdmin) {
                throw new AccessDeniedException("User is not an admin!");
            }
            user.setRole(updateUserDTO.role());
        }

        if (updateUserDTO.email() != null) user.setEmail(updateUserDTO.email());
        if (updateUserDTO.phone() != null) user.setPhone(updateUserDTO.phone());
        if (updateUserDTO.username() != null) user.setUsername(updateUserDTO.username());
        if (updateUserDTO.description() != null) user.setDescription(updateUserDTO.description());
        if (updateUserDTO.profilePicUrl() != null) user.setProfilePicUrl(updateUserDTO.profilePicUrl());
        if (updateUserDTO.backgroundPicUrl() != null) user.setBackgroundPicUrl(updateUserDTO.backgroundPicUrl());
        if (updateUserDTO.newPassword() != null) user.setPassword(passwordEncoder.encode(updateUserDTO.newPassword()));

        userRepository.save(user);

        return UserDTO.builder()
                .id(Optional.empty())
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

    void deleteUser(
            Long userId
    ) {

        if(!userRepository.existsById(userId)) {

            throw new EntityNotFoundException("User (id=" + userId + ") was not found!");
        }

        if(!authorizationChecker.isAdmin() && !authorizationChecker.isOwner(userId)) {

            throw new AccessDeniedException("User is not an admin or the owner!");
        }

        userRepository.deleteById(userId);
    }

    List<TagDTO> getUserTags(
            Long userId,
            String name,
            Pageable pageable
    ) {

        if(!userRepository.existsById(userId)) {

            throw new EntityNotFoundException("User (id=" + userId + ") was not found!");
        }

        Specification<Tag> tagSpec =
                tagSpecification.build(userId, null, name);

        return tagRepository.findAll(tagSpec, pageable)
                .stream().map(tag -> TagDTO.builder()
                        .id(Optional.of(tag.getId()))
                        .name(tag.getName())
                        .build()).toList();
    }

    TagDTO createUserTag(
            Long userId,
            CreateTagAssociationDTO
                    createTagAssociationDTO
    ) {

        if(!userRepository.existsById(userId)) {

            throw new EntityNotFoundException("User (id=" + userId + ") was not found!");
        }

        constraintValidator.validate(createTagAssociationDTO);

        if(!authorizationChecker.isOwner(userId)) {
            throw new AccessDeniedException("User is not the owner!");
        }

        User user = userRepository.findUserById(userId);
        Tag tag = tagRepository.findTagById(createTagAssociationDTO.tagId());

        user.getTags().add(tag);
        userRepository.save(user);

        return TagDTO.builder()
                .id(Optional.of(tag.getId()))
                .name(tag.getName())
                .build();
    }

    Long countUserTags(
            Long userId,
            String name
    ) {

        if(!userRepository.existsById(userId)) {

            throw new EntityNotFoundException("User (id=" + userId + ") was not found!");
        }

        Specification<Tag> tagSpec =
                tagSpecification.build(userId, null, name);

        return tagRepository.count(tagSpec);
    }

    void deleteUserTag(
            Long userId,
            Long tagId
    ) {

        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User (id=" + userId + ") was not found!");
        }

        if (!tagRepository.existsById(tagId)) {
            throw new EntityNotFoundException("Tag (id=" + tagId + ") was not found!");
        }

        if (!authorizationChecker.isOwner(userId)) {
            throw new AccessDeniedException("User is not the owner!");
        }

        User user = userRepository.findUserById(userId);
        user.getTags().removeIf(tag -> tag.getId().equals(tagId));
        userRepository.save(user);
    }

    List<UserDTO> getUserFollowing(
            Long userId,
            UserRole userRole,
            String email,
            String phone,
            String username,
            String description,
            LocalDateTime joinedAtFrom,
            LocalDateTime joinedAtTo,
            List<Long> tagIds,
            Pageable pageable
    ) {

        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User (id=" + userId + ") was not found!");
        }

        boolean isAdmin = authorizationChecker.isAdmin();

        if (userRole != null || email != null || phone != null) {
            if (!isAdmin) {
                throw new AccessDeniedException("User is not an admin!");
            }
        }

        Specification<User> spec = userSpecification.build(
                null,
                userId, userRole, email, phone, username,
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

    UserDTO createUserFollowing(
            Long userId,
            CreateUserAssociationDTO
            createUserAssociationDTO
    ) {

        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User (id=" + userId + ") was not found!");
        }

        constraintValidator.validate(createUserAssociationDTO);

        if(!authorizationChecker.isOwner(userId)) {
            throw new AccessDeniedException("User is not the owner!");
        }

        User user = userRepository.findUserById(userId);
        User toAddUser = userRepository
                .findUserById(createUserAssociationDTO.userId());
        user.getFollowing().add(toAddUser);

        userRepository.save(user);

        boolean isAdmin = authorizationChecker.isAdmin();

        return UserDTO.builder()
                .id(Optional.of(toAddUser.getId()))
                .role(isAdmin ? Optional.of(toAddUser.getRole()) : Optional.empty())
                .email(isAdmin ? Optional.of(toAddUser.getEmail()) : Optional.empty())
                .phone(isAdmin ? Optional.of(toAddUser.getPhone()) : Optional.empty())
                .username(toAddUser.getUsername())
                .description(toAddUser.getDescription())
                .profilePicUrl(toAddUser.getProfilePicUrl())
                .backgroundPicUrl(toAddUser.getBackgroundPicUrl())
                .joinedAt(toAddUser.getJoinedAt())
                .build();
    }

    Long countUserFollowing(
            Long userId,
            UserRole userRole,
            String email,
            String phone,
            String username,
            String description,
            LocalDateTime joinedAtFrom,
            LocalDateTime joinedAtTo,
            List<Long> tagIds
    ) {

        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User (id=" + userId + ") was not found!");
        }

        boolean isAdmin = authorizationChecker.isAdmin();

        if (userRole != null || email != null || phone != null) {
            if (!isAdmin) {
                throw new AccessDeniedException("User is not an admin!");
            }
        }

        Specification<User> spec = userSpecification.build(
                null, userId, userRole, email, phone, username,
                description, joinedAtFrom, joinedAtTo, tagIds);

        return userRepository.count(spec);
    }

    void deleteUserFollowing(Long userId, Long followingUserId) {

        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User (id=" + userId + ") was not found!");
        }

        if (!userRepository.existsById(followingUserId)) {
            throw new EntityNotFoundException("User (id=" + followingUserId + ") was not found!");
        }

        if (!authorizationChecker.isOwner(userId)) {
            throw new AccessDeniedException("User is not the owner!");
        }

        User user = userRepository.findUserById(userId);
        user.getFollowing().removeIf(u -> u.getId().equals(followingUserId));

        userRepository.save(user);
    }
}
