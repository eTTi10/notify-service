package com.lguplus.fleta.provider.rest.multipush;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyClient {
	//private static final Log LOGGER = LogFactory.getLog("multiPushRequest");

	//	private static final long CONN_TIMEOUT = Integer.parseInt(MlCommProperties.getProperty("push.socket.timeout"));
	private static final int RETRY_COUNT = 5;//Integer.parseInt(MlCommProperties.getProperty("push.call.retryCnt"));
	private static final int CONN_TIMEOUT = 1000; //TODO

	private static final AttributeKey<MessageInfo> SERVER_STATE = AttributeKey.valueOf("MessageInfo.state");

	EventLoopGroup eventGroup;
	Bootstrap bootstrap = null;
	ChannelFuture connectFuture = null;
	Channel channel = null;
	private String host;
	private int port;

	public Channel getChannel() {
		return channel;
	}

	public void initailize(String host, int port) throws Exception {
		this.host = host;
		this.port = port;

		this.eventGroup =  new NioEventLoopGroup();

		log.info("[NettyClient] Server IP : " + host + ", port : " + String.valueOf(port));
		bootstrap = new Bootstrap()
				.group(this.eventGroup)
				.channel(NioSocketChannel.class)
				.option(ChannelOption.TCP_NODELAY, true)
				.option(ChannelOption.SO_KEEPALIVE, true)
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000) //TODO. Parameter  //getProperty("push.socket.timeout")
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline p = ch.pipeline();
						p.addLast("clientDecoder", new NettyDecoder());
						p.addLast("clientEncoder", new NettyEncoder());
						p.addLast("handler", new NettyHandler());
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
		InetSocketAddress inetSocketAddress = new InetSocketAddress(host, port);
		connectFuture = bootstrap.connect(inetSocketAddress);

		channel = connectFuture.awaitUninterruptibly().channel();
		log.info("[NettyClient] The new channel has been connected. [" + channel.id() + "]");
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
			if (null != message) {
				ChannelFuture writeFuture = this.channel.write(message);
				writeFuture.awaitUninterruptibly(CONN_TIMEOUT);

				if (!writeFuture.isSuccess()) {
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
				ChannelFuture writeFuture = this.channel.write(message);
				writeFuture.awaitUninterruptibly(CONN_TIMEOUT);

				int writeTryTimes = 1;
				Object lock = new Object();

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
					//response = ctx.getAttachment(); //TODO.
					//if(channel.hasAttr(SERVER_STATE))
					response = channel.attr(SERVER_STATE).get();
					readWaited++;
				}

				// Remove the current attachment
				//ctx.setAttachment(null); //TODO.

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

}
