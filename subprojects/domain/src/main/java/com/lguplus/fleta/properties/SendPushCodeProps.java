package com.lguplus.fleta.properties;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "push")
public class SendPushCodeProps {

    /**
     * send_code에 해당하는 push info
     */
    private List<Map<String, String>> sendCodes;

    /**
     * service_type에 해당하는 appid, serviceid
     */
    private List<Map<String, String>> serviceTargets;

    /**
     * server index 에 해당하는 객체를 찾는다.
     *
     * @param sendCode key가 되는 send_code
     * @return sendCode 에 해당하는 객체
     */
    public Optional<Map<String, String>> findMapBySendCode(String sendCode) {
        return sendCodes.stream().filter(m -> m.get("sendcode").equals(sendCode)).findFirst();
    }

    public Optional<Map<String, String>> findMapByServiceType(String serviceTarget) {
        return serviceTargets.stream().filter(m -> m.get("serviceTarget").equals(serviceTarget)).findFirst();
    }
}
