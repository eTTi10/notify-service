package com.lguplus.fleta.domain.repository;

import com.lguplus.fleta.data.dto.request.outer.MobileLatestRequestDto;
import com.lguplus.fleta.data.entity.MobileLatest;
import com.lguplus.fleta.provider.jpa.MobileLatestJpaRepository;
import com.lguplus.fleta.repository.latest.MobileLatestRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MobileLatestRepositoryImpl implements MobileLatestRepository {

    private final MobileLatestJpaRepository mobileLatestJpaRepository;


    @Override
    public List<String> getLatestCountList(MobileLatestRequestDto requestDto) {
        return mobileLatestJpaRepository.getLatestCountList(requestDto);
    }

    @Override
    public MobileLatest insertLatest(MobileLatestRequestDto requestDto) {
        MobileLatest entity = MobileLatest.builder()
            .saId(requestDto.getSaId())
            .mac(requestDto.getMac())
            .ctn(requestDto.getCtn())
            .catId(requestDto.getCategoryId())
            .regId(requestDto.getRegistrantId())
            .catName(requestDto.getCategoryName())
            .serviceType(requestDto.getServiceType())
            .build();

        return mobileLatestJpaRepository.saveAndFlush(entity);
    }

    @Override
    public int deleteLatest(MobileLatestRequestDto requestDto) {
        return mobileLatestJpaRepository.deleteLatest(requestDto);
    }
}
