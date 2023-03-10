package com.lguplus.fleta.provider.socket.pool;

import com.lguplus.fleta.provider.socket.multi.NettyTcpJunitServer;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.DestroyMode;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@Slf4j
@ExtendWith({MockitoExtension.class})
class PushSocketConnFactoryTest {

    static NettyTcpJunitServer server;
    static String SERVER_IP = "127.0.0.1";
    static int SERVER_PORT = 9600;

    @BeforeAll
    static void setUpAll() throws InterruptedException {
        server = new NettyTcpJunitServer();
        new Thread(() -> {
            server.runServer(SERVER_PORT);
        }).start();
        LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(200));
    }

    @AfterAll
    static void setUpClose() {
        server.stopServer();
    }

    @Test
    void validateObject() throws Exception {
        //normal case
        PushSocketConnFactory.PushServerInfoVo serverInfo = PushSocketConnFactory.PushServerInfoVo.builder()
            .host(SERVER_IP).port(Integer.parseInt("" + SERVER_PORT)).timeout(Integer.parseInt("2000")).channelPort(Integer.parseInt("8080"))
            .defaultChannelHost("PsAgt").closeSecond(Integer.parseInt("170")).destinationIp("222.231.13.85")
            .isLgPush(false).build();

        PushSocketConnFactory pushSocketConnFactory = new PushSocketConnFactory(serverInfo);

        ReflectionTestUtils.setField(pushSocketConnFactory, "commChannelNum", new AtomicInteger(9999));

        GenericObjectPool<PushSocketInfo> pool = new GenericObjectPool<>(pushSocketConnFactory, getPoolConfig(2, 1));

        PushSocketInfo socketInfo = pool.borrowObject();

        assertTrue(socketInfo.isOpened());
        pool.close();
    }

    @Test
    void validateObject_error2() throws Exception {
        //normal case
        PushSocketConnFactory.PushServerInfoVo serverInfo = PushSocketConnFactory.PushServerInfoVo.builder()
            .host(SERVER_IP).port(Integer.parseInt("1" + SERVER_PORT)).timeout(Integer.parseInt("2000")).channelPort(Integer.parseInt("8080"))
            .defaultChannelHost("PsAgt").closeSecond(Integer.parseInt("170")).destinationIp("222.231.13.85")
            .isLgPush(false).build();

        PushSocketConnFactory pushSocketConnFactory = new PushSocketConnFactory(serverInfo);

        ReflectionTestUtils.setField(pushSocketConnFactory, "commChannelNum", new AtomicInteger(9999));

        GenericObjectPool<PushSocketInfo> pool = new GenericObjectPool<>(pushSocketConnFactory, getPoolConfig(2, 1));

        assertThrows(Exception.class, () -> {
            pool.borrowObject();
        });
        pool.close();
    }

    @Test
    void validateObject2() throws Exception {
        //normal case
        PushSocketConnFactory.PushServerInfoVo serverInfo = PushSocketConnFactory.PushServerInfoVo.builder()
            .host(SERVER_IP).port(Integer.parseInt("" + SERVER_PORT)).timeout(Integer.parseInt("2000")).channelPort(Integer.parseInt("8080"))
            .defaultChannelHost("PsAgt").closeSecond(Integer.parseInt("170")).destinationIp("222.231.13.85")
            .isLgPush(false).build();

        PushSocketConnFactory pushSocketConnFactory = new PushSocketConnFactory(serverInfo);

        GenericObjectPool<PushSocketInfo> pool = new GenericObjectPool<>(pushSocketConnFactory, getPoolConfig(2, 1));

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
            .host(SERVER_IP).port(Integer.parseInt("" + SERVER_PORT)).timeout(Integer.parseInt("2000")).channelPort(Integer.parseInt("8080"))
            .defaultChannelHost("PsAgt").closeSecond(Integer.parseInt("170")).destinationIp("222.231.13.85")
            .isLgPush(false).build();

        PushSocketConnFactory pushSocketConnFactory = new PushSocketConnFactory(serverInfo);

        int maxCount = 1;
        GenericObjectPool<PushSocketInfo> pool = new GenericObjectPool<>(pushSocketConnFactory, getPoolConfigErr(maxCount, 1));

        PushSocketInfo socketInfo = pool.borrowObject();
        log.debug(socketInfo.toString());

        assertTrue(socketInfo.isOpened());

        ReflectionTestUtils.setField(socketInfo, "isOpened", false);
        ReflectionTestUtils.setField(socketInfo, "isFailure", true);
        pool.returnObject(socketInfo);

        PushSocketInfo socketInfoRtn = pool.borrowObject();
        log.debug(socketInfoRtn.toString());
        assertTrue(!socketInfoRtn.isOpened());
        pool.returnObject(socketInfoRtn);

        pool.setConfig(getPoolConfig(1, 1));

        socketInfoRtn = pool.borrowObject();
        log.debug(socketInfoRtn.toString());
        pool.returnObject(socketInfoRtn);
        pool.close();
    }

    @Test
    void validateObject4() throws Exception {
        //normal case
        PushSocketConnFactory.PushServerInfoVo serverInfo = PushSocketConnFactory.PushServerInfoVo.builder()
            .host(SERVER_IP).port(Integer.parseInt("" + SERVER_PORT)).timeout(Integer.parseInt("2000")).channelPort(Integer.parseInt("8080"))
            .defaultChannelHost("PsAgt").closeSecond(Integer.parseInt("170")).destinationIp("222.231.13.85")
            .isLgPush(false).build();

        PushSocketConnFactory pushSocketConnFactory = new PushSocketConnFactory(serverInfo);

        int maxCount = 1;
        GenericObjectPool<PushSocketInfo> pool = new GenericObjectPool<>(pushSocketConnFactory, getPoolConfigErr(maxCount, 1));

        PushSocketInfo socketInfo = pool.borrowObject();
        log.debug(socketInfo.toString());

        assertTrue(socketInfo.isOpened());

        ReflectionTestUtils.setField(socketInfo, "lastTransactionTime", Instant.now().getEpochSecond() - 200);
        pool.returnObject(socketInfo);

        pool.setConfig(getPoolConfig(1, 1));

        PushSocketInfo socketInfoRtn = pool.borrowObject();
        log.debug(socketInfoRtn.toString());
        pool.returnObject(socketInfoRtn);
        pool.close();

    }

    @Test
    void validateObject5() throws Exception {
        //normal case
        PushSocketConnFactory.PushServerInfoVo serverInfo = PushSocketConnFactory.PushServerInfoVo.builder()
            .host(SERVER_IP).port(Integer.parseInt("" + SERVER_PORT)).timeout(Integer.parseInt("2000")).channelPort(Integer.parseInt("8080"))
            .defaultChannelHost("PsAgt").closeSecond(Integer.parseInt("170")).destinationIp("222.231.13.85")
            .isLgPush(false).build();

        PushSocketConnFactory pushSocketConnFactory = new PushSocketConnFactory(serverInfo);

        int maxCount = 1;
        GenericObjectPool<PushSocketInfo> pool = new GenericObjectPool<>(pushSocketConnFactory, getPoolConfigErr(maxCount, 1));

        PushSocketInfo socketInfo = pool.borrowObject();
        log.debug(socketInfo.toString());

        assertTrue(socketInfo.isOpened());

        ReflectionTestUtils.setField(socketInfo, "lastTransactionTime", Instant.now().getEpochSecond() - 400);
        pool.returnObject(socketInfo);

        pool.setConfig(getPoolConfig(1, 1));

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
        poolConfig.setBlockWhenExhausted(true);//?????? ???????????? ???????????? ?????? ???????????? ????????? ????????? ?????? ???, true ?????? ??????, false ?????? NoSuchElementException ??????
        poolConfig.setMaxWaitMillis(2000);// ?????? ?????? ??????
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(false);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setLifo(false); //false : FIFO, default: LIFO :: FIFO(????????? ?????????????????? ??????????????? ?????? ???????????? ????????? ???????????? ??????)
        poolConfig.setTimeBetweenEvictionRunsMillis(10 * 1000L);

        return poolConfig;
    }

    private GenericObjectPoolConfig<PushSocketInfo> getPoolConfigErr(int maxTotal, int minIdle) {
        GenericObjectPoolConfig<PushSocketInfo> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setJmxEnabled(false);
        poolConfig.setMaxTotal(maxTotal); //100
        poolConfig.setMaxIdle(maxTotal);  //100
        poolConfig.setMinIdle(minIdle);   //20
        poolConfig.setBlockWhenExhausted(false);//?????? ???????????? ???????????? ?????? ???????????? ????????? ????????? ?????? ???, true ?????? ??????, false ?????? NoSuchElementException ??????
        poolConfig.setMaxWaitMillis(2000);// ?????? ?????? ??????
        poolConfig.setTestOnCreate(false);
        poolConfig.setTestOnBorrow(false);
        poolConfig.setTestOnReturn(false);
        poolConfig.setTestWhileIdle(false);
        poolConfig.setLifo(true); //false : FIFO, default: LIFO :: FIFO(????????? ?????????????????? ??????????????? ?????? ???????????? ????????? ???????????? ??????)
        poolConfig.setTimeBetweenEvictionRunsMillis(10 * 1000L);

        return poolConfig;
    }

    @Test
    void createInvalid() throws Exception {
        //normal case
        PushSocketConnFactory.PushServerInfoVo serverInfo = PushSocketConnFactory.PushServerInfoVo.builder()
            .host(SERVER_IP).port(Integer.parseInt("" + SERVER_PORT)).timeout(Integer.parseInt("2000")).channelPort(Integer.parseInt("8080"))
            .defaultChannelHost("PsAgt").closeSecond(Integer.parseInt("170")).destinationIp("222.231.13.85")
            .isLgPush(false).build();

        PushSocketConnFactory pushSocketConnFactory = new PushSocketConnFactory(serverInfo);
        PushSocketConnFactory spyFactory = spy(pushSocketConnFactory);

        PushSocketInfo pushSocketInfo = new PushSocketInfo();
        ReflectionTestUtils.setField(pushSocketInfo, "isOpened", false);
        doReturn(pushSocketInfo).when(spyFactory).createNewSocketInfo();

        PushSocketInfo testSocketInfo = spyFactory.create();
        Assertions.assertEquals(null, testSocketInfo);

    }

}