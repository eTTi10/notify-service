package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.data.dto.response.inner.InnerResponseDto;

public class CommonDomainFeinClient {

    protected <T> T getResult(final InnerResponseDto<T> response) {

        return response.getResult().getData();
    }
}
