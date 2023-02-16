package com.lguplus.fleta.client;

import com.lguplus.fleta.data.dto.request.MmsRequestDto;
import java.util.Map;

/**
 * ################### 개발중 입니다. 잠시 개발 중단 상태입니다. 리뷰대상이 아닙니다. #####################
 */
public interface MmsAgentClient {

    String sendMMS(Map<String, ?> mmsConfig, MmsRequestDto mmsDto);
    String sendMMS(Map<String, ?> mmsConfig,Map<String, String> mapServers, MmsRequestDto mmsDto);

}
