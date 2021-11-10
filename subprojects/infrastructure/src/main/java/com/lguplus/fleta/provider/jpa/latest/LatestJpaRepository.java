package com.lguplus.fleta.provider.jpa.latest;

import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto;
import com.lguplus.fleta.data.entity.LatestEntity;
import com.lguplus.fleta.repository.LatestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Repository
public class LatestJpaRepository implements LatestRepository {

    @PersistenceContext
    private EntityManager entityManager;


    /**
     * variation 정보 조회
     *
     * @param latestRequestDto variation 정보 조회를 위한 DTO
     * @return variation 정보 조회 결과
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<LatestEntity> getLatest(LatestRequestDto latestRequestDto) {
        log.debug("====================getVariationInfoList====================BEGIN");

        String saId = latestRequestDto.getSaId();
        String mac = latestRequestDto.getMac();
        String ctn = latestRequestDto.getCtn();
        String catId = latestRequestDto.getCatId();

        log.debug("saId : "+saId);
        log.debug("mac : "+mac);
        log.debug("ctn : "+ctn);
        log.debug("catId : "+catId);


        List<String> parameters = new ArrayList<>();

        String sql = "SELECT SA_ID, MAC, CTN, REG_ID, CAT_ID, CAT_NAME, R_DATE, CATEGORY_GB FROM SMARTUX.PT_UX_LATEST";
        //조건문이 있으면 추가로 sql += 동적쿼리 작성...

        Query nativeQuery = entityManager.createNativeQuery(sql, LatestEntity.class);

        IntStream.range(0, parameters.size()).forEach(i -> nativeQuery.setParameter(i + 1, parameters.get(i)));

        List<LatestEntity> latestList = (List<LatestEntity>)nativeQuery.getResultList();

        log.debug("====================getVariationInfoList====================END");

        return latestList;
    }
}
