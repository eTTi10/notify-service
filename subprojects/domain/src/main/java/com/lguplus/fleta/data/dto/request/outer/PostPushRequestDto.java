package com.lguplus.fleta.data.dto.request.outer;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostPushRequestDto extends PushRequestDto {

    private String saId; // 가입자정보
    private String stbMac; // 가입자 STB MAC Address
    private String albumId; // 앨범 ID
    private String serviceType;
    private String categoryId;  // 카테고리 ID
    private String msg;         // 보낼메시지(타이틀명)
    private String sendDt;      // 공연시작일(푸시발송예정일)
    private String pushYn;

    public enum CONST {
        /**
         * 푸시 등록
         */
        REG("Y"),
        /**
         * 푸시등록 해제
         */
        RELEASE("N"),
        ;
        public final String CODE;

        private CONST(String code) {
            this.CODE = code;
        }
    }

    /**
     * P_KEY : 0:4,8,12월, 1:1,5,9월, 2:2,6,10월, 3:3,7,11월 (푸시발송예정일기준 4로 나눈 나머지)
     * @return Integer
     */
    public Integer generatorPkey() {
        if (StringUtils.isNotBlank(this.sendDt) && this.sendDt.length() == 12) {
            String p_key = this.sendDt.substring(4, 6);
            Integer ipkey = Integer.parseInt(p_key) % 4;
            return ipkey;
        }
        return null;
    }

}
