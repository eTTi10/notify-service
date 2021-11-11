package com.lguplus.fleta.service.latest;

import com.lguplus.fleta.data.dto.LatestDto;
import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto;
import com.lguplus.fleta.data.entity.LatestEntity;
import com.lguplus.fleta.repository.LatestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class LatestDomainService {

    private final LatestRepository latestRepository;


    /**
     * 최신회 정보조회
     * @param latestRequestDto  최신회 정보조회를 위한 DTO
     * @return 최신회 정보조회 결과
     */
    public List<LatestDto> getLatest(LatestRequestDto latestRequestDto) {
        return latestRepository.getLatest(latestRequestDto);
    }

    /**
     * 최신회 정보삭제
     * @param latestRequestDto 최신회 정보삭제를 위한 DTO
     * @return 삭제건수
     */
    public int deleteLatest(LatestRequestDto latestRequestDto) {
        return latestRepository.deleteLatest(latestRequestDto);
    }

    /**
     * 최신회 정보등록
     * @param latestRequestDto 최신회 정보등록을 위한 DTO
     * @return 등록건수
     */
    public int insertLatest(LatestRequestDto latestRequestDto) {
        return latestRepository.insertLatest(latestRequestDto);
    }
}
