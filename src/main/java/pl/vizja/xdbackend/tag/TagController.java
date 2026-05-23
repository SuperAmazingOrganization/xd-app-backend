package pl.vizja.xdbackend.tag;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.vizja.xdbackend.tag.dto.CreateTagDTO;
import pl.vizja.xdbackend.tag.dto.TagDTO;
import pl.vizja.xdbackend.tag.dto.UpdateTagDTO;
import java.util.List;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
@Tag(name = "Tags")
class TagController {

    private final TagService tagService;

    @Operation(summary = "Get tags")
    @GetMapping
    ResponseEntity<List<TagDTO>> getTags(
            @RequestParam @Nullable String name,
            @Nullable Pageable pageable
            ) {

        return ResponseEntity.ok(tagService.getTags(name, pageable));
    }

    @Operation(summary = "Create a new tag [ADMIN]")
    @PostMapping
    ResponseEntity<TagDTO> createTag(
            @RequestBody CreateTagDTO createTagDTO
    ) {

        return ResponseEntity.ok(tagService.createTag(createTagDTO));
    }

    @Operation(summary = "Count tags")
    @GetMapping("/count")
    ResponseEntity<Long> countTags(
            @RequestParam @Nullable String name
    ) {

        return ResponseEntity.ok(tagService.countTags(name));
    }

    @Operation(summary = "Get a tag")
    @GetMapping("/{tagId}")
    ResponseEntity<TagDTO> getTag(
            @PathVariable Long tagId
    ) {

        return ResponseEntity.ok(tagService.getTag(tagId));
    }

    @Operation(summary = "Update a tag [ADMIN]")
    @PatchMapping("/{tagId}")
    ResponseEntity<TagDTO> updateTag(
            @PathVariable Long tagId,
            @RequestBody UpdateTagDTO updateTagDTO
    ) {

        return ResponseEntity.ok(tagService.updateTag(tagId,
                updateTagDTO));
    }

    @Operation(summary = "Delete a tag [ADMIN]")
    @DeleteMapping("/{tagId}")
    ResponseEntity<Void> deleteTag(
            @PathVariable Long tagId
    ) {

        tagService.deleteTag(tagId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
