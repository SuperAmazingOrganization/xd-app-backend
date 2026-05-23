package pl.vizja.xdbackend.tag;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import pl.vizja.xdbackend.security.AuthorizationChecker;
import pl.vizja.xdbackend.tag.dto.CreateTagDTO;
import pl.vizja.xdbackend.tag.dto.TagDTO;
import pl.vizja.xdbackend.tag.dto.UpdateTagDTO;
import pl.vizja.xdbackend.validation.CustomConstraintValidator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
class TagService {

    private final TagRepository tagRepository;
    private final TagSpecification tagSpecification;
    private final CustomConstraintValidator constraintValidator;
    private final AuthorizationChecker authorizationChecker;

    List<TagDTO> getTags(
            String name,
            Pageable pageable
    ) {

        Specification<Tag> tagSpec =
                tagSpecification.build(null, null, name);

        return tagRepository.findAll(tagSpec, pageable)
                .stream().map(tag -> TagDTO.builder()
                        .id(Optional.of(tag.getId()))
                        .name(tag.getName())
                        .build()).toList();
    }

    TagDTO createTag(
        CreateTagDTO createTagDTO
    ) {

        constraintValidator.validate(createTagDTO);

        if(!authorizationChecker.isAdmin()) {
            throw new AccessDeniedException("User is not an admin!");
        }

        Tag tag = Tag.builder()
                .name(createTagDTO.name())
                .build();

        tagRepository.save(tag);

        return TagDTO.builder()
                .id(Optional.of(tag.getId()))
                .name(tag.getName())
                .build();

    }

    Long countTags(
            String name
    ) {

        Specification<Tag> tagSpec =
                tagSpecification.build(null,null, name);

        return tagRepository.count(tagSpec);
    }

    TagDTO getTag(
            Long tagId
    ) {

        if(!tagRepository.existsById(tagId)) {
            throw new EntityNotFoundException
                    ("Tag (id=" + tagId + ") was not found!");
        }

        Tag tag = tagRepository.findTagById(tagId);

        return TagDTO.builder()
                .id(Optional.empty())
                .name(tag.getName())
                .build();
    }

    TagDTO updateTag(
            Long tagId,
            UpdateTagDTO updateTagDTO
    ) {

        if(!tagRepository.existsById(tagId)) {
            throw new EntityNotFoundException
                    ("Tag (id=" + tagId + ") was not found!");
        }

        constraintValidator.validate(updateTagDTO);

        if(!authorizationChecker.isAdmin()) {
            throw new AccessDeniedException("User is not an admin!");
        }

        Tag tag = tagRepository.findTagById(tagId);
        String name = tag.getName();
        if(name != null) tag.setName(name);

        tagRepository.save(tag);

        return TagDTO.builder()
                .id(Optional.empty())
                .name(tag.getName())
                .build();
    }

    void deleteTag(
            Long tagId
    ) {

        if(!tagRepository.existsById(tagId)) {
            throw new EntityNotFoundException
                    ("Tag (id=" + tagId + ") was not found!");
        }

        if(!authorizationChecker.isAdmin()) {
            throw new AccessDeniedException("User is not an admin!");
        }

        tagRepository.deleteById(tagId);
    }
}
