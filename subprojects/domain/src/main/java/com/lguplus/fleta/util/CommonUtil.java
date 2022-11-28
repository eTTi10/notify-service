package com.lguplus.fleta.util;

import io.micrometer.core.instrument.util.StringUtils;

public class CommonUtil {

    /**
     * P_KEY : 0:4,8,12월, 1:1,5,9월, 2:2,6,10월, 3:3,7,11월 (푸시발송예정일기준 4로 나눈 나머지)
     * @return Integer
     */
    public static Integer generatorPkey(String sendDt) {
        if (StringUtils.isNotBlank(sendDt) && sendDt.length() == 12) {
            String sendMonth = sendDt.substring(4, 6);
            return Integer.parseInt(sendMonth) % 4;
        }
        return null;
    }

}
