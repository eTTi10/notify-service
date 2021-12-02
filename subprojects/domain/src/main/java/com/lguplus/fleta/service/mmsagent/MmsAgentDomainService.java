package com.lguplus.fleta.service.mmsagent;

import com.lguplus.fleta.client.CallSettingDomainClient;
import com.lguplus.fleta.config.MmsAgentConfig;
import com.lguplus.fleta.data.dto.request.MmsRequestDto;
import com.lguplus.fleta.data.dto.request.SendMmsRequestDto;
import com.lguplus.fleta.data.dto.request.inner.CallSettingRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingResultMapDto;
import com.lguplus.fleta.exception.mmsagent.NotFoundMsgException;
import com.lguplus.fleta.exception.smsagent.ServerSettingInfoException;
import com.lguplus.fleta.service.mmsagent.module.*;
import com.lguplus.fleta.service.mmsagent.module.content.BasicContent;
import com.lguplus.fleta.service.mmsagent.module.content.UplusContent;
import com.lguplus.fleta.service.mmsagent.module.inf.*;
import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MmsAgentDomainService {
    private final CallSettingDomainClient apiClient;
    private final MmsAgentConfig config;
    private Map<String, ?> mms;//yml파일 mms
    private Map<String, Object> setting;//yml파일 setting

    public SuccessResponseDto sendMmsCode(@NotNull SendMmsRequestDto sendMmsRequestDto) throws Exception {
        //============ yml설정파일 객체생성 ============
        mms = config.getMms();//1레벨 객체
        setting = (Map<String, Object>)config.getMms().get("setting");//2레벨 객체

        //============ Start [setting API 호출 캐시등록] =============

        //setting API 호출관련 파라메타 셋팅
        CallSettingRequestDto prm = CallSettingRequestDto.builder().build();//callSettingApi파라메타
        prm.setSaId((String)setting.get("rest_sa_id"));//ex) MMS:mms SMS:sms
        prm.setStbMac((String)setting.get("rest_stb_mac"));//ex) MMS:mms SMS:sms
        prm.setCodeId(sendMmsRequestDto.getMmsCd());//ex) M011
        prm.setSvcType((String)setting.get("rest_svc_type"));//ex) MMS:E SMS:I

        //setting API 호출하여 캐시에 메세지 등록후 출력
        CallSettingResultMapDto callSettingApi = apiClient.mmsCallSettingApi(prm);

        //메세지목록 조회결과 취득
        List<CallSettingDto> settingApiList =  callSettingApi.getResult().getRecordset();

        if(callSettingApi.getResult().getTotalCount() > 0) {
            CallSettingDto settingItem =  settingApiList.get(0);
            MmsRequestDto mmsDto = MmsRequestDto.builder().build();
            mmsDto.setCtn(sendMmsRequestDto.getCtn());
            mmsDto.setMmsTitle(prm.getCodeId());
            mmsDto.setMmsMsg(settingItem.getCodeName());//메세지
            mmsDto.setCtn(sendMmsRequestDto.getCtn());
            sendMMS(mmsDto);
        }else{
            returnError("1506");
        }
        return SuccessResponseDto.builder().build();
    }

    private void returnError(@NotNull String errorCode) throws Exception {
        log.error("{errorCode:"+errorCode+"}");
        switch (errorCode){
            case "1506":
                throw new NotFoundMsgException();//1506:해당 코드에 존재하는 메시지가 없음(조회한 메세지의 출력결과가 없을때)
            case "5200":
                throw new ServerSettingInfoException();//5200:서버 설정 정보 오류(서버주소정보가 없을때...)
            default :
                throw new Exception();//기타에러
        }
    }

    public void sendMMS(MmsRequestDto mmsDto) throws Exception {
        String url = (String)mms.get("server_url");
        if(StringUtils.isEmpty(url)){
            returnError("5200");
        }

        // Add Req Information
        SubmitReq submitReq = new SubmitReq();
        submitReq.setMm7NamespacePrefix((String)mms.get("prefix"));
        submitReq.setNamespace((String)mms.get("namespace"));
        submitReq.setMm7Version((String)mms.get("version"));

        // transactionID
        String reqDate = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date(System.currentTimeMillis()));
        int ranNum = (int)(Math.random() * (9999999 - 1000000 + 1)) + 1000000;//7자리 난수발생
        String transactionID = ranNum+"_"+reqDate;

        // Set submitReq Info
        submitReq.setTransactionId(transactionID);
        submitReq.setVaspId((String)mms.get("vaspid"));
        submitReq.setVasId((String)mms.get("vasid"));
        submitReq.setSenderAddress(new Address((String)mms.get("sender"),null,null));
        submitReq.setCallBackAddress(new Address(mms.get("callback").toString(),null,null));
        submitReq.addRecipient(new Address(mmsDto.getCtn(), Address.RecipientType.TO));
        submitReq.setServiceCode("0000");
        submitReq.setMessageClass(MessageClass.INFORMATIONAL);
        submitReq.setTimeStamp(new Date(System.currentTimeMillis()));
        submitReq.setEarlistDeliveryTime(new RelativeDate(new Date(System.currentTimeMillis())));
        submitReq.setDeliveryReport(false);
        submitReq.setReadReply(false);
        submitReq.setChargedParty(ChargedParty.SENDER);
        submitReq.setDistributionIndicator(false);
        submitReq.setSubject(mmsDto.getMmsTitle());//mms_cd에서 전달된값 ex) M011

        // Add Text Content
        UplusContent con1 = new UplusContent(
            MessageManager.convertMsg(mmsDto.getMmsMsg(), mmsDto.getMmsRep())
        );
        con1.setContentId("mm7-content-1");
        con1.setX_Kmms_SVCCODE("");
        con1.setX_Kmms_redistribution("");
        con1.setX_Kmms_TextInput("0");

        submitReq.setContent(new BasicContent(con1));


        try {
            if("Y".equals((String)mms.get("debug_mode"))) {
                MM7Message.save(submitReq, System.out, new MM7Context());
            }
        } catch (IOException e) {}

        MMSC mmsc = new BasicMMSC(url, mms);
        mmsc.getContext().setMm7Namespace((String)mms.get("namespace"));
        mmsc.getContext().setMm7Version((String)mms.get("version"));

        MM7Response rsp = mmsc.submit(submitReq);

        int statusCode = MM7Response.SC_SUCCESS;
        String statusText = "";
        //[ ASIS에 Swagger관련 코드가 있었지만 TOBE에서는 제외 ] SwaggerDefinition logger;...
    }


}
