package com.lguplus.fleta.data.dto.request.outer;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PushRequestDto {

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
}
