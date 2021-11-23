package com.lguplus.fleta.service.smsagent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmsMessageManagerDomainService {

    private final static String sep = "\\|";

    /**
     * 지정된 문자열로 변경하여 리턴한다.
     * @param msg
     * @param replacement
     * @return
     */
    public static String convertMsg(String msg, String replacement){
        if(StringUtils.isEmpty(replacement)) return msg;
        else{
            String[] rep = replacement.split(sep);
            int i = 1;
            for(String t : rep){
                String repTxt = "{" + i + "}";
                msg = msg.replace(repTxt, t);
                i++;
            }
            return msg;
        }
    }

}
