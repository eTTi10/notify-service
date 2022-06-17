package com.lguplus.fleta.validation;

import org.apache.oro.text.perl.Perl5Util;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, METHOD})
@Retention(RUNTIME)
@Repeatable(Perl5Pattern.List.class)
@Constraint(validatedBy = Perl5Pattern.Validator.class)
public @interface Perl5Pattern {

    String regexp();

    String message() default "잘못된 요청 형식 또는 지원하지 않는 응답 형식";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({FIELD, METHOD})
    @Retention(RUNTIME)
    @Documented
    @interface List {

        Perl5Pattern[] value();
    }

    class Validator implements ConstraintValidator<Perl5Pattern, String> {

        Perl5Util perl5Util = new Perl5Util();
        Perl5Pattern annotation;

        @Override
        public void initialize(Perl5Pattern annotation) {
            this.annotation = annotation;
        }

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            return value == null || perl5Util.match("/" + annotation.regexp() + "/", value);
        }
    }
}
