package com.lguplus.fleta.service.mmsagent;

import com.lguplus.fleta.client.CallSettingDomainClient;
import com.lguplus.fleta.client.MmsAgentDomainClient;
import com.lguplus.fleta.config.MmsAgentConfig;
import com.lguplus.fleta.data.dto.request.MmsRequestDto;
import com.lguplus.fleta.data.dto.request.SendMmsRequestDto;
import com.lguplus.fleta.data.dto.request.inner.CallSettingRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingResultMapDto;
import com.lguplus.fleta.exception.NoResultException;
import com.lguplus.fleta.exception.mmsagent.NumberFormatException;
import com.lguplus.fleta.exception.mmsagent.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
//커밋3
@Slf4j
@Component
@RequiredArgsConstructor
/**
 * ################### 개발중 입니다. 잠시 개발 중단 상태입니다. 리뷰대상이 아닙니다. #####################
 */
public class MmsAgentDomainService {
    private final CallSettingDomainClient apiClient;
    private final MmsAgentConfig config;
    private final MmsAgentDomainClient mmsSoap;

    private Map<String, ?> mmsConfig;//yml파일 mms
    private Map<String, Object> settingConfig;//yml파일 setting


    /**
     * 전송메세지를 취득후 MM7모듈함수를 실행
     * @param sendMmsRequestDto
     * @return
     */
    public SuccessResponseDto sendMmsCode(SendMmsRequestDto sendMmsRequestDto) {
        //yml설정파일 객체생성
        mmsConfig = config.getMms();//1레벨 객체
        settingConfig = (Map<String, Object>)config.getMms().get("setting");//2레벨 객체

        //setting API 호출관련 파라메타 셋팅
        CallSettingRequestDto prm = CallSettingRequestDto.builder()
                .saId((String)settingConfig.get("rest_sa_id"))//ex) MMS:mms SMS:sms
                .stbMac((String)settingConfig.get("rest_stb_mac"))//ex) MMS:mms SMS:sms
                .codeId(sendMmsRequestDto.getMmsCd())//ex) M011
                .svcType((String)settingConfig.get("rest_svc_type"))//ex) MMS:E SMS:I
                .build();

        CallSettingResultMapDto callSettingApi = apiClient.mmsCallSettingApi(prm);
        List<CallSettingDto> settingApiList =  callSettingApi.getResult().getRecordset();
        CallSettingDto settingItem = null;

        if(settingApiList == null){
            throw new BlackListException();//1506:
        }

        if(settingApiList.isEmpty()) {
            throw new BlackListException();//1506:
        }
        settingItem =  settingApiList.get(0);


        MmsRequestDto mmsDto = MmsRequestDto.builder()
                .ctn(sendMmsRequestDto.getCtn())
                .mmsTitle(sendMmsRequestDto.getMmsCd())
                .mmsMsg(settingItem.getCodeName())//메세지
                .mmsRep(sendMmsRequestDto.getReplacement())
                .build();
        String returnMmsCode = mmsSoap.sendMMS(mmsConfig, mmsDto);

        if(!returnMmsCode.equals("1000")){
            switch (returnMmsCode){
                case "0001":
                    throw new NoResultException();//검색 결과 없음
                case "1500":
                    throw new SystemErrorException();//시스템 장애
                case "1501":
                    throw new MsgTypeErrorException();//메시지 형식 오류
                case "1502":
                    throw new PhoneNumberErrorException();//전화번호 형식 오류
                case "1503":
                    throw new SystemBusyException();//메시지 처리 수용 한계 초과
                case "1504":
                    throw new NotSendTimeException();//
                case "1505":
                    throw new BlackListException();//1506:해당 코드에 존재하는 메시지가 없음(조회한 메세지의 출력결과가 없을때)
                case "1506":
                    throw new NotFoundMsgException();//해당 코드에 존재하는 메시지가 없음
                case "5000":
                    throw new ParameterMissingException();//파라미터값이 전달이 안됨
                case "5001":
                    throw new NumberFormatException();//파라미터는 숫자형 데이터이어야 함
                case "5101":
                    throw new MessageSocketException();//소켓 에러
                case "5200":
                    throw new ServerSettingInfoException();//5200:서버 설정 정보 오류(서버주소정보가 없을때...)
                case "5400":
                    throw new NoHttpsException();//HTTPS 통신이 아닙니다.
                case "8000":
                    throw new DuplicateKeyException();//중복 키 에러
                case "8999":
                    throw new DatabaseException();//DB 에러
                case "9998":
                    throw new MmsServiceException();//MM7 Service Error
                default :
                    throw new MmsRuntimeException();
            }
        }
        return SuccessResponseDto.builder().build();
    }



}
