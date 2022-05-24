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
@ConfigurationProperties(prefix = "sms.agent")
public class SmsAgentProps {

    /** smsagent agent servers */
   private List<Map<String, String>> servers;

    /**
     * server index 에 해당하는 객체를 찾는다.
     *
     * @param index 서버 index
     * @return index 에 해당하는 객체
     */
   public Optional<Map<String, String>> findMapByIndex(String index) {
       return servers.stream().filter(m -> m.get("index").equals(index)).findFirst();
   }

   public int getCount(){
       return servers.size();
   }

}
