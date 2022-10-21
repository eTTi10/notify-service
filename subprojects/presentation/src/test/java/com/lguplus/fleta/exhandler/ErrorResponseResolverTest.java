package com.lguplus.fleta.exhandler;

import com.lguplus.fleta.data.dto.response.ErrorResponseDto;
import com.lguplus.fleta.exception.InvalidRequestTypeException;
import com.lguplus.fleta.exception.ParameterMissingException;
import com.lguplus.fleta.exception.ParameterTypeMismatchException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.ConstraintViolation;
import javax.validation.Payload;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.metadata.ConstraintDescriptor;
import static org.assertj.core.api.Assertions.assertThat;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.util.annotation.ConstraintAnnotationDescriptor;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;

@ExtendWith(SpringExtension.class)
class ErrorResponseResolverTest {

    //    @Mock
    //    StandardEnvironment environment;

    //    @Autowired
    //    @InjectMocks
    ErrorResponseResolver errorResponseResolver;

    @BeforeEach
    void setUp() throws Exception {
        StandardEnvironment environment = mock(StandardEnvironment.class);

        MutablePropertySources mutablePropertySources = mock(MutablePropertySources.class);
        given(environment.getPropertySources()).willReturn(mutablePropertySources);

        PropertiesPropertySource propertySource = mock(PropertiesPropertySource.class);
        given(mutablePropertySources.get(anyString())).willReturn((PropertySource) propertySource);

        Map<String, Object> map = new HashMap<>();
        map.put("error.flag.com.lguplus.fleta.exception.ParameterTypeMismatchException", "5008");
        map.put("error.flag.java.lang.Throwable", "9999");
        map.put("error.flag.javax.validation.constraints.NotNull", "5000");
        map.put("error.flag.com.lguplus.fleta.exhandler.NotInstantiatableException", "9999");

        map.put("error.message.5008", "잘못된 요청 형식 또는 지원하지 않는 응답 형식");
        map.put("error.message.9999", "기타 에러");
        map.put("error.message.5000", "필수 요청 정보 누락");
        given(propertySource.getSource()).willReturn(map);

        errorResponseResolver = new ErrorResponseResolver(environment);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    void testErrorResponseResolver_propertySource_equals_null() {
        StandardEnvironment environment = mock(StandardEnvironment.class);

        MutablePropertySources mutablePropertySources = mock(MutablePropertySources.class);
        given(environment.getPropertySources()).willReturn(mutablePropertySources);

        PropertiesPropertySource propertySource = null;
        given(mutablePropertySources.get(anyString())).willReturn((PropertySource) propertySource);

        Exception e = assertThrows(IllegalStateException.class, () -> new ErrorResponseResolver(environment));
        assertTrue(e instanceof IllegalStateException);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    void testErrorResponseResolver_flags_not_containsKey_Throwable() {
        StandardEnvironment environment = mock(StandardEnvironment.class);

        MutablePropertySources mutablePropertySources = mock(MutablePropertySources.class);
        given(environment.getPropertySources()).willReturn(mutablePropertySources);

        PropertiesPropertySource propertySource = mock(PropertiesPropertySource.class);
        given(mutablePropertySources.get(anyString())).willReturn((PropertySource) propertySource);

        Map<String, Object> map = new HashMap<>();
        map.put("error.flag.test.key", "TEST_FLAG");
        map.put("error.message.test.key", "TEST_MESSAGE");
        given(propertySource.getSource()).willReturn(map);

        Exception e = assertThrows(IllegalStateException.class, () -> new ErrorResponseResolver(environment));
        assertTrue(e instanceof IllegalStateException);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    void testErrorResponseResolver_flags_containsKey_Throwable() {
        StandardEnvironment environment = mock(StandardEnvironment.class);

        MutablePropertySources mutablePropertySources = mock(MutablePropertySources.class);
        given(environment.getPropertySources()).willReturn(mutablePropertySources);

        PropertiesPropertySource propertySource = mock(PropertiesPropertySource.class);
        given(mutablePropertySources.get(anyString())).willReturn((PropertySource) propertySource);

        Map<String, Object> map = new HashMap<>();
        map.put("error.flag.java.lang.Throwable", "9999");
        map.put("error.flag.javax.validation.constraints.NotNull", "5000");
        map.put("test", "");
        map.put("error.flag.org.junit.jupiter.api.Test", "");
        map.put("error.flag.java.lang.String", "");
        map.put("error.flag.java.none.TestException", "99999999");

        //map.put("error.message.9999", "기타 에러");
        map.put("error.message.5000", "필수 요청 정보 누락");
        given(propertySource.getSource()).willReturn(map);

        Exception e = assertThrows(IllegalStateException.class, () -> new ErrorResponseResolver(environment));
        assertTrue(e instanceof IllegalStateException);
    }


    @Test
    void testResolve_BindException() {
        //BindException be = new BindException(new Object(), "java.lang.Object");
        BindException bex = mock(BindException.class);

        List<ObjectError> errors = new ArrayList<>();
        errors.add(new ObjectError("java.none.TestException", "This is Test Exception."));
        given(bex.getAllErrors()).willReturn(errors);

        ErrorResponseDto resolve = errorResponseResolver.resolve(bex);
        String flag = resolve.getFlag();
        assertThat(flag).isEqualTo("9999");
    }

    @Test
    void testResolve_BindException_NoError() {
        ErrorResponseDto resolve = errorResponseResolver.resolve(new BindException(this, "sample"));
        String flag = resolve.getFlag();
        assertThat(flag).isEqualTo("9999");
    }

    @Test
    void testResolve_BindException_ObjectError() {
        ConstraintDescriptor<NotNull> constraintDescriptor = new ConstraintDescriptorImpl<>(
            ConstraintHelper.forAllBuiltinConstraints(),
            null,
            new ConstraintAnnotationDescriptor.Builder(NotNull.class).build(),
            ConstraintLocation.ConstraintLocationKind.FIELD,
            ConstraintDescriptorImpl.ConstraintType.GENERIC);

        ConstraintViolation<NotNull> constraintViolation = ConstraintViolationImpl.forParameterValidation(
            null, null, null, null, null, null, null, null, null, constraintDescriptor, null, null);
        ObjectError error = new ObjectError("test", null);
        error.wrap(constraintViolation);

        BindException bex = new BindException("test", "test");
        bex.addError(error);

        ErrorResponseDto resolve = errorResponseResolver.resolve(bex);
        String flag = resolve.getFlag();
        assertThat(flag).isEqualTo("5000");
    }

    @Test
    void testGetConstraintMessage_BlankMessage() throws Exception {
        Method method = ReflectionUtils.findMethod(errorResponseResolver.getClass(), "getConstraintMessage", String.class, String.class);
        method.setAccessible(true);
        String message = (String) method.invoke(errorResponseResolver, "5000", null);
        assertThat(message).isEqualTo("필수 요청 정보 누락");
    }

    @Test
    void testGetConstraintMessage_NotBlankMessage() throws Exception {
        Method method = ReflectionUtils.findMethod(errorResponseResolver.getClass(), "getConstraintMessage", String.class, String.class);
        method.setAccessible(true);
        String message = (String) method.invoke(errorResponseResolver, "5000", "메시지");
        assertThat(message).isEqualTo("메시지");
    }

    @Test
    void testResolveByPayload_NotThrowable() throws Exception {
        Method method = ReflectionUtils.findMethod(errorResponseResolver.getClass(), "resolveByPayload", Class.class, String.class);
        method.setAccessible(true);
        ErrorResponseDto response = (ErrorResponseDto) method.invoke(errorResponseResolver, Payload.class, null);
        assertThat(response).isNull();
    }

    @Test
    void testResolveByPayload_NotRegistered() throws Exception {
        Method method = ReflectionUtils.findMethod(errorResponseResolver.getClass(), "resolveByPayload", Class.class, String.class);
        method.setAccessible(true);
        ErrorResponseDto response = (ErrorResponseDto) method.invoke(errorResponseResolver, InvalidRequestTypeException.class, "필수 요청 정보 누락");
        assertThat(response).isNull();
    }

    @Test
    void testResolveByPayload_InstantiationSucceeded() throws Exception {
        Method method = ReflectionUtils.findMethod(errorResponseResolver.getClass(), "resolveByPayload", Class.class, String.class);
        method.setAccessible(true);
        ErrorResponseDto response = (ErrorResponseDto) method.invoke(errorResponseResolver, ParameterTypeMismatchException.class, null);
        assertThat(response.getFlag()).isEqualTo("5008");
    }

    @Test
    void testResolveByPayload_InstantiationFailed() throws Exception {
        Method method = ReflectionUtils.findMethod(errorResponseResolver.getClass(), "resolveByPayload", Class.class, String.class);
        method.setAccessible(true);
        ErrorResponseDto response = (ErrorResponseDto) method.invoke(errorResponseResolver, NotInstantiatableException.class, "필수 요청 정보 누락");
        assertThat(response).isNull();
    }

    @Test
    void testResolveByObjectError_PayloadEmpty() throws Exception {
        ConstraintDescriptor<NotNull> constraintDescriptor = new ConstraintDescriptorImpl<>(
            ConstraintHelper.forAllBuiltinConstraints(),
            null,
            new ConstraintAnnotationDescriptor.Builder(NotNull.class).build(),
            ConstraintLocation.ConstraintLocationKind.FIELD,
            ConstraintDescriptorImpl.ConstraintType.GENERIC);

        ConstraintViolation<NotNull> constraintViolation = ConstraintViolationImpl.forParameterValidation(
            null, null, null, null, null, null, null, null, null, constraintDescriptor, null, null);
        ObjectError error = new ObjectError("test", null);
        error.wrap(constraintViolation);

        Method method = ReflectionUtils.findMethod(errorResponseResolver.getClass(), "resolveByObjectError", ObjectError.class);
        method.setAccessible(true);
        ErrorResponseDto response = (ErrorResponseDto) method.invoke(errorResponseResolver, error);
        assertThat(response.getFlag()).isEqualTo("5000");
    }

    @Test
    void testResolveByObjectError_PayloadNotEmpty_ResponseNotNull() throws Exception {
        ConstraintDescriptor<NotNull> constraintDescriptor = new ConstraintDescriptorImpl<>(
            ConstraintHelper.forAllBuiltinConstraints(),
            null,
            new ConstraintAnnotationDescriptor.Builder(NotNull.class).setPayload(new Class<?>[]{ParameterTypeMismatchException.class}).build(),
            ConstraintLocation.ConstraintLocationKind.FIELD,
            ConstraintDescriptorImpl.ConstraintType.GENERIC);

        ConstraintViolation<NotNull> constraintViolation = ConstraintViolationImpl.forParameterValidation(
            null, null, null, null, null, null, null, null, null, constraintDescriptor, null, null);
        ObjectError error = new ObjectError("test", null);
        error.wrap(constraintViolation);

        Method method = ReflectionUtils.findMethod(errorResponseResolver.getClass(), "resolveByObjectError", ObjectError.class);
        method.setAccessible(true);
        ErrorResponseDto response = (ErrorResponseDto) method.invoke(errorResponseResolver, error);
        assertThat(response.getFlag()).isEqualTo("5008");
    }

    @Test
    void testResolveByObjectError_PayloadNotEmpty_ResponseNull() throws Exception {
        ConstraintDescriptor<NotBlank> constraintDescriptor = new ConstraintDescriptorImpl<>(
            ConstraintHelper.forAllBuiltinConstraints(),
            null,
            new ConstraintAnnotationDescriptor.Builder(NotBlank.class).setPayload(new Class<?>[]{ParameterMissingException.class}).build(),
            ConstraintLocation.ConstraintLocationKind.FIELD,
            ConstraintDescriptorImpl.ConstraintType.GENERIC);

        ConstraintViolation<NotBlank> constraintViolation = ConstraintViolationImpl.forParameterValidation(
            null, null, null, null, null, null, null, null, null, constraintDescriptor, null, null);
        ObjectError error = new ObjectError("test", null);
        error.wrap(constraintViolation);

        Method method = ReflectionUtils.findMethod(errorResponseResolver.getClass(), "resolveByObjectError", ObjectError.class);
        method.setAccessible(true);
        ErrorResponseDto response = (ErrorResponseDto) method.invoke(errorResponseResolver, error);
        assertThat(response).isNull();
    }

    @Test
    void testResolveByObjectErrorCode_TypeMismatch() throws Exception {
        Method method = ReflectionUtils.findMethod(errorResponseResolver.getClass(), "resolveByObjectErrorCode", String.class);
        method.setAccessible(true);
        ErrorResponseDto response = (ErrorResponseDto) method.invoke(errorResponseResolver, "typeMismatch");
        assertThat(response.getFlag()).isEqualTo("5008");
    }

    @Test
    void testResolve_NoThrowable() throws Exception {
        Field f = ReflectionUtils.findField(errorResponseResolver.getClass(), "flags");
        f.setAccessible(true);
        final Map<Class<?>, String> flags = (Map) f.get(errorResponseResolver);
        flags.remove(Throwable.class);
        ErrorResponseDto response = errorResponseResolver.resolve(new RuntimeException());
        assertThat(response).isNull();
    }

    //    @Test
    //    void testResolve_Throwable() {
    //    }

}
