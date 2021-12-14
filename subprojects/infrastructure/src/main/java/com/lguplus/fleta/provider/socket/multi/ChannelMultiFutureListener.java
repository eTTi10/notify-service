package com.lguplus.fleta.provider.socket.multi;

import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;

import java.nio.channels.Channel;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ChannelMultiFutureListener implements GenericFutureListener<ChannelFuture> {
    private int successCount;
    private int failureCount;
    private Channel channel;
    private Map<String, ChannelFuture> futures;
    private CompletableFuture<Integer> completableFuture; //new CompletableFuture<>();

    /*
    CompletableFuture<Void> completableFuture = new CompletableFuture<>();
    //대기
    completableFuture.get();
     CompletableFuture<Void> f = new CompletableFuture<>();
        // write a message asynchronously that will notify the future when complete
        writeAsync(message, new ErrorToFutureCompletionHandler<Long>(f, () -> f.complete(null)));
        // wait on the future to return
        try {
            f.get();
        } catch (ExecutionException ex) {
            throw new CJCommunicationsException("Failed to write message", ex.getCause());
        } catch (InterruptedException ex) {
            throw new CJCommunicationsException("Failed to write message", ex);
        }
     */

    public ChannelMultiFutureListener(Channel channel, LinkedHashMap<String, ChannelFuture> futures, CompletableFuture<Integer> completableFuture) {
        successCount = 0;
        failureCount = 0;
        this.channel = channel;
        this.completableFuture = completableFuture;

        this.futures = Collections.unmodifiableMap(futures);

        for (ChannelFuture f: this.futures.values()) {
            f.addListener(this);
        }
    }

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        boolean success = future.isSuccess();
        boolean callSetDone;

        synchronized (ChannelMultiFutureListener.this) {
            if (success) {
                successCount ++;
            } else {
                failureCount ++;
            }

            callSetDone = successCount + failureCount == futures.size();
            //assert successCount + failureCount <= futures.size();
        }

        if (callSetDone) {
            if (failureCount > 0) {
                /*
                List<Map.Entry<Channel, Throwable>> failed = new ArrayList<Map.Entry<Channel, Throwable>>(failureCount);
                for (ChannelFuture f: futures.values()) {
                    if (!f.isSuccess()) {
                        failed.add(new DefaultEntry<Channel, Throwable>(f.channel(), f.cause()));
                    }
                }
                setFailure0(new ChannelGroupException(failed));
                */
                for (ChannelFuture f: futures.values()) {
                    if (!f.isSuccess()) {
                       // failed.add(new DefaultEntry<Channel, Throwable>(f.channel(), f.cause()));
                        //f.
                    }
                }

                completableFuture.complete(-1);
            } else {
                //setSuccess0();
                completableFuture.complete(3);
            }
        }
    }
}
