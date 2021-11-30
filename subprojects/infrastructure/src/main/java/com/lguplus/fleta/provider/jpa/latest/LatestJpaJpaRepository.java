package com.lguplus.fleta.provider.jpa.latest;

import com.lguplus.fleta.data.dto.LatestDto;
import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto;
import com.lguplus.fleta.data.entity.LatestCheckEntity;
import com.lguplus.fleta.data.entity.LatestEntity;
import com.lguplus.fleta.repository.LatestRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class LatestJpaJpaRepository implements LatestRepository {

    @PersistenceContext
    private EntityManager em;

    private String sqlStr(String value){
        return "'"+value+"'";
    }

    /**
     * 최신회 리스트 조회
     * @param latestRequestDto
     * @return
     */
    @Override
    public List<LatestDto> getLatestList(LatestRequestDto latestRequestDto) {
        String sql = "SELECT SA_ID, MAC, CTN, REG_ID, CAT_ID, CAT_NAME, R_DATE, CATEGORY_GB \n" +
                "FROM SMARTUX.PT_UX_LATEST \n" +
                "WHERE  SA_ID = "+sqlStr(latestRequestDto.getSaId())+" " +
                " AND  MAC = "+sqlStr(latestRequestDto.getMac())+" " +
                " AND  CTN = "+sqlStr(latestRequestDto.getCtn())+" ";
        if(!StringUtils.isEmpty(latestRequestDto.getCatId())) {
            sql += " AND CAT_ID = "+sqlStr(latestRequestDto.getCatId())+" ";
        }
        sql += "ORDER BY SA_ID, MAC, CTN";

        List<LatestEntity> rs = (List<LatestEntity>) em.createNativeQuery(sql, LatestEntity.class).getResultList();

        List<LatestDto> resultList = new ArrayList<LatestDto>();

        rs.forEach(e->{
            LatestDto item = LatestDto.builder().build();
            item.setSaId(e.getSaId());
            item.setMac(e.getCatId());
            item.setCtn(e.getCtn());
            item.setCatId(e.getCatId());
            item.setCatName(e.getCatName());
            item.setRDate(e.getRDate());
            item.setCategoryGb(e.getCategoryGb());
            resultList.add(item);
        });
        return resultList;
    }

    /**
     * 최신회 체크리스트 조회
     * @param latestRequestDto
     * @return
     */
    @Override
    public List<LatestDto> getLatestCheckList(LatestRequestDto latestRequestDto) {
        String sql = "SELECT SA_ID, MAC, CTN, CAT_ID \n" +
                "FROM SMARTUX.PT_UX_LATEST \n" +
                "WHERE  SA_ID = :saId " +
                " AND  MAC = :mac " +
                " AND  CTN = :ctn ";

        List<LatestCheckEntity> rs = (List<LatestCheckEntity>) em.createNativeQuery(sql, LatestCheckEntity.class)
                .setParameter("saId",latestRequestDto.getSaId())
                .setParameter("mac",latestRequestDto.getMac())
                .setParameter("ctn",latestRequestDto.getCtn())
                .getResultList();
        List<LatestDto> resultList = new ArrayList<LatestDto>();

        rs.forEach(e->{
            LatestDto item = LatestDto.builder().build();
            item.setCatId(e.getCatId());
            resultList.add(item);
        });
        return resultList;
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
        String sql = "DELETE FROM SMARTUX.PT_UX_LATEST \n" +
                "WHERE SA_ID = :saId \n" +
                " AND MAC = :mac " +
                " AND CTN = :ctn \n" +
                " AND CAT_ID = :catId \n";

        Query nativeQuery = em.createNativeQuery(sql)
                .setParameter("saId",latestRequestDto.getSaId())
                .setParameter("mac",latestRequestDto.getMac())
                .setParameter("ctn",latestRequestDto.getCtn())
                .setParameter("catId",latestRequestDto.getCatId())
                ;
        int execCnt = nativeQuery.executeUpdate();
        return execCnt;
    }



    /**
     * 최신회 등록
     * @param latestRequestDto
     * @return
     */
    @Override
    @Modifying
    @Transactional
    public int insertLatest(LatestRequestDto latestRequestDto) {
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

        int execCnt = nativeQuery.executeUpdate();
        return execCnt;

    }

}
