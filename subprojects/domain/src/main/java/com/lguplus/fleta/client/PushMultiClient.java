package com.lguplus.fleta.client;

import com.lguplus.fleta.data.dto.request.inner.PushRequestMultiSendDto;
import com.lguplus.fleta.data.dto.response.inner.PushMessageInfoDto;
import com.lguplus.fleta.data.dto.response.inner.PushMultiResponseDto;

/**
 * Push Multi Socket client
 *
 * 공지 푸시등록
 */
public interface PushMultiClient {

    String PUSH_COMMAND = "PUSH_NOTI";
    String LG_PUSH_OLD = "LGUPUSH_OLD";
    String REGIST_ID_NM = "[@RegistId]";
    String TRANSACT_ID_NM = "[@RegistId]";

    /**
     * Push Multi 전송
     *
     * @param dto Push Multi 정보
     * @return Push Multi 결과
     */
    PushMultiResponseDto requestPushMulti(PushRequestMultiSendDto dto);

    /**
     * Push 비동기 수신시 저장
     * @param dto
     */
    void receiveAsyncMessage(PushMessageInfoDto dto);

}
