package com.lguplus.fleta.service.push;

import com.lguplus.fleta.data.dto.request.inner.PushSingleRequestDto;
import com.lguplus.fleta.exception.push.*;
import com.lguplus.fleta.properties.HttpServiceProps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PushSingleDomainService {

    private final HttpServiceProps httpServiceProps;

    /**
     * 단건푸시등록
     *
     * @param pushSingleRequestDto 단건푸시등록을 위한 DTO
     * @return 단건푸시등록 결과
     */
    public String requestPushSingle(PushSingleRequestDto pushSingleRequestDto) {
        log.debug("pushSingleRequestDto ::::::::::::::: {}", pushSingleRequestDto);

        httpServiceProps.getKeys().forEach(m -> log.debug(m.toString()));

        String serviceId = pushSingleRequestDto.getServiceId();
        String pushType = pushSingleRequestDto.getPushType();
        String msg = pushSingleRequestDto.getMsg();

        //test
        if(serviceId.equals("1101")) {
            throw new SocketNotFoundException();
        } else if(serviceId.equals("1102")) {
            throw new SocketException();
        } else if(serviceId.equals("1103")) {
            throw new SocketTimeException();
        } else if(serviceId.equals("1104")) {
            throw new BadRequestException();
        } else if(serviceId.equals("1105")) {
            throw new UnAuthorizedException();
        } else if(serviceId.equals("1106")) {
            throw new ForbiddenException();
        } else if(serviceId.equals("1107")) {
            throw new NotFoundException();
        } else if(serviceId.equals("1108")) {
            throw new PreConditionFailedException();
        } else if(serviceId.equals("1109")) {
            throw new InternalErrorException();
        } else if(serviceId.equals("1110")) {
            throw new ServiceUnavailableException();
        } else if(serviceId.equals("1111")) {
            throw new FailException();
        } else if(serviceId.equals("1112")) {
            throw new AcceptedException();
        } else if(serviceId.equals("1113")) {
            throw new NotExistRegistIdException();
        } else if(serviceId.equals("1114")) {
            throw new ExceptionOccursException();
        } else if(serviceId.equals("1115")) {
            throw new ServiceIdNotFoundException();
        } else if(serviceId.equals("1116")) {
            throw new TimeoutException();
        } else if(serviceId.equals("1120")) {
            throw new MaxRequestOverException();
        } else if(serviceId.equals("1130")) {
            throw new SendingFailedException();
        }

        try {
            // 4자리수 넘지 않도록 방어코드
            if (HttpServiceProps.singleTransactionIDNum.get() >= 9999) {
                HttpServiceProps.singleTransactionIDNum.set(0);
            }

            String tRealTransaction = "";
            String tTransactionDate = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);

            log.debug("tTransactionDate :::::::: {}", tTransactionDate);

            int tTransactionNum = HttpServiceProps.singleTransactionIDNum.incrementAndGet();

            try {
                NumberFormat nf = new DecimalFormat("0000");
                tRealTransaction = tTransactionDate + nf.format(tTransactionNum);

                log.debug("tRealTransaction :::::::: {}", tRealTransaction);

            } catch (Exception e) {
//                multiLogger.info("[pushHttpSingle][setPushData][TransactionID Error]["+e.getClass().getName()+"]["+e.getMessage()+"]");
//                throw new CustomExceptionHandler(Properties.getProperty("flag.etc"),Properties.getProperty("message.etc")+"["+e.getMessage()+"]");
            }

            //서비스 KEY
            String tServicePwd = "";
            try{
                Map<String, String> serviceMap = httpServiceProps.findMapByServiceId(serviceId).orElseGet(HashMap::new);

                log.debug("serviceMap ::::::::::::::::: {}", serviceMap);

                tServicePwd = serviceMap.get("service_pwd");

                log.debug("service_id ::::::::::::::: {}\tservice_pwd ::::::::::::: {}", serviceId, tServicePwd);

                if (tServicePwd == null || tServicePwd.isBlank()) {
                    log.debug("========no found service_pwd!!===========");
//                    throw new CustomExceptionHandler(Properties.getProperty("flag.pushgw.servicenotfound"), Properties.getProperty("message.pushgw.servicenotfound"));
                }

            } catch (Exception e) {
//                multiLogger.info("[pushHttpSingle][setPushData][ServicePass Error]["+e.getClass().getName()+"]["+e.getMessage()+"]");
//                throw new CustomExceptionHandler(Properties.getProperty("flag.etc"),Properties.getProperty("message.etc")+"["+e.getMessage()+"]");
            }

            // PAYLOAD
            /*String tPayLoadStr = "";
            try {
                if (pushType.equals("G")) {
                    StringBuffer sb = new StringBuffer();

                    sb.append("{");
//					sb.append("\"MSG1\":"+"\""+msg+"\",");
//					sb.append("\"PushCtrl\":"+"\"MSG\"");
                    sb.append(msg.replace('\b', ' ').replace('\t', ' ').replace('\n', ' ').replace('\f', ' ').replace('\r', ' ').replace("\\\\\\\"", "&quot;").replace("\\", "").replace("&quot;","\\\\\\\""));
                    sb.append("}");

                    tPayLoadStr = sb.toString();

                } else {
                    StringBuffer sb = new StringBuffer();
                    String tCmStr = "";

                    sb.append("{");
                    sb.append("\"aps\":{");
                    sb.append("\"alert\":{");
//					sb.append("\"MESSAGE\":"+"\""+msg+"\"}");
                    sb.append(msg.replace('\b', ' ').replace('\t', ' ').replace('\n', ' ').replace('\f', ' ').replace('\r', ' ').replace("\\\\\\\"", "&quot;").replace("\\", "").replace("&quot;","\\\\\\\"")+"}");

                    for (String itemList : arrItem) {
                        String[] item = itemList.split("\\!\\^");
                        if (item.length >= 2) {
                            if (item[0].equalsIgnoreCase("cm")) {
                                tCmStr = ",\""+item[0]+"\":\""+item[1]+"\"";
                            } else {
                                sb.append(",\""+item[0]+"\":\""+item[1]+"\"");
                            }
                        }
                    }
                    sb.append("}"+ tCmStr +"}");

                    tPayLoadStr = sb.toString();
                }
            } catch(Exception e) {
                multiLogger.info("[pushHttpSingle][setPushData][PAYLOAD Error]["+e.getClass().getName()+"]["+e.getMessage()+"]");
                throw new CustomExceptionHandler(Properties.getProperty("flag.etc"),Properties.getProperty("message.etc")+"["+e.getMessage()+"]");
            }*/

        } catch (Exception e){

        }



        return null;
    }

}
