package com.lguplus.fleta.provider.jpa;

import com.lguplus.fleta.data.entity.LatestEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LatestJpaRepository extends JpaRepository<LatestEntity, String> {

    List<LatestEntity> findBySaIdAndMacAndCtnAndCatIdOrCatIdIsNull(String saId, String mac, String ctn, String catId);

    List<LatestEntity> findBySaIdAndMacAndCtn(String saId, String mac, String ctn);

    int deleteBySaIdAndMacAndCtnAndCatId(String saId, String mac, String ctn, String catId);
}
