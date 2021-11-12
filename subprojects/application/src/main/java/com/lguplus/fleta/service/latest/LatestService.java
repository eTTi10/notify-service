package com.lguplus.fleta.service.latest;

import com.lguplus.fleta.data.dto.LatestDto;
import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto;
import com.lguplus.fleta.data.dto.response.GenericRecordsetResponseDto;
import com.lguplus.fleta.data.entity.LatestEntity;
import io.reactivex.rxjava3.core.Single;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class LatestService {

    private final LatestDomainService latestDomainService;

    public  GenericRecordsetResponseDto<LatestDto> getLatestList(LatestRequestDto latestRequestDto) {
        List<LatestDto> result = latestDomainService.getLatestList(latestRequestDto);

        return GenericRecordsetResponseDto.<LatestDto>genericRecordsetResponseBuilder()
                .totalCount(result.size())
                .recordset(result)
                .build();
    }

    public  int deleteLatest(LatestRequestDto latestRequestDto) {
        int result = latestDomainService.deleteLatest(latestRequestDto);
        return result;
    }

    /**
     * 최신회 리스트의 최대등록값&중복체크 결과를 반환
     * @param latestRequestDto
     * @return [DUPL:중복, "OVER:최대값초과", "OK"]
     */
    public String getLatestCheckList(LatestRequestDto latestRequestDto) {
        List<LatestDto> checkList = latestDomainService.getLatestCheckList(latestRequestDto);

        int maxCount = 5;//yml 속성에서 가져올 것 latest.maxCount = 5 속성에 값이 없다면 기본은 5

        if(maxCount < checkList.size())return "OVER";//최대값 초과

        //개선된 for문으로 교체할것
        for (int i = 0; i < checkList.size(); i++) {
            String catId = checkList.get(i).getCatId();
            if (catId.equals(latestRequestDto.getCatId())) {
                return "DUPL";//중복
            }
        }
        return "OK";
    }

    public  int insertLatest(LatestRequestDto latestRequestDto) {
        int result = latestDomainService.insertLatest(latestRequestDto);
        return result;
    }
}
