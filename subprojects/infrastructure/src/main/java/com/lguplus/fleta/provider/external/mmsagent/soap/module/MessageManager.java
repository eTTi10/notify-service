package com.lguplus.fleta.provider.external.mmsagent.soap.module;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import org.springframework.util.StringUtils;

public class MessageManager {

    private final static String sep = "\\|";

    /**
     * 지정된 문자열로 변경하여 리턴한다.
     *
     * @param msg
     * @param replacement
     * @return
     */
    public static String convertMsg(String msg, String replacement) {
        if (StringUtils.hasLength(replacement)
            && StringUtils.hasLength(msg)) {
            try {
                replacement = URLDecoder.decode(replacement, StandardCharsets.UTF_8);

                String[] rep = replacement.split(sep);
                int i = 1;
                for (String t : rep) {
                    String repTxt = "{" + i + "}";
                    msg = msg.replace(repTxt, t);
                    i++;
                }
                return msg;
            } catch (Exception e) {
                return msg;
            }
        } else {
            return msg;
        }
    }
}
