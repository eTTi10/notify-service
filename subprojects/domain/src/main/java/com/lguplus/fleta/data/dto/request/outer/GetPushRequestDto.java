package com.lguplus.fleta.data.dto.request.outer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@ToString
public class GetPushRequestDto {

    private String saId; // 가입자정보
    private String stbMac; // 가입자 STB MAC Address
    private String albumId; // 앨범 ID
    private String serviceType; // 푸시 서비스 타입


    public enum SERVICE_TYPE {
        /**
         * 뮤직공연 (C)
         */
        MUSIC_SHOW("C")
        ;
        public final String CODE;

        private SERVICE_TYPE(String code) {
            this.CODE = code;
        }
    }
}
