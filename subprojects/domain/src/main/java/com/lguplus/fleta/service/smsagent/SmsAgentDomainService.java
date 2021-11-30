package com.lguplus.fleta.service.smsagent;

import com.lguplus.fleta.data.dto.request.SendSmsCodeRequestDto;
import com.lguplus.fleta.data.dto.request.SendSmsRequestDto;
import com.lguplus.fleta.data.dto.request.inner.SmsAgentRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;
import com.lguplus.fleta.exception.smsagent.NotFoundMsgException;
import com.lguplus.fleta.exception.smsagent.NotSendTimeException;
import com.lguplus.fleta.exception.smsagent.ServerSettingInfoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


/**
 *
 *  SMSAgentDomainService 설명
 *
 *  최초작성 2021-11-23
**/
@Slf4j
@Component
@RequiredArgsConstructor
public class SmsAgentDomainService {

    @Value("${check.https}")
    private String propertyCheckHttps;

    @Value("${agent.no.send.use}")
    private String agentNoSendUse;

    @Value("${agent.no.sendtime}")
    private String agentNoSendTime;

    @Value("${sms.setting.rest_url}")
    private String smsSettingRestUrl;

    @Value("${sms.setting.rest_path}")
    private String smsSettingRestPath;

    @Value("${sms.setting.request.method}")
    private String smsSettingRequestMethod;

    @Value("${sms.setting.timeout}")
    private String smsSettingTimeout;


    private final RetryModuleDomainService retryModuleDomainService;

    public SmsGatewayResponseDto sendSms(SendSmsRequestDto sendSmsRequestDto) {

        log.debug("[sms] - [{}]]", sendSmsRequestDto.toString());

        String s_ctn = sendSmsRequestDto.getSCtn();
        String r_ctn = sendSmsRequestDto.getRCtn();
        String msg = sendSmsRequestDto.getMsg();

        SmsGatewayResponseDto smsGatewayResponseDto = new SmsGatewayResponseDto();

        try {

            if ("1".equals(agentNoSendUse)) {

                try {

                    if (!agentNoSendTime.isEmpty()) {
                        Calendar cal = Calendar.getInstance();
                        Calendar startCal = Calendar.getInstance();
                        Calendar endCal = Calendar.getInstance();

                        String[] noSendAry = agentNoSendTime.split("\\|");

                        int startTime = Integer.parseInt(noSendAry[0]);
                        int endTime = Integer.parseInt(noSendAry[1]);

                        startCal.set(Calendar.HOUR_OF_DAY, startTime);
                        startCal.set(Calendar.MINUTE, 0);
                        startCal.set(Calendar.SECOND, 0);
                        startCal.set(Calendar.MILLISECOND, 0);

                        if (startTime >= endTime) {
                            endCal.add(Calendar.DAY_OF_MONTH, 1);
                        }

                        endCal.set(Calendar.HOUR_OF_DAY, endTime);
                        endCal.set(Calendar.MINUTE, 0);
                        endCal.set(Calendar.SECOND, 0);
                        endCal.set(Calendar.MILLISECOND, 0);

                        if (cal.after(startCal) && cal.before(endCal)) {

                            throw new NotSendTimeException("전송 가능한 시간이 아님");
                        }
                    }
                } catch (Exception e) {

                    throw new ServerSettingInfoException("서버 설정 정보 오류");
                }
            }

            smsGatewayResponseDto = SmsProviderDomainService.send(s_ctn, r_ctn, msg);


//        } catch (CustomExceptionHandler e) {
//            cLog.endLog(s_ctn, r_ctn, msg, e.getClass().getSimpleName(), e.getMessage());
//            throw e;
        } catch (Exception e) {
//            cLog.errorLog(s_ctn, r_ctn, msg, e.getClass().getSimpleName(), e.getMessage());
//            throw new CustomExceptionHandler(e);
        }

        //#########[LOG END]#########
//        cLog.endLog(s_ctn, r_ctn, msg, resultVO.getFlag());
//        return resultVO;
        return smsGatewayResponseDto;

    }

    public SmsGatewayResponseDto sendSmsCode(SendSmsCodeRequestDto sendSMSCodeRequestDto) {

        //#########[LOG SET]#########
        log.debug ("[smsCode] - {}", sendSMSCodeRequestDto.toString());
        String sms_cd = sendSMSCodeRequestDto.getSmsCd();

        SmsGatewayResponseDto smsGatewayResponseDto = new SmsGatewayResponseDto();

        try {

            String msg = selectSmsMsg(sms_cd);
            if(StringUtils.isEmpty(msg)){

                //1506
                throw new NotFoundMsgException("해당 코드에 존재하는 메시지가 없음");
            }

            SmsAgentRequestDto smsAgentRequestDto = SmsAgentRequestDto.builder()
                    .smsCd(sendSMSCodeRequestDto.getSmsCd())
                    .smsId(sendSMSCodeRequestDto.getCtn())
                    .replacement(sendSMSCodeRequestDto.getReplacement())
                    .smsMsg(msg)
                    .build();

            smsGatewayResponseDto = sendSmsCode(smsAgentRequestDto, false);

//        } catch (CustomExceptionHandler e) {
//            log.info("[smsCode] {} {} {} {}", sendSMSCodeRequestDto.toString(), e.getClass().getSimpleName(), e.getCause(), e.getMessage());
//            throw e;
        } catch (Exception e) {
//            cLog.errorLog("[smsCode]",sa_id, stb_mac, sms_cd, replacement, e.getClass().getSimpleName(), e.getMessage());
//            throw new CustomExceptionHandler(e);

            log.info("[smsCode] {} {} {} {}", sendSMSCodeRequestDto.toString(), e.getClass().getSimpleName(), e.getCause(), e.getMessage());

            //9999
            throw new RuntimeException("기타 오류");

        }

        //#########[LOG END]#########
//        cLog.endLog("[smsCode]",sa_id, stb_mac, sms_cd, replacement, resultVO.getFlag());
//        return resultVO;

        return smsGatewayResponseDto;
    }

    public SmsGatewayResponseDto sendSmsCode(SmsAgentRequestDto smsAgentRequestDto, boolean encryptYn) {

        return retryModuleDomainService.smsSendCode(smsAgentRequestDto, encryptYn);

    }


    public String selectSmsMsg(String sms_cd) {

        Map<String, String> map = null;
        try{
            map = callSettingApi();
        }
        catch(java.lang.Exception e){

        }

        if(null == map || map.size() == 0){
            log.debug("[selectSmsMsg]Cannot found URL");
            return "";
        }

        return map.get(sms_cd);
    }


    @Cacheable(value="smsMessageCacheVaue", key="smsMessageCache")
    public Map<String,String> callSettingApi() {


        Map<String, String> map = new HashMap<String, String>();

        try {

            String url = smsSettingRestUrl + smsSettingRestPath;
            String method = StringUtils.defaultIfEmpty(smsSettingRequestMethod, "GET");
            int timeout = Integer.parseInt(StringUtils.defaultIfEmpty(smsSettingTimeout, "5000"));
            if(StringUtils.isEmpty(url)){
                log.debug("[selectSmsMsg]Cannot found URL");
                return null;
            }

            String response = callHttpClient(url, "application/json", method, "UTF-8", null, timeout, timeout);
            log.debug("[callSettingApi][Call]["+response+"]");

/*
            JSONObject jObj = (JSONObject)JSONValue.parse(response);
            JSONArray records = (JSONArray) ((JSONObject)jObj.get("result")).get("recordset");
            for(int i=0;i < records.size(); i++){
                JSONObject jo = (JSONObject)records.get(i);
                String codeId = (String) jo.get("code_id");
                String codeName = (String) jo.get("code_name");
                map.put(codeId, codeName);
            }
*/
        } catch (Exception e) {
            log.debug("[callSettingApi][Call]["+e.getClass().getName()+"]"+e.getMessage());
            //9999
            throw new RuntimeException("기타 오류");
        }
        return map;
    }


    /**
     * HttpClient를 이용하여 웹주소를 호출한다.
     * @param url	예)http://123.123.123.2:80
     * @param acceptHeader	예)application/xml
     * @param Method	예)POST
     * @param encoding	예)UTF-8
     * @param body	POST,PUT일 경우 BODY영역을 이용해서 데이터를 전달 할 수 있다. 예)<aaaa><bbb>BODY</bbb></aaa>
     * @param conn_timeout	예)2
     * @param socket_timeout	예)2
     * @return
     * @throws Exception
     */

    public static String callHttpClient(String url, String acceptHeader, String Method, String encoding, String body, int conn_timeout, int socket_timeout) throws Exception{


        String responseBody = "";
/*
        CloseableHttpClient httpclient = HttpClients.createDefault();
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(socket_timeout)
                .setConnectTimeout(conn_timeout)
                .build();

        try {
            RequestBuilder builder = null;
            if("POST".equals(Method.toUpperCase()))
                builder = RequestBuilder.post().setUri(url).setEntity(new StringEntity(body, encoding));
            else if("PUT".equals(Method.toUpperCase()))
                builder = RequestBuilder.put().setUri(url).setEntity(new StringEntity(body, encoding));
            else if("DELETE".equals(Method.toUpperCase()))
                builder = RequestBuilder.delete().setUri(url);
            else
                builder = RequestBuilder.get().setUri(url);

            builder.setHeader(HttpHeaders.ACCEPT, acceptHeader)
                    .setHeader(HttpHeaders.ACCEPT_CHARSET, encoding)
                    .setHeader(HttpHeaders.CONTENT_TYPE, acceptHeader)
                    .setHeader(HttpHeaders.CONTENT_ENCODING, encoding)
                    .setConfig(requestConfig);

            HttpUriRequest request = builder.build();

            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
                @Override
                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }

            };

            responseBody = httpclient.execute(request, responseHandler);
        } catch (ClientProtocolException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                if(null!=httpclient) httpclient.close();
            } catch (IOException e) {}
        }
*/

        return responseBody;
    }

}
