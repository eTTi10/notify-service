package com.lguplus.fleta.service.latest;

import com.lguplus.fleta.data.dto.request.outer.MobileLatestRequestDto;
import com.lguplus.fleta.exception.ExceedMaxRequestException;
import com.lguplus.fleta.exception.UndefinedException;
import com.lguplus.fleta.exception.database.DataAlreadyExistsException;
import com.lguplus.fleta.exception.latest.DeleteNotFoundException;
import com.lguplus.fleta.repository.latest.MobileLatestRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MobileLatestDomainService {


    private static final int MAX_COUNT = 5;

    private final MobileLatestRepository mobileLatestRepository;

    public void deleteLatest(MobileLatestRequestDto requestDto) {

        int deleteCount = mobileLatestRepository.deleteLatest(requestDto);

        if (deleteCount <= 0) {
            throw new DeleteNotFoundException("삭제대상 없음");
        }
    }

    public void insertLatest(MobileLatestRequestDto requestDto) {

        checkLatestCountList(requestDto);

        try {
            mobileLatestRepository.insertLatest(requestDto);
        } catch (Exception e) {
            throw new UndefinedException();
        }
    }

    private void checkLatestCountList(MobileLatestRequestDto requestDto) {

        List<String> latestCountList = mobileLatestRepository.getLatestCountList(requestDto);

        if (MAX_COUNT <= latestCountList.size()) {
            throw new ExceedMaxRequestException("최대 등록 갯수 초과");
        }
        if (latestCountList.stream().anyMatch(categoryId -> categoryId.equals(requestDto.getCategoryId()))) {
            throw new DataAlreadyExistsException("기존 데이터 존재");
        }
    }
}
