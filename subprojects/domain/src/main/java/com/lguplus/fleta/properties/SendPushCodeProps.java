package com.lguplus.fleta.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "pushinfo")
public class SendPushCodeProps {

    /** smsagent agent servers */
    private List<Map<String, String>> sendCodes;

    /**
     * server index 에 해당하는 객체를 찾는다.
     *
     * @param sendCode 서버 index
     * @return sendCode 에 해당하는 객체
     */
    public Optional<Map<String, String>> findMapBySendCode(String sendCode) {
        return sendCodes.stream().filter(m -> m.get("sendcode").equals(sendCode)).findFirst();
    }
}
