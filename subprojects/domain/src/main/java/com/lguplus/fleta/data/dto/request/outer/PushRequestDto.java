package com.lguplus.fleta.data.dto.request.outer;

import lombok.Getter;

@Getter
public class PushRequestDto {

    private String saId; // 가입자정보
    private String stbMac; // 가입자 STB MAC Address
    private String albumId; // 앨범 ID
    private String serviceType; // 푸시 서비스 타입

}
