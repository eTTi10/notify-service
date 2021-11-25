package com.lguplus.fleta.client;

import com.lguplus.fleta.data.dto.request.inner.CallSettingRequestDto;

import java.util.List;
import java.util.Map;

public interface CallSettingDomainClient {
    public Map<String, Object> callSettingApi(CallSettingRequestDto prm);
}
