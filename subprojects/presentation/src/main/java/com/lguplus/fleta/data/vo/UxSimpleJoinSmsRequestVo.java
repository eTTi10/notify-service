package com.lguplus.fleta.data.vo;

import com.lguplus.fleta.data.annotation.ParamAlias;
import com.lguplus.fleta.data.type.CarrierType;
import com.lguplus.fleta.data.type.DeviceInfo;
import com.lguplus.fleta.data.type.NetworkInfo;
import lombok.Getter;

@Getter
public class UxSimpleJoinSmsRequestVo {

    /**
     * 가입자 번호
     */
    @ParamAlias("sa_id")
    private String saId;

    /**
     * 가입자 맥주소
     */
    @ParamAlias("stb_mac")
    private String stbMac;

    /**
     * 전화번호
     */
    private String ctn;

    /**
     * 통합 통계용 서비스명
     */
    @ParamAlias("app_name")
    private String appName;

    /**
     * 통합 통계용 UI 버전
     */
    @ParamAlias("ui_version")
    private String uiVersion;

    /**
     * 통합 통계용 이전 페이지
     */
    @ParamAlias("pre_page")
    private String previousPage;

    /**
     * 통합 통계용 현재 페이지
     */
    @ParamAlias("cur_page")
    private String currentPage;

    /**
     * 통합 통계용 접속 단말 타입
     */
    @ParamAlias("dev_info")
    private DeviceInfo deviceInfo;

    /**
     * 통합 통계용 OS 정보
     */
    @ParamAlias("os_info")
    private String osInfo;

    /**
     * 통합 통계용 접속 네트워크 정보
     */
    @ParamAlias("nw_info")
    private NetworkInfo networkInfo;

    /**
     * 통합 통계용 단말 모델명
     */
    @ParamAlias("dev_model")
    private String deviceModel;

    /**
     * 통합 통계용 통신사 구분
     */
    @ParamAlias("carrier_type")
    private CarrierType carrierType;

}
