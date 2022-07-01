package com.lguplus.fleta.validation;

import static java.lang.annotation.ElementType.FIELD;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import java.util.Optional;
import java.util.regex.Pattern;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

@Target(FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = AlphabetPattern.Validator.class)
public @interface AlphabetPattern {

    String message() default "영문자만 사용할수 있습니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<AlphabetPattern, String> {

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            return Optional.ofNullable(value)
                .map(v -> {
                    Pattern pattern = Pattern.compile("^[a-zA-Z]*$");
                    return pattern.matcher(v).find();
                })
                .orElse(false);
        }
    }
}
