package com.lguplus.fleta.provider.rest.announce;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.springframework.data.annotation.Immutable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class Announce {

    private final HttpClient httpClient;

    public String request(String host, int port, String url, String param, int timeout, String acceptHeader, String protocolName, String encoding) {
        HttpResponse response = null;
        try {

            //httpClient.getParams().setParameter("http.connection.timeout", timeout);//timeout

            String uri = protocolName+"://"+host+":"+Integer.toString(port)+url;//https?http?

            HttpPost post = new HttpPost(uri);
            post.setHeader("accept", acceptHeader); //xml, json
            post.setEntity(new StringEntity(param,encoding)); //utf-8

            //response = httpClient.execute(new HttpGet("http://m.naver.com/"));

            //String responseText = EntityUtils.toString(response.getEntity());

            ResponseHandler<List<Object>> responseHandler = new ResponseHandler<List<Object>>() {
                @Override
                public List<Object> handleResponse(
                        final HttpResponse _response) throws ClientProtocolException, IOException {

                    int status = _response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = _response.getEntity();
                        String responseStr = "";
                        if( entity != null ) {
                            responseStr = EntityUtils.toString(entity);
                        }

                        return Arrays.asList(_response, responseStr);
                    } else {
                        //throw new ClientProtocolException("Unexpected response status: " + status);
                        return Arrays.asList(_response, "");
                    }
                }
            };

            //ResponseHandler<String> responseHandler = new CustomResponseHandler();
            List<Object> res = httpClient.execute(post, responseHandler);
            response = (HttpResponse) res.get(0);
            String responseStr = (String) res.get(1);
            return responseStr;
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        finally {
            //httpClient.close();
            HttpClientUtils.closeQuietly(response);
        }
        return null;
    }
}
