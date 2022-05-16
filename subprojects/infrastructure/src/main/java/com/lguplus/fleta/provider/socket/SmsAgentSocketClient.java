package com.lguplus.fleta.provider.socket;

import com.lguplus.fleta.client.SmsAgentClient;
import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;
import com.lguplus.fleta.exception.smsagent.*;
import com.lguplus.fleta.properties.SmsAgentProps;
import com.lguplus.fleta.provider.socket.smsagent.SmsGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmsAgentSocketClient implements SmsAgentClient {

    @Value("${sms.agent.tps}")
    private int agentTps;

    private final SmsAgentProps smsAgentProps;

    private int mSendTerm;
  
    private LinkedList<SmsGateway> sGatewayQueue = new LinkedList<>();

    @PostConstruct
    public void initGateway() {

        log.debug("System.getProperty(server.index):" + System.getProperty("server.index"));
        String index = StringUtils.defaultIfEmpty(System.getProperty("server.index"), "1");
        log.debug("index:" + index);

        Map<String, String> mapServers = smsAgentProps.findMapByIndex(index).orElseThrow();
        String[] ipList = mapServers.get("ip").split("\\|");
        String[] portList = mapServers.get("port").split("\\|");
        String[] idList = mapServers.get("id").split("\\|");
        String[] pwList = mapServers.get("password").split("\\|");

        log.debug("mapServers:"+ mapServers);
        log.debug("mapServers.get(ip):"+ mapServers.get("ip"));

        int length = idList.length;

        for (int i = 0; i < length; i++) {
            SmsGateway smsGateway = new SmsGateway(ipList[i], portList[i], idList[i], pwList[i]);
            sGatewayQueue.offer(smsGateway);
        }
        mSendTerm = calculateTerm();
    }

    public SmsGatewayResponseDto send(String sCtn, String rCtn, String message) throws UnsupportedEncodingException, ExecutionException, InterruptedException {

        Future<SmsGatewayResponseDto> asyncResult;

        if (!rCtn.startsWith("01") || 7 >= rCtn.length()) {

            //1502
            throw new SmsAgentCustomException("1502", "전화번호 형식 오류");
        }

        if (80 < message.getBytes("KSC5601").length) {

            //1501
            throw new SmsAgentCustomException("1501", "메시지 형식 오류");
        }

        if (!sGatewayQueue.isEmpty()) {

            SmsGateway smsGateway = sGatewayQueue.poll();  //큐의 첫번째 요소 가져오고 삭제

            smsGateway.clearResult();
            long prevSendDate = smsGateway.getLastSendDate().getTime();
            long currentDate = System.currentTimeMillis();

            if (currentDate - prevSendDate <= mSendTerm) {

                sGatewayQueue.offer(smsGateway);   //큐의 마지막 요소로 삽입
              
                //1503
                throw new SmsAgentCustomException("1503", "메시지 처리 수용 한계 초과");
            }

            try {
                //게이트웨이 서버에 소켓접속이 완료된 경우
                if (smsGateway.isBind()) {

                    log.debug("smsGateway.sendMessage( {}, {}, {}, {}, {})", sCtn, rCtn, sCtn, message, smsGateway.getPort());
                    smsGateway.sendMessage(sCtn, rCtn, sCtn, message, smsGateway.getPort());
                    asyncResult = smsGateway.getResult();

                } else {

                    sGatewayQueue.offer(smsGateway);
                    //1500
                    throw new SmsAgentCustomException("1500", "시스템 장애");
                }
            } catch (IOException e) {
                sGatewayQueue.offer(smsGateway);
                //9999
                throw new SmsAgentCustomException("9999", "기타 오류");
            }

            sGatewayQueue.offer(smsGateway);

        } else {
            //1503
            throw new SmsAgentCustomException("1503", "메시지 처리 수용 한계 초과");
        }

        return asyncResult.get();
    }

    private int calculateTerm() {

        int result = 1000;

        try {
            BigDecimal smsTPS = new BigDecimal(agentTps);

            // (1 / smsTPS) * 1000 + 50
            result = new BigDecimal(1).divide(smsTPS, 3, RoundingMode.DOWN).multiply(new BigDecimal(1000)).add(new BigDecimal(50)).intValue();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


}
