package com.lguplus.fleta.provider.jpa;

import com.lguplus.fleta.data.entity.Latest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LatestJpaRepository extends JpaRepository<Latest, String> {

    List<Latest> findBySaIdAndMacAndCtnAndCatIdOrCatIdIsNull(String saId, String mac, String ctn, String catId);

    List<Latest> findBySaIdAndMacAndCtn(String saId, String mac, String ctn);

    int deleteBySaIdAndMacAndCtnAndCatId(String saId, String mac, String ctn, String catId);
}
