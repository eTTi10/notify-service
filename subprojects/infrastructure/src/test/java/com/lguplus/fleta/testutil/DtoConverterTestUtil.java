package com.lguplus.fleta.testutil;

import com.lguplus.fleta.util.DtoConverter;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;

/**
 * DtoConverter 유닛 테스트를 위한 Util
 * @version 1.0
 */
public class DtoConverterTestUtil {

    public static void testMockedDtoConverterForSingle(TestCallback callback, Object verificationReturned) {
        // Mock scope
        try (MockedStatic<DtoConverter> mockedDtoConverter = Mockito.mockStatic(DtoConverter.class)) {
            MockedStatic.Verification verification = () -> DtoConverter.convertSingle(any(), any());
            mockedDtoConverter.when(verification).thenReturn(verificationReturned);
            callback.call();
        }
    }

    public static <E extends Exception> void testMockedDtoConverterForSingleThrowException(TestCallback callback, Class<E> exceptionType) {
        // Mock scope
        try (MockedStatic<DtoConverter> mockedDtoConverter = Mockito.mockStatic(DtoConverter.class)) {
            MockedStatic.Verification verification = () -> DtoConverter.convertSingle(any(), any());
            mockedDtoConverter.when(verification).thenThrow(exceptionType);
            callback.call();
        }
    }

    public static void testMockedDtoConverterForList(TestCallback callback, Object verificationReturned) {
        // Mock scope
        try (MockedStatic<DtoConverter> mockedDtoConverter = Mockito.mockStatic(DtoConverter.class)) {
            MockedStatic.Verification verification = () -> DtoConverter.convertList(any(), any());
            mockedDtoConverter.when(verification).thenReturn(verificationReturned);
            callback.call();
        }
    }

    public interface TestCallback {
        void call();
    }
}
