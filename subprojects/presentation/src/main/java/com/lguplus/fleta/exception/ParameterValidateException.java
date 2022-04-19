package com.lguplus.fleta.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class ParameterValidateException extends RuntimeException {

    public ParameterValidateException(List<ObjectError> allErrors, String errorMsg) {
        super(errorMsg);
        Optional.ofNullable(allErrors).ifPresent(errors ->
            log.error(errors.stream()
                .map(e -> String.format("Error in object '%s': %s", e.getObjectName(), e.getDefaultMessage()))
                .collect(Collectors.joining(" && ")))
        );
    }
}
