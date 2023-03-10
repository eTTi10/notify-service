package com.lguplus.fleta.provider.jpa;

import com.lguplus.fleta.data.dto.request.outer.MobileLatestRequestDto;
import com.lguplus.fleta.data.entity.MobileLatest;
import com.lguplus.fleta.data.entity.id.LatestId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MobileLatestJpaRepository extends JpaRepository<MobileLatest, LatestId> {

    @Modifying(clearAutomatically = true)
    @Query(value = "SELECT latest.catId FROM MobileLatest latest "
        + "WHERE latest.saId = :#{#requestDto.saId} "
        + "AND latest.mac = :#{#requestDto.mac} "
        + "AND latest.serviceType = :#{#requestDto.serviceType} "
        + "GROUP BY latest.catId")
    List<String> getLatestCountList(@Param("requestDto") MobileLatestRequestDto requestDto);

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM MobileLatest latest "
        + "WHERE latest.saId = :#{#requestDto.getSaId} "
        + "AND latest.mac = :#{#requestDto.mac} "
        + "AND latest.catId = :#{#requestDto.categoryId} "
        + "AND latest.serviceType = :#{#requestDto.serviceType} ")
    int deleteLatest(@Param("requestDto") MobileLatestRequestDto requestDto);
}
