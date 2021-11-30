package com.lguplus.fleta.service.smsagent;

import com.lguplus.fleta.client.SmsGatewayClient;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;
import com.lguplus.fleta.exception.smsagent.MsgTypeErrorException;
import com.lguplus.fleta.exception.smsagent.PhoneNumberErrorException;
import com.lguplus.fleta.exception.smsagent.SystemBusyException;
import com.lguplus.fleta.exception.smsagent.SystemErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmsProviderDomainService {

    @Value("${agent.ip1}")
    private String agentIp1;

    @Value("${agent.port1}")
    private String agentPort1;

    @Value("${agent.id1}")
    private String agentId1;

    @Value("${agent.password1}")
    private String agentPassword1;

    @Value("${agent.tps}")
    private String agentTps;

    public static int mSendTerm;
    public static LinkedList<SmsGatewayDomainService> sGatewayQueue = new LinkedList<SmsGatewayDomainService>();

    @PostConstruct
    private void initGateway() {

        String dir = System.getProperty("user.home");
        String index = StringUtils.defaultIfEmpty(System.getProperty("server.index"), "1");
        log.debug("System.getProperty(server.index):" + index);

//        String[] ipList = Properties.getProperty("agent.ip" + index).split("\\|");
//        String[] portList = Properties.getProperty("agent.port" + index).split("\\|");
//        String[] idList = Properties.getProperty("agent.id" + index).split("\\|");
//        String[] pwList = Properties.getProperty("agent.password" + index).split("\\|");

//        int length = idList.length;

//        for (int i = 0; i < length; i++) {
//            SmsGatewayClient smsGateway = new SmsGatewayClient(ipList[i], portList[i], idList[i], pwList[i]) {
//            };
//            sGatewayQueue.offer(smsGateway);
//        }

        mSendTerm = calculateTerm();
    }

    public static SmsGatewayResponseDto send(String s_ctn, String r_ctn, String msg) throws UnsupportedEncodingException, ExecutionException, InterruptedException {

        SmsGatewayResponseDto smsGatewayResponseDto = new SmsGatewayResponseDto();
        Future<SmsGatewayResponseDto> asyncResult = null;


        if (!r_ctn.startsWith("01") || 7 >= r_ctn.length()) {

            //1502
            throw new PhoneNumberErrorException("전화번호 형식 오류");
        }

        if (80 < msg.getBytes("KSC5601").length) {

            //1501
            throw new MsgTypeErrorException("메시지 형식 오류");
        }

        if (SmsProviderDomainService.sGatewayQueue.size() > 0) {
            SmsGatewayDomainService smsGateway = SmsProviderDomainService.sGatewayQueue.poll();
            smsGateway.clearResult();
            long prevSendDate = smsGateway.getLastSendDate().getTime();
            long currentDate = System.currentTimeMillis();

            if (currentDate - prevSendDate <= SmsProviderDomainService.mSendTerm) {

                SmsProviderDomainService.sGatewayQueue.offer(smsGateway);
                //1503
                throw new SystemBusyException("메시지 처리 수용 한계 초과");
            }

            try {
                if (smsGateway.isBind()) {

                    smsGateway.sendMessage(s_ctn, r_ctn, s_ctn, msg, smsGateway.getPort());
                    asyncResult = smsGateway.getResult();

                } else {

                    SmsProviderDomainService.sGatewayQueue.offer(smsGateway);
                    //1500
                    throw new SystemErrorException("시스템 장애");
                }
            } catch (IOException e) {
                SmsProviderDomainService.sGatewayQueue.offer(smsGateway);
                //9999
                throw new RuntimeException("기타 오류");
            }

            SmsProviderDomainService.sGatewayQueue.offer(smsGateway);

        } else {
            //1503
            throw new SystemBusyException("메시지 처리 수용 한계 초과");
        }

        if (null != asyncResult) return asyncResult.get();

        return SmsGatewayResponseDto.builder().build();
    }

    private int calculateTerm() {
        int result = 1000;

        try {
            BigDecimal smsTPS = new BigDecimal(agentTps);

            // (1 / smsTPS) * 1000 + 50
            result = new BigDecimal(1).divide(smsTPS, 3, BigDecimal.ROUND_DOWN).multiply(new BigDecimal(1000)).add(new BigDecimal(50)).intValue();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


}
