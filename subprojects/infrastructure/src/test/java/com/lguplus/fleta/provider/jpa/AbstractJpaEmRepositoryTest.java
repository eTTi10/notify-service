package com.lguplus.fleta.provider.jpa;

import com.lguplus.fleta.exception.ServiceException;
import com.lguplus.fleta.testutil.TestDto;
import org.assertj.core.api.ThrowableAssert;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.*;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

/**
 * AbstractJpaEmRepository 유닛 테스트
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class AbstractJpaEmRepositoryTest {

    // @Spy EntityManager em;
    @Mock Query query;
    @Mock NativeQuery<?> nativeQuery;
    @Mock org.hibernate.query.Query hibernateQuery;

    @Spy @InjectMocks TestConcreteJpaEmRepository jpaEmRepository;
    TestDto expectedDto;

    private static class TestConcreteJpaEmRepository extends AbstractJpaEmRepository { }

    @BeforeEach
    void beforeEach() {
        expectedDto = new TestDto(1, "Megazone");
    }

    @Test
    void convertSingle_int() {
        // Given
        doCallRealMethod().when(jpaEmRepository).convertSingle(query, Integer.class);
        when(jpaEmRepository.convertSingle(query, Integer.class)).thenReturn(Optional.of(expectedDto.getId()));
        // When
        Optional<Integer> actual = jpaEmRepository.convertSingle(query, Integer.class);
        // Then
        assertThat(actual).isPresent();
        actual.ifPresent(a -> assertThat(a).isEqualTo(expectedDto.getId()));
    }

    @Test
    void convertSingle_string() {
        // Given
        doCallRealMethod().when(jpaEmRepository).convertSingle(query, String.class);
        when(jpaEmRepository.convertSingle(query, String.class)).thenReturn(Optional.of(expectedDto.getName()));
        // When
        Optional<String> actual = jpaEmRepository.convertSingle(query, String.class);
        // Then
        assertThat(actual).isPresent();
        actual.ifPresent(s -> assertThat(s).isEqualTo(expectedDto.getName()));
    }

    @Test
    void convertSingle_nullQuery() {
        // Given
        query = null;
        doCallRealMethod().when(jpaEmRepository).convertSingle(this.query, String.class);
        // When
        ThrowableAssert.ThrowingCallable callable = () -> this.jpaEmRepository.convertSingle(this.query, String.class);
        // Then
        assertThatThrownBy(callable).isInstanceOf(NullPointerException.class);
    }

    @Test
    void convertSingle_string_withNoResultException() {
        // Given
        doCallRealMethod().when(jpaEmRepository).convertSingle(query, String.class);
        when(query.getSingleResult()).thenThrow(NoResultException.class);
        // When
        Optional<String> actual = jpaEmRepository.convertSingle(query, String.class);
        // Then
        assertThat(actual).isNotPresent();
    }

    @Test
    void convertSingle_string_withNonUniqueResultException() {
        // Given
        doCallRealMethod().when(jpaEmRepository).convertSingle(query, String.class);
        when(query.getSingleResult()).thenThrow(NonUniqueResultException.class);
        // When
        ThrowableAssert.ThrowingCallable callable = () -> jpaEmRepository.convertSingle(query, String.class);
        // Then
        assertThatThrownBy(callable).isInstanceOf(ServiceException.class);
    }

    @Test
    void convertSingle_dto() {
        // Given
        mockConvertSingleWithDto();
        when(jpaEmRepository.convertSingle(query, TestDto.class)).thenReturn(Optional.of(expectedDto));
        // When
        Optional<TestDto> actualDto = jpaEmRepository.convertSingle(query, TestDto.class);
        // Then
        assertThat(actualDto).isPresent();
        actualDto.ifPresent(dto -> assertThat(dto).isInstanceOf(TestDto.class));
        actualDto.ifPresent(dto -> assertThat(dto.getName()).isEqualTo(expectedDto.getName()));
    }

    private void mockConvertSingleWithDto() {
        doCallRealMethod().when(jpaEmRepository).convertSingle(query, TestDto.class);
        when(query.unwrap(NativeQuery.class)).thenReturn(nativeQuery);
        when(nativeQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)).thenReturn(hibernateQuery);
    }

    @Test
    void convertSingle_dto_withNoResultException() {
        // Given
        mockConvertSingleWithDto();
        when(hibernateQuery.getSingleResult()).thenThrow(NoResultException.class);
        // When
        Optional<TestDto> actualDto = jpaEmRepository.convertSingle(query, TestDto.class);
        // Then
        assertThat(actualDto).isNotPresent();
    }

    @Test
    void convertSingle_dto_withNonUniqueResultException() {
        // Given
        mockConvertSingleWithDto();
        when(hibernateQuery.getSingleResult()).thenThrow(NonUniqueResultException.class);
        // When
        ThrowableAssert.ThrowingCallable callable = () -> jpaEmRepository.convertSingle(query, TestDto.class);
        // Then
        assertThatThrownBy(callable).isInstanceOf(ServiceException.class);
    }

    @Test
    void convertList_int() {
        // Given
        doCallRealMethod().when(jpaEmRepository).convertList(query, Integer.class);
        List<Integer> expected = List.of(expectedDto.getId(), expectedDto.getId());
        when(jpaEmRepository.convertList(query, Integer.class)).thenReturn(expected);
        // When
        List<Integer> actual = jpaEmRepository.convertList(query, Integer.class);
        // Then
        assertThat(actual.size()).isEqualTo(expected.size());
        assertThat(actual.get(0)).isEqualTo(expected.get(0));
    }

    @Test
    void convertList_string() {
        // Given
        doCallRealMethod().when(jpaEmRepository).convertList(query, String.class);
        List<String> expected = List.of(expectedDto.getName(), expectedDto.getName());
        when(query.getResultList()).thenReturn(expected);
        // When
        List<String> actual = jpaEmRepository.convertList(query, String.class);
        // Then
        assertThat(actual.size()).isEqualTo(expected.size());
        assertThat(actual.get(0)).isEqualTo(expected.get(0));
    }

    @Test
    void convertList_dto() {
        // Given
        doCallRealMethod().when(jpaEmRepository).convertList(query, TestDto.class);
        when(query.unwrap(NativeQuery.class)).thenReturn(nativeQuery);
        when(nativeQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)).thenReturn(hibernateQuery);
        List<TestDto> expected = List.of(expectedDto, expectedDto);
        when(jpaEmRepository.convertList(query, TestDto.class)).thenReturn(expected);
        // When
        List<TestDto> actual = jpaEmRepository.convertList(query, TestDto.class);
        // Then
        assertThat(actual.size()).isEqualTo(expected.size());
        assertThat(actual.get(0)).isEqualTo(expected.get(0));
    }

    @Test
    void convertList_nullQuery() {
        // Given
        query = null;
        doCallRealMethod().when(jpaEmRepository).convertList(query, TestDto.class);
        // When
        ThrowableAssert.ThrowingCallable callable = () -> jpaEmRepository.convertList(query, TestDto.class);
        // Then
        assertThatThrownBy(callable).isInstanceOf(NullPointerException.class);
    }
}
