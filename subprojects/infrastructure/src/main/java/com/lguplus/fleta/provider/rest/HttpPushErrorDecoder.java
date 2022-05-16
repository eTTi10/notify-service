package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.exception.httppush.HttpPushCustomException;
import com.lguplus.fleta.properties.HttpServiceProps;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class HttpPushErrorDecoder implements ErrorDecoder {

    private final HttpServiceProps httpServiceProps;


    @Override
    public Exception decode(String methodKey, Response response) {
        log.debug("\n{} 에러 발생 :::::::::::::: status: {}\nbody: {}", methodKey, response.status(), response.body());

        int status = response.status();

        Pair<String, String> cdMsgPair;

        switch (status) {
            case 202:
                // code "1112", message "The request Accepted"
                cdMsgPair = httpServiceProps.getExceptionCodeMessage("AcceptedException");
                return new HttpPushCustomException(status, cdMsgPair.getLeft(), cdMsgPair.getRight());

            case 400:
                // code "1104", message "Push GW BadRequest"
                cdMsgPair = httpServiceProps.getExceptionCodeMessage("BadRequestException");
                return new HttpPushCustomException(status, cdMsgPair.getLeft(), cdMsgPair.getRight());

            case 401:
                // code "1105", message "Push GW UnAuthorized"
                cdMsgPair = httpServiceProps.getExceptionCodeMessage("UnAuthorizedException");
                return new HttpPushCustomException(status, cdMsgPair.getLeft(), cdMsgPair.getRight());

            case 403:
                // code "1106", message "Push GW Forbidden"
                cdMsgPair = httpServiceProps.getExceptionCodeMessage("ForbiddenException");
                return new HttpPushCustomException(status, cdMsgPair.getLeft(), cdMsgPair.getRight());

            case 404:
                // code "1107", message "Push GW Not Found"
                cdMsgPair = httpServiceProps.getExceptionCodeMessage("NotFoundException");
                return new HttpPushCustomException(status, cdMsgPair.getLeft(), cdMsgPair.getRight());

            case 410:
                // code "1113", message "Not Exist RegistID"
                cdMsgPair = httpServiceProps.getExceptionCodeMessage("NotExistRegistIdException");
                return new HttpPushCustomException(status, cdMsgPair.getLeft(), cdMsgPair.getRight());

            case 412:
                // code "1108", message "Push GW Precondition Failed"
                cdMsgPair = httpServiceProps.getExceptionCodeMessage("PreConditionFailedException");
                return new HttpPushCustomException(status, cdMsgPair.getLeft(), cdMsgPair.getRight());

            case 500:
                // code "1109", message "Push GW Internal Error"
                cdMsgPair = httpServiceProps.getExceptionCodeMessage("InternalErrorException");
                return new HttpPushCustomException(status, cdMsgPair.getLeft(), cdMsgPair.getRight());

            case 502:
                // code "1114", message "Exception Occurs"
                cdMsgPair = httpServiceProps.getExceptionCodeMessage("ExceptionOccursException");
                return new HttpPushCustomException(status, cdMsgPair.getLeft(), cdMsgPair.getRight());

            case 503:
                // code "1110", message "Push GW Service Unavailable"
                cdMsgPair = httpServiceProps.getExceptionCodeMessage("ServiceUnavailableException");
                return new HttpPushCustomException(status, cdMsgPair.getLeft(), cdMsgPair.getRight());

            default:
                return new RuntimeException("기타 오류");
        }
    }

}
