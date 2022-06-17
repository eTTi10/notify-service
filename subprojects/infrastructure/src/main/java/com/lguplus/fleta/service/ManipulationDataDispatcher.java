package com.lguplus.fleta.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class ManipulationDataDispatcher {

    public static final String WORKER_NAME = "ManipulationDataDispatcher";

    private final ExecutorService executorService;
    private final ObjectMapper objectMapper;

    public ManipulationDataDispatcher() {

        executorService = new ThreadPoolExecutor(8, 256,
                0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
                runnable -> new Thread(runnable, WORKER_NAME));
        objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public void dispatch(final DataManipulationListener dataManipulationListener, final String message) {

        try {
            final Payload payload = objectMapper.readValue(message, Manipulation.class).payload;
            if (payload == null) {
                throw new IllegalArgumentException("Invalid manipulation message: " + message);
            }

            if (payload.after == null) {
                if (payload.before == null) {
                    throw new IllegalArgumentException("Invalid manipulation message: " + message);
                } else {
                    executorService.submit(() -> dataManipulationListener.onDelete(payload.before));
                }
            } else if (payload.before == null) {
                executorService.submit(() -> dataManipulationListener.onInsert(payload.after));
            } else {
                executorService.submit(() -> dataManipulationListener.onUpdate(payload.after, payload.before));
            }
        } catch (final JsonProcessingException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    static class Manipulation {
        @JsonProperty Payload payload;
    }

    static class Payload {
        @JsonProperty Map<String, String> after;
        @JsonProperty Map<String, String> before;
    }
}
