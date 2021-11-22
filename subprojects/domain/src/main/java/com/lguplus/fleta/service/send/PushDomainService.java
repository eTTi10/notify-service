package com.lguplus.fleta.service.send;

import com.lguplus.fleta.client.PersonalizationDomainClient;
import com.lguplus.fleta.data.dto.RegIdDto;
import com.lguplus.fleta.data.dto.request.SendSMSCodeRequestDto;
import com.lguplus.fleta.data.dto.request.outer.SendPushCodeRequestDto;
import com.lguplus.fleta.data.dto.response.RegistrationIdResponseDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.entity.RegistrationIdEntity;
import com.lguplus.fleta.repository.PushRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PushDomainService {

    private final PersonalizationDomainClient personalizationDomainClient;
    private final PushRepository pushRepository;

    public RegIdDto getRegistrationID(SendPushCodeRequestDto sendPushCodeRequestDto) {

        Map<String, String> inputMap = new HashMap<>();

        inputMap.put("sa_id", sendPushCodeRequestDto.getSaId());
        inputMap.put("stb_mac", sendPushCodeRequestDto.getStbMac());

//        RegIdDto regIdDto = Optional.ofNullable(personalizationDomainClient.getRegistrationID(inputMap)).orElseGet(RegIdDto::new);
        RegIdDto regIdDto = personalizationDomainClient.getRegistrationID(inputMap);
        return regIdDto;
    }


/*
    private final String changeNodeName = "reg_id";

    public String makePushBody(String sa_id, String stb_mac, String body) throws MimsCommonException {

        try{
            PushDomParser pdp = new PushDomParser("request");
            Document doc = PushDomParser.getDom(body);
            String reg_id = pdp.getValue(doc, changeNodeName);

            //reg_id를 기입하지 않았다면 DB에서 RegID를 찾아서 처리한다.
            if(StringUtils.isEmpty(reg_id)){
                ValidatorFactory.create()
                        .valid("sa_id", sa_id).notNull().maxLength(12)
                        .valid("stb_mac", stb_mac).notNull().maxLength(14).done();

                String findRegId = "";
                try{
                    RegistrationIdVo registrationidVo = new RegistrationIdVo();
                    registrationidVo.setSa_id(sa_id);
                    registrationidVo.setStb_mac(stb_mac);
                    findRegId = dao.getRegistrationID(registrationidVo);
                }catch(java.lang.Exception e){
                    throw new MimsCommonException(ResultCode.DBError.getFlag(), ResultCode.DBError.getMessage());
                }

                if(StringUtils.isEmpty(findRegId)) throw new MimsCommonException(ResultCode.DBDataNotExist.getFlag(), "RegID 미존재");

                body = pdp.changeNodeData(doc, changeNodeName, findRegId);
            }
        }catch(MimsCommonException ce){
            throw ce;
        }catch(java.lang.Exception e){
            throw new MimsCommonException(ResultCode.RequestValueBadFormat.getFlag(), ResultCode.RequestValueBadFormat.getMessage());
        }

        return body;
    }


    public String makePushBodyTemp(String sa_id, String stb_mac, String body) throws MimsCommonException {

        try{
            PushDomParser pdp = new PushDomParser("request");
            Document doc = PushDomParser.getDom(body);
            String reg_id = pdp.getValue(doc, changeNodeName);


            ValidatorFactory.create()
                    .valid("sa_id", sa_id).notNull().maxLength(12)
                    .valid("stb_mac", stb_mac).notNull().maxLength(14).done();

            String findRegId = "";
            try{
                RegistrationIdVo registrationidVo = new RegistrationIdVo();
                registrationidVo.setSa_id(sa_id);
                registrationidVo.setStb_mac(stb_mac);
                findRegId = dao.getRegistrationID(registrationidVo);
            }catch(java.lang.Exception e){
                throw new MimsCommonException(ResultCode.DBError.getFlag(), ResultCode.DBError.getMessage());
            }

            //	if(StringUtils.isEmpty(findRegId)) throw new MimsCommonException(ResultCode.DBDataNotExist.getFlag(), "RegID 미존재");

            body = pdp.changeNodeData(doc, changeNodeName, findRegId);

        }catch(MimsCommonException ce){
            throw ce;
        }catch(java.lang.Exception e){
            throw new MimsCommonException(ResultCode.RequestValueBadFormat.getFlag(), ResultCode.RequestValueBadFormat.getMessage());
        }

        return body;
    }


    public String makePushResultCtn(String code , String message){
        String body = "";

        body = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<error>" +
                "<code>"+code+"</code>" +
                "<message>"+message+"</message>" +
                "<wait_time></wait_time>" +
                "</error>";

        return body;
    }


    public String makePushBodyCtn(String ctn , String payload  , String items , String reg_type , String payloaditem , String push_type) throws MimsCommonException {
        String body = "";
        String findRegId = "";
        try{

            ValidatorFactory.create().valid("ctn", ctn).notNull().done();

            if(reg_type.equals("2")){
                RegistrationIdVo registrationidVo = new RegistrationIdVo();
                registrationidVo.setCtn("0"+ctn);
                findRegId = dao.getRegistrationIDbyCtn(registrationidVo);
            } else{
                findRegId = ctn;
            }


            if(push_type.equals("G")) {

                body = "<request><users>" +
                        "<reg_id>"+findRegId+"</reg_id></users>" +
                        "<msg><![CDATA["+payload+"]]></msg>" +
                        "<items>"+items+"</items>" +
                        "</request>";

            }else if(push_type.equals("A")) {

                body = "<request><users>" +
                        "<reg_id>"+findRegId+"</reg_id></users>" +
                        "<msg><![CDATA["+payload+"]]></msg>" +
                        "<items><item><![CDATA["+payloaditem+"]]></item>"+items+"</items>" +
                        "</request>";
            }


        }catch(MimsCommonException ce){
            throw ce;
        }

        return body;
    }

    public String makePushBodyPos(String ctn , String payload  , String items , String reg_type , String payloaditem , String push_type) throws MimsCommonException {
        String body = "";
        String findRegId = "";
        try{

            ValidatorFactory.create().valid("ctn", ctn).notNull().done();

            if(reg_type.equals("2")){
                RegistrationIdVo registrationidVo = new RegistrationIdVo();
                registrationidVo.setCtn("0"+ctn);
                findRegId = dao.getRegistrationIDbyCtn(registrationidVo);
            } else{
                findRegId = ctn;
            }


            if(push_type.equals("G")) {

                body = "<request>" +
                        "<reg_id>"+findRegId+"</reg_id>" +
                        "<msg><![CDATA["+payload+"]]></msg>" +
                        "<items>"+items+"</items>" +
                        "</request>";

            }else if(push_type.equals("A")) {

                body = "<request>" +
                        "<reg_id>"+findRegId+"</reg_id>" +
                        "<msg><![CDATA["+payload+"]]></msg>" +
                        "<items><item><![CDATA["+payloaditem+"]]></item>"+items+"</items>" +
                        "</request>";
            }


        }catch(MimsCommonException ce){
            throw ce;
        }

        return body;
    }
*/

}
