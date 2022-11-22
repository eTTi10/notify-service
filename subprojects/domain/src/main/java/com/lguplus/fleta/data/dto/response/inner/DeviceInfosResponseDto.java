package com.lguplus.fleta.data.dto.response.inner;

import com.lguplus.fleta.data.entity.DeviceInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceInfosResponseDto {

    @ApiModelProperty(position = 1, example = "1000000871", value = "가입자 번호")
    private String saId;

    @ApiModelProperty(position = 2, example = "H", value = "서비스타입(H:U+모바일TV)")
    private String serviceType;

    @ApiModelProperty(position = 3, example = "G", value = "단말(G:안드로이드, A:아이폰")
    private String agentType;

    @ApiModelProperty(position = 4, example = "G", value = "noti_type A:전체받기/ S:구독만받기 / N:푸시 안받기")
    private String notiType;

    public DeviceInfosResponseDto(DeviceInfo deviceInfo) {
        this.saId = deviceInfo.getSaId();
        this.serviceType = deviceInfo.getServiceType();
        this.agentType = deviceInfo.getAgentType();
        this.notiType = deviceInfo.getNotiType();
    }

}
