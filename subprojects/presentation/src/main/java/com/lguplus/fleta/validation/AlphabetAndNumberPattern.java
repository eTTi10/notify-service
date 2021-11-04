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
@Constraint(validatedBy = AlphabetAndNumberPattern.Validator.class)
public @interface AlphabetAndNumberPattern {

    String message() default "숫자와 영문자만 사용할수 있습니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<AlphabetAndNumberPattern, String> {

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            return Optional.ofNullable(value)
                .map(v -> {
                    Pattern pattern = Pattern.compile("^[a-zA-Z0-9.,]*$");
                    return pattern.matcher(v).find();
                })
                .orElse(false);
        }
    }
}
