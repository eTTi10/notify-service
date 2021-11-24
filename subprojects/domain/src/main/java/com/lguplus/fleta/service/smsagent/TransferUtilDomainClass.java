package com.lguplus.fleta.service.smsagent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransferUtilDomainClass {

    /**
     * HttpClient를 이용하여 웹주소를 호출한다.
     * @param url	예)http://123.123.123.2:80
     * @param acceptHeader	예)application/xml
     * @param Method	예)POST
     * @param encoding	예)UTF-8
     * @param body	POST,PUT일 경우 BODY영역을 이용해서 데이터를 전달 할 수 있다. 예)<aaaa><bbb>BODY</bbb></aaa>
     * @param conn_timeout	예)2
     * @param socket_timeout	예)2
     * @return
     * @throws Exception
     */
/*
    public static String callHttpClient(String url, String acceptHeader, String Method, String encoding, String body, int conn_timeout, int socket_timeout) throws Exception{


        String responseBody = "";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(socket_timeout)
                .setConnectTimeout(conn_timeout)
                .build();

        try {
            RequestBuilder builder = null;
            if("POST".equals(Method.toUpperCase()))
                builder = RequestBuilder.post().setUri(url).setEntity(new StringEntity(body, encoding));
            else if("PUT".equals(Method.toUpperCase()))
                builder = RequestBuilder.put().setUri(url).setEntity(new StringEntity(body, encoding));
            else if("DELETE".equals(Method.toUpperCase()))
                builder = RequestBuilder.delete().setUri(url);
            else
                builder = RequestBuilder.get().setUri(url);

            builder.setHeader(HttpHeaders.ACCEPT, acceptHeader)
                    .setHeader(HttpHeaders.ACCEPT_CHARSET, encoding)
                    .setHeader(HttpHeaders.CONTENT_TYPE, acceptHeader)
                    .setHeader(HttpHeaders.CONTENT_ENCODING, encoding)
                    .setConfig(requestConfig);

            HttpUriRequest request = builder.build();

            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
                @Override
                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }

            };

            responseBody = httpclient.execute(request, responseHandler);
        } catch (ClientProtocolException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                if(null!=httpclient) httpclient.close();
            } catch (IOException e) {}
        }

        return responseBody;
    }
*/

}
