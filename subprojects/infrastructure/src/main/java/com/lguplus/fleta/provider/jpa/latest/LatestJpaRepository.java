package com.lguplus.fleta.provider.jpa.latest;

import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto;
import com.lguplus.fleta.data.entity.LatestCheckEntity;
import com.lguplus.fleta.data.entity.LatestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.Query;
import java.util.List;

public interface LatestJpaRepository extends JpaRepository<LatestEntity,String> {
    List<LatestEntity> findBySaIdAndMacAndCtnAndCatIdOrCatIdIsNull(String saId, String mac, String ctn, String catId);
    /*
    public List<LatestCheckEntity> getLatestCheckList(LatestRequestDto latestRequestDto) {
    String sql = "SELECT SA_ID, MAC, CTN, CAT_ID \n" +
            "FROM SMARTUX.PT_UX_LATEST \n" +
            "WHERE  SA_ID = :saId " +
            " AND  MAC = :mac " +
            " AND  CTN = :ctn ";

        return em.createNativeQuery(sql, LatestCheckEntity .class)
            .setParameter("saId",latestRequestDto.getSaId())
            .setParameter("mac",latestRequestDto.getMac())
            .setParameter("ctn",latestRequestDto.getCtn())
            .getResultList();

     */
    List<LatestEntity> findBySaIdAndMacAndCtn(String saId, String mac, String ctn);
    /*
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
                .setParameter(catIdStr,latestRequestDto.getCatId())
                ;
        return nativeQuery.executeUpdate();
    }

     */
    int deleteBySaIdAndMacAndCtnAndCatId(String saId, String mac, String ctn, String catId);



}
