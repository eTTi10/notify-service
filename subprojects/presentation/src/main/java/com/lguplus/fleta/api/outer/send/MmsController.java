package com.lguplus.fleta.api.outer.send;

import com.lguplus.fleta.data.dto.request.SendMMSRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.vo.SendMMSVo;
import com.lguplus.fleta.service.send.MMSService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MmsController {

    private final MMSService smsService;

    @PostMapping("/mims/sendMms")
    public SuccessResponseDto setPayment(@Valid SendMMSVo request) {

        log.debug("SmsController.setPayment() - {}:{}", "MMS발송 요청", request);

        SendMMSRequestDto requestDto = request.convert();
        validationCheckQual(request);

        return smsService.sendMMS(requestDto);
    }

    private void validationCheckQual(SendMMSVo request){
        Pattern stbMacPattern = Pattern.compile("^[a-zA-Z0-9.]*$");
        Pattern pSpecialCharacter = Pattern.compile("^[A-Za-z0-9]*$");
        Matcher mSpecialCharacter = pSpecialCharacter.matcher(request.getSaId());
        if(!mSpecialCharacter.find()){
            //throw new DataAlreadyExistsException("test");
            //throw new Exception("aa");
            //ParameterTypeMismatchException();
            //ParameterTypeMismatchException("5008","sa_id의 패턴이 일치하지 않습니다.");
        }
    }
}


    /*
    private void validationCheckQual(String sa_id, String stb_mac, String mms_cd, String ctn) throws Exception{

        //특문
        Pattern stbMacPattern = Pattern.compile("^[a-zA-Z0-9.]*$");
        Pattern pSpecialCharacter = Pattern.compile("^[A-Za-z0-9]*$");
        Matcher mSpecialCharacter = pSpecialCharacter.matcher(sa_id);
        if(!mSpecialCharacter.find()){
            throw new NetworkTypePattern("5008","sa_id의 패턴이 일치하지 않습니다.");
        }
        Matcher mSpecialCharacter2 = stbMacPattern.matcher(stb_mac);
        if(!mSpecialCharacter2.find()){
            throw new CustomExceptionHandler("5008","stb_mac의 패턴이 일치하지 않습니다.");
        }
        Matcher mSpecialCharacter3 = pSpecialCharacter.matcher(ctn);
        if(!mSpecialCharacter3.find()){
            throw new CustomExceptionHandler("5008","ctn의 패턴이 일치하지 않습니다.");
        }
        Matcher mSpecialCharacter4 = pSpecialCharacter.matcher(mms_cd);
        if(!mSpecialCharacter4.find()){
            throw new CustomExceptionHandler("5008","mms_cd의 패턴이 일치하지 않습니다.");
        }

        //공백
        Pattern pBlank = Pattern.compile("\\s");
        Matcher mBlank = pBlank.matcher(sa_id);
        if(mBlank.find()) {
            throw new CustomExceptionHandler("5008","sa_id 값에 공백이 없어야 합니다.");
        }
        Matcher mBlank1 = pBlank.matcher(stb_mac);
        if(mBlank1.find()) {
            throw new CustomExceptionHandler("5008","stb_mac 값에 공백이 없어야 합니다.");
        }
        Matcher mBlank2 = pBlank.matcher(ctn);
        if(mBlank2.find()) {
            throw new CustomExceptionHandler("5008","ctn 값에 공백이 없어야 합니다.");
        }
        Matcher mBlank3 = pBlank.matcher(mms_cd);
        if(mBlank3.find()) {
            throw new CustomExceptionHandler("5008","mms_cd 값에 공백이 없어야 합니다.");
        }

        //길이(최소)
        if(sa_id.length() < 8){
            throw new CustomExceptionHandler("5002","sa_id 의 길이가 8보다 작습니다.");
        }
        if(stb_mac.length() < 10){
            throw new CustomExceptionHandler("5002","stb_mac 의 길이가 10보다 작습니다.");
        }
        //길이(최대)
        if(sa_id.length() > 15){
            throw new CustomExceptionHandler("5002","sa_id 의 길이가 10보다 큽니다.");
        }
        if(stb_mac.length() > 20){
            throw new CustomExceptionHandler("5002","stb_mac 의 길이가 20보다 큽니다.");
        }

        //ctn
        Pattern pattern = Pattern.compile("^01([0|1|6|7|8|9])([0-9]{3,4})([0-9]{4})$");
        Matcher matcher = pattern.matcher(ctn);
        if(!matcher.matches()){
            throw new CustomExceptionHandler(Properties.getProperty("flag.phone_number_error"), Properties.getProperty("message.phone_number_error"));
        }
    }
    */