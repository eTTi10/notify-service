package com.lguplus.fleta.provider.socket.pool;

import com.google.common.primitives.Ints;
import com.lguplus.fleta.data.dto.response.inner.PushClientResponseDto;
import com.lguplus.fleta.exception.push.PushBizException;
import com.lguplus.fleta.exception.push.ServiceIdNotFoundException;
import fleta.util.JunitTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.DestroyMode;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.net.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ExtendWith({ MockitoExtension.class})
class PushSocketConnFactoryTest {

    @Test
    void validateObject() throws Exception {
        //normal case
        PushSocketConnFactory.PushServerInfoVo serverInfo = PushSocketConnFactory.PushServerInfoVo.builder()
                .host("211.115.75.227").port(Integer.parseInt("9600")).timeout(Integer.parseInt("2000")).channelPort(Integer.parseInt("8080"))
                .defaultChannelHost("PsAgt").closeSecond(Integer.parseInt("170")).destinationIp("222.231.13.85")
                .isLgPush(false).build();

        PushSocketConnFactory pushSocketConnFactory = new PushSocketConnFactory(serverInfo);

        JunitTestUtils.setValue(pushSocketConnFactory, "commChannelNum", new AtomicInteger(9999));

        GenericObjectPool<PushSocketInfo> pool = new GenericObjectPool<>(pushSocketConnFactory, getPoolConfig(2, 1) );

        PushSocketInfo socketInfo = pool.borrowObject();

        assertTrue(socketInfo.isOpened());
        pool.close();
    }

    @Test
    void validateObject_error2() throws Exception {
        //normal case
        PushSocketConnFactory.PushServerInfoVo serverInfo = PushSocketConnFactory.PushServerInfoVo.builder()
                .host("211.115.75.227").port(Integer.parseInt("8888")).timeout(Integer.parseInt("2000")).channelPort(Integer.parseInt("8080"))
                .defaultChannelHost("PsAgt").closeSecond(Integer.parseInt("170")).destinationIp("222.231.13.85")
                .isLgPush(false).build();

        PushSocketConnFactory pushSocketConnFactory = new PushSocketConnFactory(serverInfo);

        JunitTestUtils.setValue(pushSocketConnFactory, "commChannelNum", new AtomicInteger(9999));

        GenericObjectPool<PushSocketInfo> pool = new GenericObjectPool<>(pushSocketConnFactory, getPoolConfig(2, 1) );

        assertThrows(Exception.class, () -> {
            pool.borrowObject();
        });
        pool.close();
    }

    @Test
    void validateObject2() throws Exception {
        //normal case
        PushSocketConnFactory.PushServerInfoVo serverInfo = PushSocketConnFactory.PushServerInfoVo.builder()
                .host("211.115.75.227").port(Integer.parseInt("9600")).timeout(Integer.parseInt("2000")).channelPort(Integer.parseInt("8080"))
                .defaultChannelHost("PsAgt").closeSecond(Integer.parseInt("170")).destinationIp("222.231.13.85")
                .isLgPush(false).build();

        PushSocketConnFactory pushSocketConnFactory = new PushSocketConnFactory(serverInfo);

        GenericObjectPool<PushSocketInfo> pool = new GenericObjectPool<>(pushSocketConnFactory, getPoolConfig(2, 1) );

        PushSocketInfo socketInfo = pool.borrowObject();

        assertTrue(socketInfo.isOpened());

        pool.invalidateObject(socketInfo);
        socketInfo = pool.borrowObject();
        pool.invalidateObject(socketInfo, DestroyMode.ABANDONED);
        pool.evict();
        pool.close();

    }

    @Test
    void validateObject3() throws Exception {
        //normal case
        PushSocketConnFactory.PushServerInfoVo serverInfo = PushSocketConnFactory.PushServerInfoVo.builder()
                .host("211.115.75.227").port(Integer.parseInt("9600")).timeout(Integer.parseInt("2000")).channelPort(Integer.parseInt("8080"))
                .defaultChannelHost("PsAgt").closeSecond(Integer.parseInt("170")).destinationIp("222.231.13.85")
                .isLgPush(false).build();

        PushSocketConnFactory pushSocketConnFactory = new PushSocketConnFactory(serverInfo);

        int maxCount = 1;
        GenericObjectPool<PushSocketInfo> pool = new GenericObjectPool<>(pushSocketConnFactory, getPoolConfigErr(maxCount, 1) );

        PushSocketInfo socketInfo = pool.borrowObject();
        log.debug(socketInfo.toString());

        assertTrue(socketInfo.isOpened());

        JunitTestUtils.setValue(socketInfo, "isOpened", false);
        JunitTestUtils.setValue(socketInfo, "isFailure", true);
        pool.returnObject(socketInfo);

        PushSocketInfo socketInfoRtn = pool.borrowObject();
        log.debug(socketInfoRtn.toString());
        assertTrue(!socketInfoRtn.isOpened());
        pool.returnObject(socketInfoRtn);

        pool.setConfig(getPoolConfig(1,1));

        socketInfoRtn = pool.borrowObject();
        log.debug(socketInfoRtn.toString());
        pool.returnObject(socketInfoRtn);
        pool.close();
    }

    @Test
    void validateObject4() throws Exception {
        //normal case
        PushSocketConnFactory.PushServerInfoVo serverInfo = PushSocketConnFactory.PushServerInfoVo.builder()
                .host("211.115.75.227").port(Integer.parseInt("9600")).timeout(Integer.parseInt("2000")).channelPort(Integer.parseInt("8080"))
                .defaultChannelHost("PsAgt").closeSecond(Integer.parseInt("170")).destinationIp("222.231.13.85")
                .isLgPush(false).build();

        PushSocketConnFactory pushSocketConnFactory = new PushSocketConnFactory(serverInfo);

        int maxCount = 1;
        GenericObjectPool<PushSocketInfo> pool = new GenericObjectPool<>(pushSocketConnFactory, getPoolConfigErr(maxCount, 1) );

        PushSocketInfo socketInfo = pool.borrowObject();
        log.debug(socketInfo.toString());

        assertTrue(socketInfo.isOpened());

        JunitTestUtils.setValue(socketInfo, "lastTransactionTime", Instant.now().getEpochSecond()-200);
        pool.returnObject(socketInfo);

        pool.setConfig(getPoolConfig(1,1));

        PushSocketInfo socketInfoRtn = pool.borrowObject();
        log.debug(socketInfoRtn.toString());
        pool.returnObject(socketInfoRtn);
        pool.close();

    }

    @Test
    void validateObject5() throws Exception {
        //normal case
        PushSocketConnFactory.PushServerInfoVo serverInfo = PushSocketConnFactory.PushServerInfoVo.builder()
                .host("211.115.75.227").port(Integer.parseInt("9600")).timeout(Integer.parseInt("2000")).channelPort(Integer.parseInt("8080"))
                .defaultChannelHost("PsAgt").closeSecond(Integer.parseInt("170")).destinationIp("222.231.13.85")
                .isLgPush(false).build();

        PushSocketConnFactory pushSocketConnFactory = new PushSocketConnFactory(serverInfo);

        int maxCount = 1;
        GenericObjectPool<PushSocketInfo> pool = new GenericObjectPool<>(pushSocketConnFactory, getPoolConfigErr(maxCount, 1) );

        PushSocketInfo socketInfo = pool.borrowObject();
        log.debug(socketInfo.toString());

        assertTrue(socketInfo.isOpened());

        JunitTestUtils.setValue(socketInfo, "lastTransactionTime", Instant.now().getEpochSecond()-400);
        pool.returnObject(socketInfo);

        pool.setConfig(getPoolConfig(1,1));

        PushSocketInfo socketInfoRtn = pool.borrowObject();
        log.debug(socketInfoRtn.toString());
        pool.returnObject(socketInfoRtn);
        pool.close();

    }

    private GenericObjectPoolConfig<PushSocketInfo> getPoolConfig(int maxTotal, int minIdle) {
        GenericObjectPoolConfig<PushSocketInfo> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setJmxEnabled(false);
        poolConfig.setMaxTotal(maxTotal); //100
        poolConfig.setMaxIdle(maxTotal);  //100
        poolConfig.setMinIdle(minIdle);   //20
        poolConfig.setBlockWhenExhausted(true);//풀이 관리하는 커넥션이 모두 사용중인 경우에 커넥션 요청 시, true 이면 대기, false 이면 NoSuchElementException 발생
        poolConfig.setMaxWaitMillis(2000);// 최대 대기 시간
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(false);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setLifo(false); //false : FIFO, default: LIFO :: FIFO(소켓이 만들어지거나 사용되어진 시간 기준으로 오래된 자원부터 사용)
        poolConfig.setTimeBetweenEvictionRunsMillis(10 * 1000L);

        return poolConfig;
    }

    private GenericObjectPoolConfig<PushSocketInfo> getPoolConfigErr(int maxTotal, int minIdle) {
        GenericObjectPoolConfig<PushSocketInfo> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setJmxEnabled(false);
        poolConfig.setMaxTotal(maxTotal); //100
        poolConfig.setMaxIdle(maxTotal);  //100
        poolConfig.setMinIdle(minIdle);   //20
        poolConfig.setBlockWhenExhausted(false);//풀이 관리하는 커넥션이 모두 사용중인 경우에 커넥션 요청 시, true 이면 대기, false 이면 NoSuchElementException 발생
        poolConfig.setMaxWaitMillis(2000);// 최대 대기 시간
        poolConfig.setTestOnCreate(false);
        poolConfig.setTestOnBorrow(false);
        poolConfig.setTestOnReturn(false);
        poolConfig.setTestWhileIdle(false);
        poolConfig.setLifo(true); //false : FIFO, default: LIFO :: FIFO(소켓이 만들어지거나 사용되어진 시간 기준으로 오래된 자원부터 사용)
        poolConfig.setTimeBetweenEvictionRunsMillis(10 * 1000L);

        return poolConfig;
    }

}