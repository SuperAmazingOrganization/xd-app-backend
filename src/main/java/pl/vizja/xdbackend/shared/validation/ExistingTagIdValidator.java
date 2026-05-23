package pl.vizja.xdbackend.shared.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.vizja.xdbackend.tag.TagRepository;

@Service
@RequiredArgsConstructor
public class ExistingTagIdValidator implements
        ConstraintValidator<ExistingTagIdConstraint, Long> {

    private final TagRepository tagRepository;

    @Override
    public void initialize(ExistingTagIdConstraint tagId) {
    }

    @Override
    public boolean isValid(Long tagId,
                           ConstraintValidatorContext cxt) {
        return tagRepository.existsById(tagId);
    }

}