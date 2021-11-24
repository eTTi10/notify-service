package com.lguplus.fleta.exception;

import com.lguplus.fleta.data.dto.response.inner.InnerResponseDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class ClientException extends RuntimeException {

    private final int status;
    private final String reason;
    private final Map<String, Collection<String>> headers;
    private final InnerResponseDto<?> body;

    public boolean hasBody() {
        return body != null;
    }

}
