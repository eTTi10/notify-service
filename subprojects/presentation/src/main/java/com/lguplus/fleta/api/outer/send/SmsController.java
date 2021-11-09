package com.lguplus.fleta.api.outer.send;

import com.lguplus.fleta.api.inner.smsagent.SMSAgentController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@RequiredArgsConstructor
@RestController
public class SmsController {

    private final SMSAgentController smsAgentController;

//    @Autowired
//    private CacheConfigProperties properties;

    /**
     * SMS발송 Queue 등록
     * @param sa_id
     * @param stb_mac
     * @param sms_cd
     * @param ctn
     * @param replacement
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/mims/sendSms")
    public ResponseEntity<String> setPayment(
            @RequestParam(value = "sa_id", required = false, defaultValue = "") String sa_id,
            @RequestParam(value = "stb_mac", required = false, defaultValue = "") String stb_mac,
            @RequestParam(value = "sms_cd", required = false, defaultValue = "") String sms_cd,
            @RequestParam(value = "ctn", required = false, defaultValue = "") String ctn,
            @RequestParam(value = "replacement", required = false, defaultValue = "") String replacement,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        // #########[LOG SET]#########
//        CLog2 cLog = new CLog2(logger, request.getRemoteAddr(), request.getRequestURI(), request.getMethod());
//        cLog.startLog("sendSms", sa_id, stb_mac ,sms_cd, ctn, replacement);
        String acceptHeader = defaultAcceptHeader(request.getHeader("Accept"));

        String result = null;
        try {
//            ValidatorFactory.create()
//                    .valid("sms_cd", sms_cd).notNull()
//                    .valid("ctn", ctn).notNull().done();

            String param = "";
            for (Enumeration<?> e = request.getParameterNames(); e.hasMoreElements() ; ) {
                if("" != param) param += "&";
                String name = (String)e.nextElement();
                param += name + "=" + request.getParameter(name);
            }

//            String host = properties.getString("sms.queue.host");
//            String url = properties.getString("sms.queue.request.url");
//            String method = properties.getString("sms.queue.request.method", "GET");
//            int port = properties.getInt("sms.queue.port", 80);
//            int timeout = properties.getInt("sms.queue.timeout", 30000);

//            HttpResult httpResult = HttpClientUtils.sendRequest(host, port, url + "?" + param, method, "UTF-8", acceptHeader, timeout);

//            cLog.middleLog("sendSms Request", sa_id, stb_mac, httpResult.getRequestMethod(), httpResult.getRequestURL());
//            cLog.middleLog("sendSms Response", sa_id, stb_mac, httpResult.getResponseStatus(), httpResult.getResponseContentType(), httpResult.getResponseBody());

//            result = httpResult.getResponseBody();
        } catch (Exception e) {
//            cLog.errorLog("sendSms", sa_id, stb_mac, e.getClass().getName(), e.getMessage());
            throw e;
        } finally {
//            cLog.endLog("sendSms", sa_id, stb_mac, result);
        }

        return getResponseEntity(acceptHeader, result);
    }

    /**
     *  Response header 값을 설정 후 ResponseEntity를 반환
     * @param acceptHeader Request Header의 Accept 값
     * @param body Response의 Body 값
     * @return
     */
    private ResponseEntity<String> getResponseEntity(String acceptHeader, String body) {
        HttpHeaders responseHeaders = new HttpHeaders();
//        responseHeaders.add(HTTP.CONTENT_TYPE, acceptHeader + HTTP.CHARSET_PARAM + HTTP.UTF_8);
        return new ResponseEntity<String>(body, responseHeaders, HttpStatus.OK);
    }

    /**
     * Accept Header를 필터해서 리턴한다.
     *
     * @param accept Accept Header
     * @return 필터된 Accept Header
     */
    private String defaultAcceptHeader(String accept) {
        accept = StringUtils.defaultString(accept).toLowerCase();

        if (accept.contains(MediaType.APPLICATION_JSON.toString())) {
            accept = MediaType.APPLICATION_JSON.toString();
        } else if (accept.contains(MediaType.APPLICATION_XML.toString())) {
            accept = MediaType.APPLICATION_XML.toString();
        } else {
            accept = MediaType.TEXT_PLAIN.toString();
        }

        return accept;
    }

}
