package com.lguplus.fleta.service.smsagent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RetryModuleDomainService {

    private int callCount = 0;
    private int systemEr = 0;
    private int busyEr = 0;

    @Value("${sms.send.retry}")
    private String stringRetry;

    @Value("${sms.send.busy.retry}")
    private String stringBusyRetry;

    @Value("${sms.sender.retry.sleep.ms}")
    private String stringSleepTime;

    private int retry = Integer.parseInt(StringUtils.defaultIfEmpty(stringRetry, "0"));
    private int busyRetry = Integer.parseInt(StringUtils.defaultIfEmpty(stringBusyRetry, "5"));
    private int sleepTime = Integer.parseInt(StringUtils.defaultIfEmpty(stringSleepTime, "1000"));

    public void clear(){
        callCount = 0;
        systemEr = 0;
        busyEr = 0;
    }

    public ResultVO smsSend(SmsSendVo smsVo, boolean encryptYn, Log log){
        //0:재처리 안함 1:SMS서버 에러로 재처리 2:서버가 busy하여 재처리
        int checkRetry = 0;
        ResultVO resultVO;
        String sendMsg = "";
        try {
            callCount++;
            sendMsg = SmsMessageManager.convertMsg(smsVo.getSmsMsg(), smsVo.getReplacement());
            resultVO = SmsProvider.send(Properties.getProperty("sms.sender.no")
                    , encryptYn ? AesUtil.decryptAES(smsVo.getSmsId()) : smsVo.getSmsId(), sendMsg);
//			//###############TEST
//			resultVO = new ResultVO();
//			resultVO.setFlag(Properties.getProperty("flag.success"));
//			resultVO.setMessage(Properties.getProperty("message.success"));
//			//###############TEST
        } catch (CustomExceptionHandler e) {
            log.info("[smsSend][Ex]"+ e.getFlag() + ":" + e.getMessage());
            resultVO = new ResultVO();
            resultVO.setFlag(e.getFlag());
            resultVO.setMessage(e.getMessage());
        } catch (Exception e) {
            log.info("[smsSend][Ex]"+ e.getClass().getName() + ":" + e.getMessage());
            resultVO = new ResultVO();
            resultVO.setFlag(Properties.getProperty("flag.etc"));
            resultVO.setMessage(Properties.getProperty("message.etc"));
        }

        //retry여부를 판단한다.
        if(Properties.getProperty("flag.system_error").equals(resultVO.getFlag()) || Properties.getProperty("flag.etc").equals(resultVO.getFlag())){
            checkRetry = 1;
            systemEr++;
        }else if(Properties.getProperty("flag.system_busy").equals(resultVO.getFlag())){
            checkRetry = 2;
            busyEr++;
        }

        log.info("[smsSend]["+smsVo.getPtDay()+"]["+smsVo.getSmsCd()+"]["+smsVo.getSmsId()+"]["+sendMsg+"][callCount:"+callCount+"][systemEr:"+systemEr+"][busyEr:"+busyEr+"]["+resultVO.getFlag()+"]["+resultVO.getMessage()+"]");
        if(checkRetry == 0 || systemEr > retry || busyEr > busyRetry){
            return resultVO;
        }else{
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {}

            return smsSend(smsVo, encryptYn, log);
        }
    }

}
