package com.lguplus.fleta.provider.jpa;

import com.lguplus.fleta.exception.ServiceException;
import com.lguplus.fleta.util.DtoConverter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.springframework.util.ClassUtils;

/**
 * JpaEmRepository 공통 추상 클래스 (EntityManager를 통한 DTO 직접 반환 기능 제공)
 *
 * @version 1.0
 */
@Slf4j
public abstract class AbstractJpaEmRepository {

    private static final List<String> PERMITTED_CLASS_NAMES = Arrays.asList("String", "LocalDate", "LocalDateTime");

    /**
     * 쿼리 결과를 list<T>(없으면 빈 list)로 반환한다.
     *
     * @param query     EntityManager query
     * @param classType *Dto.class
     * @return List<T>
     */
    @SuppressWarnings({"deprecation", "unchecked"})
    public <T> List<T> convertList(@NonNull final Query query, final Class<T> classType) {
        List<?> results;
        if (ClassUtils.isPrimitiveOrWrapper(classType) || PERMITTED_CLASS_NAMES.contains(classType.getSimpleName())) {
            results = query.getResultList();
            return results.stream().map(o -> (T) o).collect(Collectors.toList());
        } else {
            results = query.unwrap(NativeQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();
        }

        return DtoConverter.convertList(results, classType);
    }

    /**
     * 쿼리 결과 단건을 Optional<T>로 반환한다.
     *
     * @param query     EntityManager query
     * @param classType *Dto.class
     * @return Optional<T>
     */
    public <T> Optional<T> convertSingle(@NonNull final Query query, final Class<T> classType) {
        if (ClassUtils.isPrimitiveOrWrapper(classType) || PERMITTED_CLASS_NAMES.contains(classType.getSimpleName())) {
            return this.convertPrimitiveOrWrapper(query);
        }

        return this.convertSingleDto(query, classType);
    }

    @SuppressWarnings("deprecation")
    private <T> Optional<T> convertSingleDto(Query query, Class<T> classType) {
        Object singleResult = null;
        try {
            singleResult = query.unwrap(NativeQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getSingleResult();
        } catch (NoResultException nre) {
            log.debug(">>> NoResultException: {}", nre.getMessage());
        } catch (NonUniqueResultException e) {
            throw new ServiceException(e);
        }

        return DtoConverter.convertSingle(singleResult, classType);
    }

    @SuppressWarnings("unchecked")
    private <T> Optional<T> convertPrimitiveOrWrapper(Query query) {
        T singleResult = null;
        try {
            singleResult = (T) query.getSingleResult();
        } catch (NoResultException nre) {
            log.debug(">>> NoResultException: {}", nre.getMessage());
        } catch (NonUniqueResultException e) {
            throw new ServiceException(e);
        }

        return Optional.ofNullable(singleResult);
    }
}
