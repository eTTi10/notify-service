package com.lguplus.fleta.provider.external.mmsagent.soap;

import com.lguplus.fleta.client.MmsAgentClient;
import com.lguplus.fleta.data.dto.request.MmsRequestDto;
import com.lguplus.fleta.provider.external.mmsagent.soap.module.BasicMMSC;
import com.lguplus.fleta.provider.external.mmsagent.soap.module.MM7Error;
import com.lguplus.fleta.provider.external.mmsagent.soap.module.MM7Response;
import com.lguplus.fleta.provider.external.mmsagent.soap.module.MessageManager;
import com.lguplus.fleta.provider.external.mmsagent.soap.module.SubmitReq;
import com.lguplus.fleta.provider.external.mmsagent.soap.module.content.BasicContent;
import com.lguplus.fleta.provider.external.mmsagent.soap.module.content.UplusContent;
import com.lguplus.fleta.provider.external.mmsagent.soap.module.inf.Address;
import com.lguplus.fleta.provider.external.mmsagent.soap.module.inf.ChargedParty;
import com.lguplus.fleta.provider.external.mmsagent.soap.module.inf.MMSC;
import com.lguplus.fleta.provider.external.mmsagent.soap.module.inf.MessageClass;
import com.lguplus.fleta.provider.external.mmsagent.soap.module.inf.RelativeDate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component

public class MmsAgentSoapClient implements MmsAgentClient {

    private final Random random = new Random();

    @Override
    public String sendMMS(Map<String, ?> mmsConfig, MmsRequestDto mmsDto) {
        Map<String, ?> mms = mmsConfig;
        String url = (String) mms.get("server_url");
        if (StringUtils.isEmpty(url)) {
            return "5200";
        }
        // Add Req Information
        SubmitReq submitReq = new SubmitReq();
        submitReq.setMm7NamespacePrefix((String) mms.get("prefix"));
        submitReq.setNamespace((String) mms.get("namespace"));
        submitReq.setMm7Version((String) mms.get("version"));

        // transactionID
        //int ranNum = (int)(Math.random() * (9999999 - 1000000 + 1)) + 1000000;//7?????? ????????????
        String reqDate = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date(System.currentTimeMillis()));

        int randomNumber = random.nextInt(10000000);
        String transactionID = randomNumber + "_" + reqDate;

        // Set submitReq Info
        submitReq.setTransactionId(transactionID);
        submitReq.setVaspId((String) mms.get("vaspid"));
        submitReq.setVasId((String) mms.get("vasid"));
        submitReq.setSenderAddress(new Address((String) mms.get("sender"), null, null));
        submitReq.setCallBackAddress(new Address(mms.get("callback").toString(), null, null));
        submitReq.addRecipient(new Address(mmsDto.getCtn(), Address.RecipientType.TO));
        submitReq.setServiceCode("0000");
        submitReq.setMessageClass(MessageClass.INFORMATIONAL);
        submitReq.setTimeStamp(new Date(System.currentTimeMillis()));
        submitReq.setEarlistDeliveryTime(new RelativeDate(new Date(System.currentTimeMillis())));
        submitReq.setDeliveryReport(false);
        submitReq.setReadReply(false);
        submitReq.setChargedParty(ChargedParty.SENDER);
        submitReq.setDistributionIndicator(false);
        submitReq.setSubject(mmsDto.getMmsTitle());//mms_cd?????? ???????????? ex) M011

        // Add Text Content
        UplusContent con1 = new UplusContent(
            MessageManager.convertMsg(mmsDto.getMmsMsg(), mmsDto.getMmsRep())
        );

        con1.setContentId("mm7-content-1");
        con1.setX_Kmms_SVCCODE("");
        con1.setX_Kmms_redistribution("");

        con1.setX_Kmms_TextInput("0");
        submitReq.setContent(new BasicContent(con1));

        log.info("\n [001][????????? ???????????????] con1.text \n" + con1.getText());

        MMSC mmsc = new BasicMMSC(url, mms);
        mmsc.getContext().setMm7Namespace((String) mms.get("namespace"));
        mmsc.getContext().setMm7Version((String) mms.get("version"));

        log.info("\n [002][mmsc??????url] url \n" + url);

        /**
         * ################### ???????????? ????????? ?????????. [ ????????? ?????? ]?????? ?????? ?????? ???????????????. ??????????????? ????????????. #####################
         */
        try {
            //?????? ?????? ?????????...????????? ??????...
            mmsc.submit(submitReq);
        } catch (MM7Error e) {
            return e.getFaultCode();
        }

        int statusCode = MM7Response.SC_SUCCESS;
        return Integer.toString(statusCode);
    }

    @Override
    public String sendMMS(Map<String, ?> mmsConfig, Map<String, String> mapServers, MmsRequestDto mmsDto) {
        Map<String, ?> mms = mmsConfig;
        String url = mapServers.get("server_url");
        if (StringUtils.isEmpty(url)) {
            return "5200";
        }
        // Add Req Information
        SubmitReq submitReq = new SubmitReq();
        submitReq.setMm7NamespacePrefix((String) mms.get("prefix"));
        submitReq.setNamespace((String) mms.get("namespace"));
        submitReq.setMm7Version((String) mms.get("version"));

        // transactionID
        //int ranNum = (int)(Math.random() * (9999999 - 1000000 + 1)) + 1000000;//7?????? ????????????
        String reqDate = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date(System.currentTimeMillis()));

        int randomNumber = random.nextInt(10000000);
        String transactionID = randomNumber + "_" + reqDate;

        // Set submitReq Info
        submitReq.setTransactionId(transactionID);
        submitReq.setVaspId((String) mms.get("vaspid"));
        submitReq.setVasId((String) mms.get("vasid"));
        submitReq.setSenderAddress(new Address((String) mms.get("sender"), null, null));
        submitReq.setCallBackAddress(new Address(mms.get("callback").toString(), null, null));
        submitReq.addRecipient(new Address(mmsDto.getCtn(), Address.RecipientType.TO));
        submitReq.setServiceCode("0000");
        submitReq.setMessageClass(MessageClass.INFORMATIONAL);
        submitReq.setTimeStamp(new Date(System.currentTimeMillis()));
        submitReq.setEarlistDeliveryTime(new RelativeDate(new Date(System.currentTimeMillis())));
        submitReq.setDeliveryReport(false);
        submitReq.setReadReply(false);
        submitReq.setChargedParty(ChargedParty.SENDER);
        submitReq.setDistributionIndicator(false);
        submitReq.setSubject(mmsDto.getMmsTitle());//mms_cd?????? ???????????? ex) M011

        // Add Text Content
        UplusContent con1 = new UplusContent(
                MessageManager.convertMsg(mmsDto.getMmsMsg(), mmsDto.getMmsRep())
        );

        con1.setContentId("mm7-content-1");
        con1.setX_Kmms_SVCCODE("");
        con1.setX_Kmms_redistribution("");

        con1.setX_Kmms_TextInput("0");
        submitReq.setContent(new BasicContent(con1));

        log.info("\n [001][????????? ???????????????] con1.text \n" + con1.getText());

        MMSC mmsc = new BasicMMSC(url, mms);
        mmsc.getContext().setMm7Namespace((String) mms.get("namespace"));
        mmsc.getContext().setMm7Version((String) mms.get("version"));

        log.info("\n [002][mmsc??????url] url \n" + url);

        /**
         * ################### ???????????? ????????? ?????????. [ ????????? ?????? ]?????? ?????? ?????? ???????????????. ??????????????? ????????????. #####################
         */
        try {
            //?????? ?????? ?????????...????????? ??????...
            mmsc.submit(submitReq);
        } catch (MM7Error e) {
            return e.getFaultCode();
        }

        int statusCode = MM7Response.SC_SUCCESS;
        return Integer.toString(statusCode);
    }
}

