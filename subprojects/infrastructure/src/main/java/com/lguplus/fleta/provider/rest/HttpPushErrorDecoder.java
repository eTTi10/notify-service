package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.exception.httppush.*;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HttpPushErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        log.debug("\n{} 에러 발생 :::::::::::::: status: {}\nbody: {}", methodKey, response.status(), response.body());

        switch (response.status()) {
            case 202:
                // code "1112", message "The request Accepted"
                throw new AcceptedException();

            case 400:
                // code "1104", message "Push GW BadRequest"
                throw new BadRequestException();

            case 401:
                // code "1105", message "Push GW UnAuthorized"
                throw new UnAuthorizedException();

            case 403:
                // code "1106", message "Push GW Forbidden"
                throw new ForbiddenException();

            case 404:
                // code "1107", message "Push GW Not Found"
                throw new NotFoundException();

            case 410:
                // code "1113", message "Not Exist RegistID"
                throw new NotExistRegistIdException();

            case 412:
                // code "1108", message "Push GW Precondition Failed"
                throw new PreConditionFailedException();

            case 500:
                // code "1109", message "Push GW Internal Error"
                throw new InternalErrorException();

            case 502:
                // code "1114", message "Exception Occurs"
                throw new ExceptionOccursException();

            case 503:
                // code "1110", message "Push GW Service Unavailable"
                throw new ServiceUnavailableException();

            default:
                throw new RuntimeException("기타 오류");
        }
    }

}
