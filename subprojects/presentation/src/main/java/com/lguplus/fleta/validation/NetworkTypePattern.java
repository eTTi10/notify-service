package com.lguplus.fleta.validation;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = NetworkTypePattern.Validator.class)
public @interface NetworkTypePattern {

    String message() default "파라미터값 net_typ 잘못된 포멧";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<NetworkTypePattern, String> {

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value != null) {
                try {
                    Integer.valueOf(value);
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            return true;
        }
    }
}
