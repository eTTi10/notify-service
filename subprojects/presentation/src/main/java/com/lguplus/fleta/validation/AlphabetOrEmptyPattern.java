package com.lguplus.fleta.validation;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Optional;
import java.util.regex.Pattern;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = AlphabetOrEmptyPattern.Validator.class)
public @interface AlphabetOrEmptyPattern {

    String message() default "영문자만 사용할수 있습니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<AlphabetOrEmptyPattern, String> {

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            return Optional.ofNullable(value)
                .filter(v -> v.length() > 0)
                .map(v -> {
                    Pattern pattern = Pattern.compile("^[a-zA-Z]*$");
                    return pattern.matcher(v).find();
                })
                .orElse(true);
        }
    }
}
