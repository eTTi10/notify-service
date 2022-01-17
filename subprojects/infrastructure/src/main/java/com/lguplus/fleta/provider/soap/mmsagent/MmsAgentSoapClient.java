package com.lguplus.fleta.provider.soap.mmsagent;

import com.lguplus.fleta.client.MmsAgentDomainClient;
import com.lguplus.fleta.data.dto.request.MmsRequestDto;
import com.lguplus.fleta.provider.soap.mmsagent.module.BasicMMSC;
import com.lguplus.fleta.provider.soap.mmsagent.module.MM7Response;
import com.lguplus.fleta.provider.soap.mmsagent.module.MessageManager;
import com.lguplus.fleta.provider.soap.mmsagent.module.SubmitReq;
import com.lguplus.fleta.provider.soap.mmsagent.module.content.BasicContent;
import com.lguplus.fleta.provider.soap.mmsagent.module.content.UplusContent;
import com.lguplus.fleta.provider.soap.mmsagent.module.inf.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;

@Slf4j
@Component
/**
 * ################### 개발중 입니다. 잠시 개발 중단 상태입니다. 리뷰대상이 아닙니다. #####################
 */
public class MmsAgentSoapClient implements MmsAgentDomainClient {
   private Random random = new Random();
    @Override
    public String sendMMS(Map<String, ?> mmsConfig, MmsRequestDto mmsDto){
        Map<String, ?> mms = mmsConfig;
        String url = (String)mms.get("server_url");
        if(StringUtils.isEmpty(url)){
            return "5200";
        }
        // Add Req Information
        SubmitReq submitReq = new SubmitReq();
        submitReq.setMm7NamespacePrefix((String)mms.get("prefix"));
        submitReq.setNamespace((String)mms.get("namespace"));
        submitReq.setMm7Version((String)mms.get("version"));

        // transactionID
        //int ranNum = (int)(Math.random() * (9999999 - 1000000 + 1)) + 1000000;//7자리 난수발생
        String reqDate = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date(System.currentTimeMillis()));

        int ranNum = random.nextInt(10000000);
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

        MMSC mmsc = new BasicMMSC(url, mms);
        mmsc.getContext().setMm7Namespace((String)mms.get("namespace"));
        mmsc.getContext().setMm7Version((String)mms.get("version"));

        int statusCode = MM7Response.SC_SUCCESS;
        return Integer.toString(statusCode);
    }
}

