package com.lguplus.fleta.provider.jpa.latest;

import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto;
import com.lguplus.fleta.data.entity.LatestCheckEntity;
import com.lguplus.fleta.data.entity.LatestEntity;
import com.lguplus.fleta.repository.LatestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LatestJpaJpaRepository implements LatestRepository {

    private final LatestJpaRepository latestJpaRepository;

    private String catIdStr = "catId";

    @PersistenceContext
    private EntityManager em;

    /**
     * 최신회 리스트 조회
     * @param latestRequestDto
     * @return
     */
    @Override
    public List<LatestEntity> getLatestList(LatestRequestDto latestRequestDto) {
        return latestJpaRepository.findBySaIdAndMacAndCtnAndCatIdOrCatIdIsNull(
                latestRequestDto.getSaId(),
                latestRequestDto.getMac(),
                latestRequestDto.getCtn(),
                latestRequestDto.getCatId()
        );
    }

    /**
     * 최신회 체크리스트 조회
     * @param latestRequestDto
     * @return
     */
    @Override
    public List<LatestEntity> getLatestCheckList(LatestRequestDto latestRequestDto) {
        return latestJpaRepository.findBySaIdAndMacAndCtn(
                latestRequestDto.getSaId(),
                latestRequestDto.getMac(),
                latestRequestDto.getCtn()
        );
    }

    /**
     * 최신회 삭제
     * @param latestRequestDto
     * @return
     */
    @Override
    @Modifying
    @Transactional
    public int deleteLatest(LatestRequestDto latestRequestDto) {
        return latestJpaRepository.deleteBySaIdAndMacAndCtnAndCatId(
                latestRequestDto.getSaId(),
                latestRequestDto.getMac(),
                latestRequestDto.getCtn(),
                latestRequestDto.getCatId()
        );
    }


    /**
     * 최신회 등록
     * @param latestRequestDto
     * @return
     */
    @Override
    public int insertLatest(LatestRequestDto latestRequestDto) {

        LatestEntity entity = LatestEntity.builder().
                saId(latestRequestDto.getSaId()).
                mac(latestRequestDto.getMac()).
                ctn(latestRequestDto.getCtn()).
                regId(latestRequestDto.getRegId()).
                catId(latestRequestDto.getCatId()).
                catName(latestRequestDto.getCatName()).
                rDate("2022-01-14 17:24:42.000").
                categoryGb(latestRequestDto.getCategoryGb()).
                build();
        //latestRequestDto
        latestJpaRepository.save(entity);

        return 1;

/*
        //인서트 쿼리
        String sql = "INSERT INTO SMARTUX.PT_UX_LATEST ( SA_ID, MAC, CTN, REG_ID, CAT_ID, CAT_NAME, R_DATE, CATEGORY_GB ) VALUES ( \n" +
                ":saId, " +
                ":mac, " +
                ":ctn, " +
                ":regId, " +
                ":catId, " +
                ":catName, " +
                "now(), " +
                ":categoryGb)";

        Query nativeQuery = em.createNativeQuery(sql)
                .setParameter("saId",latestRequestDto.getSaId())
                .setParameter("mac",latestRequestDto.getMac())
                .setParameter("ctn",latestRequestDto.getCtn())
                .setParameter("regId",latestRequestDto.getRegId())
                .setParameter("catId",latestRequestDto.getCatId())
                .setParameter("catName",latestRequestDto.getCatName())
                .setParameter("categoryGb",latestRequestDto.getCategoryGb());

        return nativeQuery.executeUpdate();

*/

    }

}
