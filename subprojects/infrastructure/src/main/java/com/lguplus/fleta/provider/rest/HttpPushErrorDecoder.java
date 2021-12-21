package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.exception.httppush.HttpPushCustomException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HttpPushErrorDecoder implements ErrorDecoder {

    @Value("${error.flag.com.lguplus.fleta.exception.httppush.AcceptedException}")
    private String acceptedExceptionCode;

    @Value("${error.message.1112}")
    private String acceptedExceptionMsg;

    @Value("${error.flag.com.lguplus.fleta.exception.httppush.BadRequestException}")
    private String badRequestExceptionCode;

    @Value("${error.message.1104}")
    private String badRequestExceptionMsg;

    @Value("${error.flag.com.lguplus.fleta.exception.httppush.UnAuthorizedException}")
    private String unAuthorizedExceptionCode;

    @Value("${error.message.1105}")
    private String unAuthorizedExceptionMsg;

    @Value("${error.flag.com.lguplus.fleta.exception.httppush.ForbiddenException}")
    private String forbiddenExceptionCode;

    @Value("${error.message.1106}")
    private String forbiddenExceptionMsg;

    @Value("${error.flag.com.lguplus.fleta.exception.httppush.NotFoundException}")
    private String notFoundExceptionCode;

    @Value("${error.message.1107}")
    private String notFoundExceptionMsg;

    @Value("${error.flag.com.lguplus.fleta.exception.httppush.NotExistRegistIdException}")
    private String notExistRegistIdExceptionCode;

    @Value("${error.message.1113}")
    private String notExistRegistIdExceptionMsg;

    @Value("${error.flag.com.lguplus.fleta.exception.httppush.PreConditionFailedException}")
    private String preConditionFailedExceptionCode;

    @Value("${error.message.1108}")
    private String preConditionFailedExceptionMsg;

    @Value("${error.flag.com.lguplus.fleta.exception.httppush.InternalErrorException}")
    private String internalErrorExceptionCode;

    @Value("${error.message.1109}")
    private String internalErrorExceptionMsg;

    @Value("${error.flag.com.lguplus.fleta.exception.httppush.ExceptionOccursException}")
    private String exceptionOccursExceptionCode;

    @Value("${error.message.1114}")
    private String exceptionOccursExceptionMsg;

    @Value("${error.flag.com.lguplus.fleta.exception.httppush.ServiceUnavailableException}")
    private String serviceUnavailableExceptionCode;

    @Value("${error.message.1110}")
    private String serviceUnavailableExceptionMsg;


    @Override
    public Exception decode(String methodKey, Response response) {
        log.debug("\n{} 에러 발생 :::::::::::::: status: {}\nbody: {}", methodKey, response.status(), response.body());

        HttpPushCustomException httpPushCustomException = new HttpPushCustomException();

        switch (response.status()) {
            case 202:
                // code "1112", message "The request Accepted"
//                throw new AcceptedException();
                httpPushCustomException.setCode(acceptedExceptionCode);
                httpPushCustomException.setMessage(acceptedExceptionMsg);

                throw httpPushCustomException;

            case 400:
                // code "1104", message "Push GW BadRequest"
//                throw new BadRequestException();

                httpPushCustomException.setCode(badRequestExceptionCode);
                httpPushCustomException.setMessage(badRequestExceptionMsg);

                throw httpPushCustomException;

            case 401:
                // code "1105", message "Push GW UnAuthorized"
//                throw new UnAuthorizedException();

                httpPushCustomException.setCode(unAuthorizedExceptionCode);
                httpPushCustomException.setMessage(unAuthorizedExceptionMsg);

                throw httpPushCustomException;

            case 403:
                // code "1106", message "Push GW Forbidden"
//                throw new ForbiddenException();

                httpPushCustomException.setCode(forbiddenExceptionCode);
                httpPushCustomException.setMessage(forbiddenExceptionMsg);

                throw httpPushCustomException;

            case 404:
                // code "1107", message "Push GW Not Found"
//                throw new NotFoundException();

                httpPushCustomException.setCode(notFoundExceptionCode);
                httpPushCustomException.setMessage(notFoundExceptionMsg);

                throw httpPushCustomException;

            case 410:
                // code "1113", message "Not Exist RegistID"
//                throw new NotExistRegistIdException();

                httpPushCustomException.setCode(notExistRegistIdExceptionCode);
                httpPushCustomException.setMessage(notExistRegistIdExceptionMsg);

                throw httpPushCustomException;

            case 412:
                // code "1108", message "Push GW Precondition Failed"
//                throw new PreConditionFailedException();
                httpPushCustomException.setCode(preConditionFailedExceptionCode);
                httpPushCustomException.setMessage(preConditionFailedExceptionMsg);

                throw httpPushCustomException;

            case 500:
                // code "1109", message "Push GW Internal Error"
//                throw new InternalErrorException();

                httpPushCustomException.setCode(internalErrorExceptionCode);
                httpPushCustomException.setMessage(internalErrorExceptionMsg);

                throw httpPushCustomException;

            case 502:
                // code "1114", message "Exception Occurs"
//                throw new ExceptionOccursException();

                httpPushCustomException.setCode(exceptionOccursExceptionCode);
                httpPushCustomException.setMessage(exceptionOccursExceptionMsg);

                throw httpPushCustomException;

            case 503:
                // code "1110", message "Push GW Service Unavailable"
//                throw new ServiceUnavailableException();

                httpPushCustomException.setCode(serviceUnavailableExceptionCode);
                httpPushCustomException.setMessage(serviceUnavailableExceptionMsg);

                throw httpPushCustomException;

            default:
                throw new RuntimeException("기타 오류");
        }
    }

}
