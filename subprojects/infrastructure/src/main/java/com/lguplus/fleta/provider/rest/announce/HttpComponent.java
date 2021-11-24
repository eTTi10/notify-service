package com.lguplus.fleta.provider.rest.announce;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;

public class HttpComponent {

    @Bean
    public HttpClient httpClient() {

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(50);
        cm.setDefaultMaxPerRoute(10);

        SocketConfig sc = SocketConfig.custom()
                .setSoTimeout(3000)
                .setSoKeepAlive(true)
                .setTcpNoDelay(true)
                .setSoReuseAddress(true)
                .build();

        // http client 생성
        return HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setConnectionRequestTimeout(3000)
                        .setSocketTimeout(3000)
                        .build()
                )
                .setConnectionManager(cm)
                .setDefaultSocketConfig(sc)
                .build();
    }

}
