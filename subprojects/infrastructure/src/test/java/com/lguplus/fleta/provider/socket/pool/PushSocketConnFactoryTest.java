package com.lguplus.fleta.provider.socket.pool;

import com.google.common.primitives.Ints;
import com.lguplus.fleta.exception.push.PushBizException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ExtendWith({ MockitoExtension.class})
class PushSocketConnFactoryTest {

    private PushSocketConnFactory pushSocketConnFactory;
    private PushSocketConnFactory pushSocketConnFactoryLg;
    private PushSocketConnFactory.PushServerInfoVo serverInfo;
    private PushSocketConnFactory.PushServerInfoVo serverInfoLg;

    @BeforeEach
    void setUp() {
        serverInfo = PushSocketConnFactory.PushServerInfoVo.builder()
                .host("211.115.75.227").port(Integer.parseInt("9600")).timeout(Integer.parseInt("2000")).channelPort(Integer.parseInt("8080"))
                .defaultChannelHost("PsAgt").closeSecond(Integer.parseInt("170")).destinationIp("222.231.13.85")
                .isLgPush(false).build();

        pushSocketConnFactory = new PushSocketConnFactory(serverInfo);

        serverInfoLg = PushSocketConnFactory.PushServerInfoVo.builder()
                .host("211.115.75.227").port(Integer.parseInt("8100")).timeout(Integer.parseInt("2000")).channelPort(Integer.parseInt("8080"))
                .defaultChannelHost("PsAgt").closeSecond(Integer.parseInt("170")).destinationIp("222.231.13.85")
                .isLgPush(true).build();

        pushSocketConnFactoryLg = new PushSocketConnFactory(serverInfoLg);
    }

    @AfterEach
    void tearDown() {
    }

    //@Test
    void create() {
        Socket s = new Socket();
        if(s.isConnected()) {

        }
    }

/*
    @Test
    void validateObject() {
    }

    @Test
    void wrap() {
    }

    @Test
    void destroyObject() {
    }

    @Test
    void isLgPush() {
    }

    @Test
    void getServerInfo() {
    }

    @Test
    void getCommChannelNum() {
    }

 */
}