package com.lguplus.fleta.client;

import com.lguplus.fleta.data.dto.request.inner.CallSettingRequestDto;
import com.lguplus.fleta.data.dto.response.GenericRecordsetResponseDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingDto;

public interface CallSettingDomainClient {
    public GenericRecordsetResponseDto<CallSettingDto> callSettingApi(CallSettingRequestDto prm);
}
