package com.lguplus.fleta.client;

import com.lguplus.fleta.data.dto.request.inner.CallSettingRequestDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingResultMapDto;

public interface SettingDomainClient {
    CallSettingResultMapDto callSettingApi(CallSettingRequestDto prm);
}
