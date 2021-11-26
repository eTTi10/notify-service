package com.lguplus.fleta.service.smsagent;

import com.lguplus.fleta.client.SmsGatewayClient;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import java.util.concurrent.Future;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmsProviderDomainService {

    @Value("${agent.ip}")
    private String agentIp;

    @Value("${agent.port}")
    private String agentPort;

    @Value("${agent.id}")
    private String agentId;

    @Value("${agent.password}")
    private String agentPassword;

    public static int mSendTerm;
    public static LinkedList<SmsGatewayClient> sGatewayQueue = new LinkedList<SmsGatewayClient>();

    @PostConstruct
    private void initGateway() {

        //        String index = StringUtils.defaultIfEmpty(CommonUtil.getSystemProperty("server.index"), "1");

//        String dir = System.getProperty("user.home");
        String index = StringUtils.defaultIfEmpty(System.getProperty("server.index"), "1");
        log.debug("System.getProperty(server.index):" + index);

//        String[] ipList = Properties.getProperty("agent.ip" + index).split("\\|");
//        String[] portList = Properties.getProperty("agent.port" + index).split("\\|");
//        String[] idList = Properties.getProperty("agent.id" + index).split("\\|");
//        String[] pwList = Properties.getProperty("agent.password" + index).split("\\|");
//
//        int length = idList.length;
//
//        for (int i = 0; i < length; i++) {
//            SmsGatewayClient smsGateway = new SmsGatewayClient(ipList[i], portList[i], idList[i], pwList[i]) {
//            };
//            sGatewayQueue.offer(smsGateway);
//        }

//        mSendTerm = calculateTerm();
    }

    public static SuccessResponseDto send(String s_ctn, String r_ctn, String msg) throws Exception {

/*
        ResultVO resultVO = new ResultVO();
        Future<ResultVO> asyncResult = null;

        CustomExceptionHandler exception = new CustomExceptionHandler();

        if (!r_ctn.startsWith("01") || 7 >= r_ctn.length()) {
            exception.setFlag(Properties.getProperty("flag.phone_number_error"));
            exception.setMessage(Properties.getProperty("message.phone_number_error"));
            throw exception;
        }

        if (80 < msg.getBytes("KSC5601").length) {
            exception.setFlag(Properties.getProperty("flag.msg_type_error"));
            exception.setMessage(Properties.getProperty("message.msg_type_error"));
            throw exception;
        }

        if (SmsProvider.sGatewayQueue.size() > 0) {
            SMSGateway smsGateway = SmsProvider.sGatewayQueue.poll();
            smsGateway.clearResult();
            long prevSendDate = smsGateway.getLastSendDate().getTime();
            long currentDate = System.currentTimeMillis();

            if (currentDate - prevSendDate <= SmsProvider.mSendTerm) {
                exception.setFlag(Properties.getProperty("flag.system_busy"));
                exception.setMessage(Properties.getProperty("message.system_busy"));
                SmsProvider.sGatewayQueue.offer(smsGateway);
                throw exception;
            }

            try {
                if (smsGateway.isBind()) {
                    smsGateway.sendMessage(s_ctn, r_ctn, s_ctn, msg, smsGateway.getPort());
                    asyncResult = smsGateway.getResult();
                } else {
                    exception.setFlag(Properties.getProperty("flag.system_error"));
                    exception.setMessage(Properties.getProperty("message.system_error"));
                    SmsProvider.sGatewayQueue.offer(smsGateway);
                    throw exception;
                }
            } catch (IOException e) {
                exception.setFlag(Properties.getProperty("flag.etc"));
                exception.setMessage(Properties.getProperty("message.etc"));
                SmsProvider.sGatewayQueue.offer(smsGateway);
                throw exception;
            }

            SmsProvider.sGatewayQueue.offer(smsGateway);
        } else {
            resultVO.setFlag(Properties.getProperty("flag.system_busy"));
            resultVO.setMessage(Properties.getProperty("message.system_busy"));
        }

        if (null != asyncResult) return asyncResult.get();
*/

//        return resultVO;
        return SuccessResponseDto.builder().build();
    }

/*
    private int calculateTerm() {
        int result = 1000;

        try {
            BigDecimal smsTPS = new BigDecimal(Properties.getProperty("agent.tps"));

            // (1 / smsTPS) * 1000 + 50
            result = new BigDecimal(1).divide(smsTPS, 3, BigDecimal.ROUND_DOWN).multiply(new BigDecimal(1000)).add(new BigDecimal(50)).intValue();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
*/


}
