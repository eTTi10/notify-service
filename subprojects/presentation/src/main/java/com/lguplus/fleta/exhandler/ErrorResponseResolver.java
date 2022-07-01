package com.lguplus.fleta.exhandler;

import com.lguplus.fleta.data.dto.response.ErrorResponseDto;
import com.lguplus.fleta.exception.ParameterTypeMismatchException;
import com.lguplus.fleta.util.YamlPropertySourceFactory;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.Constraint;
import javax.validation.ConstraintViolation;
import javax.validation.Payload;
import javax.validation.metadata.ConstraintDescriptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;

/**
 * @author Minwoo Lee
 * @since 1.0
 */
@PropertySource(name = "errors", value = "classpath:errors.yml",
    factory = YamlPropertySourceFactory.class)
@Component
@Slf4j
public class ErrorResponseResolver {

    /**
     *
     */
    private static final String FLAG_PROPERTY_PREFIX = "error.flag.";

    /**
     *
     */
    private static final String MESSAGE_PROPERTY_PREFIX = "error.message.";

    /**
     *
     */
    private final Map<Class<?>, String> flags = new HashMap<>();

    /**
     *
     */
    private final Map<String, String> messages = new HashMap<>();

    /**
     *
     */
    public ErrorResponseResolver(final StandardEnvironment environment) {

        final PropertiesPropertySource propertySource = (PropertiesPropertySource)
            environment.getPropertySources().get("errors");
        if (propertySource == null) {
            throw new IllegalStateException("Error properties file not found.");
        }

        propertySource.getSource()
            .forEach((propertyName, propertyValue) -> {
                if (propertyName.startsWith(FLAG_PROPERTY_PREFIX)) {
                    addFlag(propertyName, propertyValue);
                } else if (propertyName.startsWith(MESSAGE_PROPERTY_PREFIX)) {
                    addMessage(propertyName, propertyValue);
                }
            });

        if (!flags.containsKey(Throwable.class)) {
            throw new IllegalStateException("No error flag defined for java.lang.Throwable.");
        }

        flags.values()
            .forEach(flag -> {
                if (!messages.containsKey(flag)) {
                    throw new IllegalStateException("No error message defined for flag " + flag + ".");
                }
            });
    }

    /**
     * @param propertyName
     * @param propertyValue
     */
    private void addFlag(final String propertyName, final Object propertyValue) {

        final String className = propertyName.replace(FLAG_PROPERTY_PREFIX, "");
        try {
            final Class<?> aClass = Class.forName(className);
            if (Throwable.class.isAssignableFrom(aClass) ||
                (aClass.isAnnotation() && aClass.isAnnotationPresent(Constraint.class))) {
                flags.put(aClass, String.valueOf(propertyValue));
                return;
            }

            log.warn("{} neither subclass of java.lang.Throwable nor javax.validation.Constraint.", className);
        } catch (final ClassNotFoundException e) {
            log.warn("{} not found.", className);
        }
    }

    /**
     * @param propertyName
     * @param propertyValue
     */
    private void addMessage(final String propertyName, final Object propertyValue) {

        final String flag = propertyName.replace(MESSAGE_PROPERTY_PREFIX, "");
        messages.put(flag, String.valueOf(propertyValue));
    }

    /**
     * @param flag
     * @param constraintMessage
     * @return
     */
    private String getConstraintMessage(final String flag, final String constraintMessage) {

        if (StringUtils.isBlank(constraintMessage)) {
            return messages.get(flag);
        } else {
            return constraintMessage;
        }
    }

    /**
     * @param payload
     * @return
     */
    private ErrorResponseDto resolveByPayload(
        final Class<? extends Payload> payload,
        final String constraintMessage
    ) {

        if (!Throwable.class.isAssignableFrom(payload)) {
            return null;
        }

        final String flag = flags.get(payload);
        if (flag == null) {
            return null;
        }

        try {
            final Constructor<? extends Throwable> constructor = payload.asSubclass(Throwable.class)
                .getConstructor(String.class);
            final String message = getConstraintMessage(flag, constraintMessage);
            return resolve(constructor.newInstance(message));
        } catch (final Throwable e) {
            // Do nothing.
        }
        return null;
    }

    /**
     * @param code
     * @return
     */
    private ErrorResponseDto resolveByObjectErrorCode(final String code) {

        if ("typeMismatch".equals(code)) {
            return resolve(new ParameterTypeMismatchException());
        }
        return null;
    }

    /**
     * @param error
     * @return
     */
    private ErrorResponseDto resolveByObjectError(final ObjectError error) {

        try {
            final ConstraintDescriptor<?> constraintDescriptor = error.unwrap(ConstraintViolation.class)
                .getConstraintDescriptor();
            final Set<Class<? extends Payload>> payloads = constraintDescriptor.getPayload();
            if (!payloads.isEmpty()) {
                final ErrorResponseDto response = resolveByPayload(payloads.iterator().next(),
                    error.getDefaultMessage());
                if (response != null) {
                    return response;
                }
            }

            final String flag = flags.get(constraintDescriptor.getAnnotation().annotationType());
            if (flag == null) {
                return null;
            }

            return ErrorResponseDto.builder()
                .flag(flag)
                .message(getConstraintMessage(flag, error.getDefaultMessage()))
                .build();
        } catch (final IllegalArgumentException e) {
            return resolveByObjectErrorCode(error.getCode());
        }
    }

    /**
     * @param ex
     * @return
     */
    public ErrorResponseDto resolve(final BindException ex) {

        final List<ObjectError> errors = ex.getAllErrors();
        if (!errors.isEmpty()) {
            final ErrorResponseDto response = resolveByObjectError(ex.getAllErrors().get(0));
            if (response != null) {
                return response;
            }
        }
        return resolve((Throwable) ex);
    }

    /**
     * @param th
     * @return
     */
    public ErrorResponseDto resolve(final Throwable th) {

        Class<?> aClass = th.getClass();
        do {
            final String errorFlag = flags.get(aClass);
            if (errorFlag == null) {
                aClass = aClass.getSuperclass();
                continue;
            }

            final String message = th.getMessage();
            return ErrorResponseDto.builder()
                .flag(errorFlag)
                .message(StringUtils.isBlank(message) ? messages.get(errorFlag) : message)
                .build();
        } while (aClass != null);
        return null;
    }
}
