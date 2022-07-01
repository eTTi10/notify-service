package com.lguplus.fleta.domain.repository;

import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto;
import com.lguplus.fleta.data.entity.LatestEntity;
import com.lguplus.fleta.provider.jpa.LatestJpaRepository;
import com.lguplus.fleta.repository.latest.LatestRepository;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LatestRepositoryImpl implements LatestRepository {

    private final LatestJpaRepository latestJpaRepository;

    @Override
    public List<LatestEntity> getLatestList(LatestRequestDto latestRequestDto) {
        return latestJpaRepository.findBySaIdAndMacAndCtnAndCatIdOrCatIdIsNull(
            latestRequestDto.getSaId(),
            latestRequestDto.getMac(),
            latestRequestDto.getCtn(),
            latestRequestDto.getCatId()
        );
    }


    @Override
    public List<LatestEntity> getLatestCheckList(LatestRequestDto latestRequestDto) {
        return latestJpaRepository.findBySaIdAndMacAndCtn(
            latestRequestDto.getSaId(),
            latestRequestDto.getMac(),
            latestRequestDto.getCtn()
        );
    }

    @Override
    public int deleteLatest(LatestRequestDto latestRequestDto) {
        return latestJpaRepository.deleteBySaIdAndMacAndCtnAndCatId(
            latestRequestDto.getSaId(),
            latestRequestDto.getMac(),
            latestRequestDto.getCtn(),
            latestRequestDto.getCatId()
        );
    }

    @Override
    public LatestEntity insertLatest(LatestRequestDto latestRequestDto) {
        LatestEntity entity = LatestEntity.builder().
            saId(latestRequestDto.getSaId()).
            mac(latestRequestDto.getMac()).
            ctn(latestRequestDto.getCtn()).
            regId(latestRequestDto.getRegId()).
            catId(latestRequestDto.getCatId()).
            catName(latestRequestDto.getCatName()).
            rDate(new Date()).
            categoryGb(latestRequestDto.getCategoryGb()).
            build();
        return latestJpaRepository.save(entity);
    }

}
