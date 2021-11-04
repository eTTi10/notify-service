package com.lguplus.fleta.data.dto.request;

import com.lguplus.fleta.data.type.CarrierType;
import com.lguplus.fleta.data.type.DeviceInfo;
import com.lguplus.fleta.data.type.NetworkInfo;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class CommonRequestDto {

	private String saId;

	private String stbMac;

	private String appType;

	private String appName;

	private String uiVersion;

	private String previousPage;

	private String currentPage;

	private DeviceInfo deviceInfo;

	private String osInfo;

	private NetworkInfo networkInfo;

	private String deviceModel;

	private CarrierType carrierType;

}