package com.lguplus.fleta.rest.mmsagent;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

@FeignClient(name = "mms-agent-rest-message", url = "http://hdtv.suxm.uplus.co.kr")//${mms.setting.url}
public interface MMSAgentRestMessage {
    /**
     * 업비스 마켓코드 조회(파라메타가 없는 GET방식 샘플)
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/hdtv/comm/setting?sa_id=mms&stb_mac=mms&code_id=&svc_type=E", produces = "application/json")
    public List<Map<String, Object>> upbitMarketAll();
}
