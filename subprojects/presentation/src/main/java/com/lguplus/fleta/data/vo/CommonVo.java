package com.lguplus.fleta.data.vo;

import com.lguplus.fleta.data.annotation.ParamAlias;
import com.lguplus.fleta.data.dto.request.CommonRequestDto;
import com.lguplus.fleta.data.type.CarrierType;
import com.lguplus.fleta.data.type.DeviceInfo;
import com.lguplus.fleta.data.type.NetworkInfo;
import com.lguplus.fleta.validation.Groups;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Builder
@Getter
public class CommonVo {

	@NotBlank(message = "sa_id 파라미터값이 전달이 안됨", groups = Groups.R1.class)
	@ParamAlias("sa_id")
	private String saId;

	@NotBlank(message = "stb_mac 파라미터값이 전달이 안됨", groups = Groups.R2.class)
	@ParamAlias("stb_mac")
	private String stbMac;

	@ParamAlias("app_type")
	private String appType;

	@ParamAlias("app_name")
	private String appName;

	@ParamAlias("ui_version")
	private String uiVersion;

	@ParamAlias("pre_page")
	private String previousPage;

	@ParamAlias("cur_page")
	private String currentPage;

	@ParamAlias("dev_info")
	private DeviceInfo deviceInfo;

	@ParamAlias("os_info")
	private String osInfo;

	@ParamAlias("nw_info")
	private NetworkInfo networkInfo;

	@ParamAlias("dev_model")
	private String deviceModel;

	@ParamAlias("carrier_type")
	private CarrierType carrierType;

	public CommonRequestDto convert() {
		return CommonRequestDto.builder()
				.saId(getSaId())
				.appType(getAppType())
				.stbMac(getStbMac())
				.appName(getAppName())
				.uiVersion(getUiVersion())
				.previousPage(getPreviousPage())
				.currentPage(getCurrentPage())
				.deviceInfo(getDeviceInfo())
				.osInfo(getOsInfo())
				.networkInfo(getNetworkInfo())
				.deviceModel(getDeviceModel())
				.carrierType(getCarrierType())
				.build();
	}
}