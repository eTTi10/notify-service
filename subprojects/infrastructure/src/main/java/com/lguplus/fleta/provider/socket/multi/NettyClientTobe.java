package com.lguplus.fleta.provider.socket.multi;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Slf4j
@Component
public class NettyClientTobe {
	//private static final Log LOGGER = LogFactory.getLog("multiPushRequest");

	//	private static final long CONN_TIMEOUT = Integer.parseInt(MlCommProperties.getProperty("push.socket.timeout"));
	private static final int RETRY_COUNT = 5;//Integer.parseInt(MlCommProperties.getProperty("push.call.retryCnt"));
	private static final int CONN_TIMEOUT = 1000; //TODO

	//private static final AttributeKey<MessageInfo> SERVER_STATE = AttributeKey.valueOf("MessageInfo.state");
	public static final String ATTACHED_DATA_ID = "MessageInfo.state";

	EventLoopGroup workerGroup;
	Bootstrap bootstrap = null;
	//ChannelFuture connectFuture = null;
	Channel channel = null;
	private String host;
	private int port;
	private int workerThreads = 0; // Default : System Process *2

	public Channel getChannel() {
		return channel;
	}

	public void initailize(String host, int port) throws Exception {
		this.host = host;
		this.port = port;

		this.workerGroup =  new NioEventLoopGroup();
		//workerGroup = new NioEventLoopGroup(workerThreads, workThreadFactory);

		log.info("[NettyClient] Server IP : " + host + ", port : " + String.valueOf(port));
		bootstrap = new Bootstrap()
				.group(this.workerGroup)
				.channel(NioSocketChannel.class)
				.option(ChannelOption.TCP_NODELAY, true	)
				.option(ChannelOption.SO_KEEPALIVE, true)
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000) //TODO. Parameter  //getProperty("push.socket.timeout")
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline p = ch.pipeline();
						p.addLast("clientDecoder", new NettyDecoderTobe());
						p.addLast("clientEncoder", new NettyEncoderTobe());
						p.addLast("handler", new NettyHandlerTobe());
					}
				});
/*

		// Configure the event pipeline factory.
		bootstrap.setPipelineFactory(new PipelineFactory());
		bootstrap.setOption("tcpNoDelay", true);
		bootstrap.setOption("keepAlive", true);
		bootstrap.setOption("connectTimeoutMillis", Integer.parseInt(MlCommProperties.getProperty("push.socket.timeout")));
*/
		this.connect();
	}

	public void connect() { //throws Exception {
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));
		this.channel = future.awaitUninterruptibly().channel();
		/*
		future.addListener(new ChannelFutureListener() {
			public void operationComplete(ChannelFuture future) {
				NettyClient.this.onConnectCompleted(future, connId, addr);
			}
		});
		*/
		log.info("[NettyClient] The new channel has been connected. [" + channel.id() + "]");
	}

	void onConnectCompleted(ChannelFuture f, String connId, String addr) {
		if (!f.isSuccess()) {
			log.error("connect failed, connId=" + connId + ", e=" + f.cause().getMessage());
			//scheduleToReconnect(connId, addr);
			return;
		}
/*
		Channel ch = f.channel();
		connIdMap.put(ch.id().asLongText(), connId);
		conns.put(connId, ch);
		log.info("connection started, connId={}", connId);

		reconnectingConns.remove(connId);

		if (callback != null)
			callback.connected(connId, parseIpPort(ch.localAddress().toString()));
		*/
	}

	public void disconnect() {
		try {
			if (channel.isActive()) { //isConnected()) {
				channel.disconnect();
				channel.close();
			}
			log.info("[NettyClient] The current channel has been disconnected. [" + channel.id() + "]");
		} catch (Exception ex) {
			log.error("[NettyClient] connection closing : {}", ex);
		}
	}

	public boolean isValid() {
		if (channel == null)
			return false;
		if (!channel.isActive()) //.isConnected())
			return false;
		if (!channel.isOpen())
			return false;
		//if (!channel.isBound()) // isActive
		//	return false;

		return true;
	}

	public boolean write(Object message) {
		try {
			if (null != message && this.channel.isActive()) {
				ChannelFuture wf = this.channel.write(message);
				//this.channel.writeAndFlush(data);
				wf.awaitUninterruptibly(CONN_TIMEOUT);

				if (!wf.isSuccess()) {
					log.error("[NettyClient] write to server failed");
					return false;
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			log.error("[NettyClient] got a exception : {}" + e);
			return false;
		}

		return true;
	}

	public Object writeSync(Object message) {
		Object response = null;

		try {
			if (null != message) {

				//this.channel.wait();
				ChannelFuture wf = this.channel.write(message);
				//this.channel.writeAndFlush(data);
				wf.awaitUninterruptibly(CONN_TIMEOUT);

				int writeTryTimes = 1;
				Object lock = new Object();

				synchronized (lock) {
					while (writeTryTimes < RETRY_COUNT && !wf.isSuccess()) {
						lock.wait(10);
						wf = channel.write(message);
						wf.awaitUninterruptibly(CONN_TIMEOUT);
						writeTryTimes++;
					}
				}

				if (writeTryTimes >= RETRY_COUNT) {
					log.error("[NettyClient][Sync] write to server failed afer retry " + RETRY_COUNT + "times");
					return null;
				}

				long readWaited = 0L;
				ChannelHandlerContext ctx = channel.pipeline().lastContext();

				while (response == null && readWaited < CONN_TIMEOUT) {
					Thread.sleep(1L);
					response = getAttachment(ATTACHED_DATA_ID);
					readWaited++;
				}

				// Remove the current attachment
				//ctx.setAttachment(null); //TODO.
				setAttachment(ATTACHED_DATA_ID, null);

				if(readWaited >= CONN_TIMEOUT) {
					log.error("[NettyClient][Sync] Read from server failed after " + CONN_TIMEOUT + "ms");
					return null;
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			log.error("[NettyClient][Sync] got a exception : {}" + e);
			return null;
		}

		return response;
	}

	public Object writeSync1(Object message) {
		Object response = null;

		try {
			if (null != message) {
				ChannelFuture writeFuture = this.channel.write(message);
				writeFuture.awaitUninterruptibly(CONN_TIMEOUT);

				int writeTryTimes = 1;
				Object lock = new Object();

				/*
        ChannelPromise writePromise = newPromise();
        ChannelFuture writeFuture = clientChannel.write(request, writePromise);
        clientChannel.flush();
        assertTrue(writePromise.awaitUninterruptibly(WAIT_TIME_SECONDS, SECONDS));
        assertTrue(writePromise.isSuccess());
        assertTrue(writeFuture.awaitUninterruptibly(WAIT_TIME_SECONDS, SECONDS));
        assertTrue(writeFuture.isSuccess());
				 */

				synchronized (lock) {
					while (writeTryTimes < RETRY_COUNT && !writeFuture.isSuccess()) {
						lock.wait(10);
						writeFuture = channel.write(message);
						writeFuture.awaitUninterruptibly(CONN_TIMEOUT);
						writeTryTimes++;
					}
				}

				if (writeTryTimes >= RETRY_COUNT) {
					log.error("[NettyClient][Sync] write to server failed afer retry " + RETRY_COUNT + "times");
					return null;
				}

				long readWaited = 0L;
				ChannelHandlerContext ctx = channel.pipeline().lastContext();

				while (response == null && readWaited < CONN_TIMEOUT) {
					Thread.sleep(1L);
					response = getAttachment(ATTACHED_DATA_ID);
					readWaited++;
				}

				// Remove the current attachment
				//ctx.setAttachment(null); //TODO.
				setAttachment(ATTACHED_DATA_ID, null);

				if(readWaited >= CONN_TIMEOUT) {
					log.error("[NettyClient][Sync] Read from server failed after " + CONN_TIMEOUT + "ms");
					return null;
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			log.error("[NettyClient][Sync] got a exception : {}" + e);
			return null;
		}

		return response;
	}

	private void setAttachment(String key, Object value) {
		AttributeKey attrKey = AttributeKey.valueOf(key);
		this.channel.attr(attrKey).set(value);
	}

	private Object getAttachment(String key) {
		AttributeKey attrKey = AttributeKey.valueOf(key);
		return this.channel.attr(attrKey).get();
	}

}
