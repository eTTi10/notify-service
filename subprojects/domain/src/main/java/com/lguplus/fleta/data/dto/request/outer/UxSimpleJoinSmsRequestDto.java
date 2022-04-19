package com.lguplus.fleta.data.dto.request.outer;

import com.lguplus.fleta.data.type.CarrierType;
import com.lguplus.fleta.data.type.DeviceInfo;
import com.lguplus.fleta.data.type.NetworkInfo;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@ToString
public class UxSimpleJoinSmsRequestDto {

    /** 가입자 번호 */
    private String saId;

    /** 가입자 맥주소 */
    private String stbMac;

    /** 전화번호 */
    private String ctn;

    /** 통합 통계용 서비스명 */
    private String appName;

    /** 통합 통계용 UI 버전 */
    private String uiVersion;

    /** 통합 통계용 이전 페이지 */
    private String previousPage;

    /** 통합 통계용 현재 페이지 */
    private String currentPage;

    /** 통합 통계용 접속 단말 타입 */
    private DeviceInfo deviceInfo;

    /** 통합 통계용 OS 정보 */
    private String osInfo;

    /** 통합 통계용 접속 네트워크 정보 */
    private NetworkInfo networkInfo;

    /** 통합 통계용 단말 모델명 */
    private String deviceModel;

    /** 통합 통계용 통신사 구분 */
    private CarrierType carrierType;

}
