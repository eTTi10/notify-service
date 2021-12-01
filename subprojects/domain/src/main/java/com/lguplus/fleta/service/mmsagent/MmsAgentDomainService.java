package com.lguplus.fleta.service.mmsagent;

import com.lguplus.fleta.client.CallSettingDomainClient;
import com.lguplus.fleta.config.MmsAgentConfig;
import com.lguplus.fleta.data.dto.request.MmsRequestDto;
import com.lguplus.fleta.data.dto.request.SendMmsRequestDto;
import com.lguplus.fleta.data.dto.request.inner.CallSettingRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingResultMapDto;
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
import java.util.Random;

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

        //setting API 호출하여 메세지 등록
        CallSettingResultMapDto callSettingApi = apiClient.mmsCallSettingApi(prm);

        // Send a message
        String logStr = "\n [Start] ############## callSettingApi로 FeignClient 메세지목록 호출 ############## \n";
        logStr += "\n [ 출발지 : MmsAgentDomainService.sendMmsCode ] \n";
        logStr += "\n [ 도착지 : CallSettingDomainFeignClient.callSettingApi ] \n";
        logStr += "\n [ 요청주소 : "+(String)setting.get("rest_url")+(String)setting.get("rest_path")+" ] \n";
        logStr += "\n [ 매개변수 : " + prm.toString() + " ] \n";
        logStr += "\n ------------- Start 매개변수 가이드 -------------\n";
        logStr += "\n * sa_id:가입자정보 \n";
        logStr += "\n * stb_mac:가입자 STB MAC Address \n";
        logStr += "\n * ctn:발송대상 번호 \n";
        logStr += "\n * replacement:치환문자 \n";
        logStr += "\n * mms_cd:MMS 메시지 코드 \n";
        logStr += "\n   - M001 : 모바일tv 앱 설치안내 문자 \n";
        logStr += "\n   - M002 : 프로야구 앱 설치안내 문자 \n";
        logStr += "\n   - M003 : 아이들나라 앱 설치안내 문자 \n";
        logStr += "\n   - M004 : 골프 앱 설치안내 문자 \n";
        logStr += "\n   - M005 : 아이돌Live 앱 설치안내 문자 \n";
        logStr += "\n ------------- End 매개변수 가이드 -------------\n\n";
        logStr += "\n [ 리턴결과 : " + callSettingApi.toString() + " ] \n";
        logStr += "\n [End] ##############  ############## callSettingApi로 FeignClient 메세지목록 호출 ############## \n\n";
        log.info(logStr);
        //메세지목록 조회결과 취득
        List<CallSettingDto> settingApiList =  callSettingApi.getResult().getRecordset();

        //============ End [setting API 호출 캐시등록] =============

        if(callSettingApi.getResult().getTotalCount() > 0) {
            CallSettingDto settingItem =  settingApiList.get(0);
            MmsRequestDto mmsDto = MmsRequestDto.builder().build();
            mmsDto.setCtn(sendMmsRequestDto.getCtn());
            mmsDto.setMmsTitle((String)setting.get("rest_code_id"));
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
                throw new Exception("{flag.notfound.msg:1506, message.notfound.msg:해당 코드에 존재하는 메시지가 없음}");
            case "5200":
                throw new ServerSettingInfoException("서버 설정 정보 오류");
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
        int ranNum = generateNumber(7);
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
            if("Y".equals((String)mms.get("debug_mode"))) MM7Message.save(submitReq, System.out, new MM7Context());
        } catch (IOException e) {}

        MMSC mmsc = new BasicMMSC(url);
        mmsc.getContext().setMm7Namespace((String)mms.get("namespace"));
        mmsc.getContext().setMm7Version((String)mms.get("version"));

        String logStr = "\n [Start] MM7 ############## MMS처리모듈 : MMSCBase implements MMSC ##############\n";
        logStr += "\n [ mmsc.submit(submitReq)에서 (mmsDto.mmsMsg)실제 메세지내용을 사용한 흔적을 찾을수 없음 \n";
        logStr += "\n [ MmsRequestDto mmsDto : " + mmsDto.toString() + "] \n";
        logStr += "\n [ transactionID : " + transactionID + "] \n"; //ex) 9515127_20211128182939057
        logStr += "\n [ MMS처리서버 : " + (String)mms.get("server_url") +" ] \n";
        logStr += "\n 1) [ public MM7Response submit(SubmitReq submitReq) throws MM7Error ] XML형태의 파라메타 SubmitReq로 함수호출 \n";
        logStr += "\n    MM7Response rsp = post(submitReq) 클래스 내부의 post함수 호출 \n";
        logStr += "\n 2) [ private MM7Response post(MM7Request request) throws MM7Error] XML형태의 submitReq파라메타를 request로 넘겨주고 post 함수호출 \n";
        logStr += "\n    이후 프로세스 점검중...MM7문서와 전문비교 확인중... \n";
        logStr += "\n [End] MM7 ############## MMS처리모듈 : MMSCBase implements MMSC ##############\n";
        log.info(logStr);
        log.info("\n"+submitReq.toString()+"\n");

        MM7Response rsp = mmsc.submit(submitReq);

        int statusCode = MM7Response.SC_SUCCESS;
        String statusText = "";
        //[ ASIS에 Swagger관련 코드가 있었지만 TOBE에서는 제외 ] SwaggerDefinition logger;...
    }

    /**
     * 1~9 자리 난수 발생 [ASIS => mmsagent\common\CommonUtil.java generateNumber
     * @param length
     * @return
     */
    private int generateNumber(int length) {
        String numStr = "1";
        String plusNumStr = "1";
        for (int i = 0; i < length; i++) {
            numStr += "0";

            if (i != length - 1) {
                plusNumStr += "0";
            }
        }
        Random random = new Random();
        int result = random.nextInt(Integer.parseInt(numStr)) + Integer.parseInt(plusNumStr);

        if (result > Integer.parseInt(numStr)) {
            result = result - Integer.parseInt(plusNumStr);
        }
        return result;
    }

}
