package com.lguplus.fleta.provider.socket.smsagent;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Slf4j
@ExtendWith({MockitoExtension.class})
class ScavengerTest {

    @Test
    void testRun() throws NoSuchFieldException, IllegalAccessException {
        Field field = Scavenger.class.getDeclaredField("SCAVENGE_INTERVAL");
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, 0);

        DeliveryInfo deliveryInfo = new DeliveryInfo(new CountDownLatch(1));
        ReflectionTestUtils.setField(deliveryInfo, "requestTime", System.currentTimeMillis() - 70000);
        Map<Integer, DeliveryInfo> deliveryInfoMap = new HashMap<>(Map.of(
                1, deliveryInfo,
                2, new DeliveryInfo(new CountDownLatch(1)),
                3, deliveryInfo
        ));
        Scavenger scavenger = new Scavenger(deliveryInfoMap);
        new Thread(() -> assertDoesNotThrow(scavenger::run)).start();

        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(2));
        assertDoesNotThrow(scavenger::shutdown);
    }
}
