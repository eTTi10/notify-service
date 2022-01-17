package com.lguplus.fleta.provider.jpa.latest;

import com.lguplus.fleta.data.entity.LatestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LatestJpaRepositoryImpl extends JpaRepository<LatestEntity,String> {
    List<LatestEntity> findBySaIdAndMacAndCtnAndCatIdOrCatIdIsNull(String saId, String mac, String ctn, String catId);

    List<LatestEntity> findBySaIdAndMacAndCtn(String saId, String mac, String ctn);

    int deleteBySaIdAndMacAndCtnAndCatId(String saId, String mac, String ctn, String catId);
}
