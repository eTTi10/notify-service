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

        HttpPushCustomException httpPushCustomException = new HttpPushCustomException();
        Pair<String, String> cdMsgPair;

        switch (response.status()) {
            case 202:
                // code "1112", message "The request Accepted"
                cdMsgPair = httpServiceProps.getExceptionCodeMessage("AcceptedException");
                httpPushCustomException.setCode(cdMsgPair.getLeft());
                httpPushCustomException.setMessage(cdMsgPair.getRight());

                throw httpPushCustomException;

            case 400:
                // code "1104", message "Push GW BadRequest"
                cdMsgPair = httpServiceProps.getExceptionCodeMessage("BadRequestException");
                httpPushCustomException.setCode(cdMsgPair.getLeft());
                httpPushCustomException.setMessage(cdMsgPair.getRight());

                throw httpPushCustomException;

            case 401:
                // code "1105", message "Push GW UnAuthorized"
                cdMsgPair = httpServiceProps.getExceptionCodeMessage("UnAuthorizedException");
                httpPushCustomException.setCode(cdMsgPair.getLeft());
                httpPushCustomException.setMessage(cdMsgPair.getRight());

                throw httpPushCustomException;

            case 403:
                // code "1106", message "Push GW Forbidden"
                cdMsgPair = httpServiceProps.getExceptionCodeMessage("ForbiddenException");
                httpPushCustomException.setCode(cdMsgPair.getLeft());
                httpPushCustomException.setMessage(cdMsgPair.getRight());

                throw httpPushCustomException;

            case 404:
                // code "1107", message "Push GW Not Found"
                cdMsgPair = httpServiceProps.getExceptionCodeMessage("NotFoundException");
                httpPushCustomException.setCode(cdMsgPair.getLeft());
                httpPushCustomException.setMessage(cdMsgPair.getRight());

                throw httpPushCustomException;

            case 410:
                // code "1113", message "Not Exist RegistID"
                cdMsgPair = httpServiceProps.getExceptionCodeMessage("NotExistRegistIdException");
                httpPushCustomException.setCode(cdMsgPair.getLeft());
                httpPushCustomException.setMessage(cdMsgPair.getRight());

                throw httpPushCustomException;

            case 412:
                // code "1108", message "Push GW Precondition Failed"
                cdMsgPair = httpServiceProps.getExceptionCodeMessage("PreConditionFailedException");
                httpPushCustomException.setCode(cdMsgPair.getLeft());
                httpPushCustomException.setMessage(cdMsgPair.getRight());

                throw httpPushCustomException;

            case 500:
                // code "1109", message "Push GW Internal Error"
                cdMsgPair = httpServiceProps.getExceptionCodeMessage("InternalErrorException");
                httpPushCustomException.setCode(cdMsgPair.getLeft());
                httpPushCustomException.setMessage(cdMsgPair.getRight());

                throw httpPushCustomException;

            case 502:
                // code "1114", message "Exception Occurs"
                cdMsgPair = httpServiceProps.getExceptionCodeMessage("ExceptionOccursException");
                httpPushCustomException.setCode(cdMsgPair.getLeft());
                httpPushCustomException.setMessage(cdMsgPair.getRight());

                throw httpPushCustomException;

            case 503:
                // code "1110", message "Push GW Service Unavailable"
                cdMsgPair = httpServiceProps.getExceptionCodeMessage("ServiceUnavailableException");
                httpPushCustomException.setCode(cdMsgPair.getLeft());
                httpPushCustomException.setMessage(cdMsgPair.getRight());

                throw httpPushCustomException;

            default:
                throw new RuntimeException("기타 오류");
        }
    }

}
