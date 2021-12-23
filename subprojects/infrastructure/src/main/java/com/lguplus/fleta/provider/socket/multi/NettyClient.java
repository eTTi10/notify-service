package com.lguplus.fleta.provider.socket.multi;

import com.lguplus.fleta.data.dto.response.inner.PushMessageInfoDto;
import com.lguplus.fleta.provider.socket.PushMultiSocketClientImpl;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;

@Slf4j
@Component
@RequiredArgsConstructor
public class NettyClient {

	@Value("${push-comm.push.call.retryCnt}")
	private String pushCallRetryCnt;
	private int retryCount;

	@Value("${push-comm.push.socket.timeout}")
	private String timeout;

	private static final int CONN_TIMEOUT = 1000;
	public static final String ATTACHED_DATA_ID = "MessageInfo.state";

	EventLoopGroup workerGroup;
	Bootstrap bootstrap = null;
	Channel channel = null;
	private String host;
	private int port;

	@PostConstruct
	public void initialize(){

		retryCount = Integer.parseInt(pushCallRetryCnt);

	}

	public Channel getChannel() {
		return channel;
	}

	public void initailize(PushMultiSocketClientImpl socketClient, String host, int port) {
		this.host = host;
		this.port = port;

		this.workerGroup =  new NioEventLoopGroup();

		log.debug("[NettyClient] Server IP : " + host + ", port : " + port);
		bootstrap = new Bootstrap()
				.group(this.workerGroup)
				.channel(NioSocketChannel.class)
				.option(ChannelOption.TCP_NODELAY, true)
				.option(ChannelOption.SO_KEEPALIVE, true)
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Integer.parseInt(timeout))
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline p = ch.pipeline();
						p.addLast("clientDecoder", new NettyDecoder());
						p.addLast("clientEncoder", new NettyEncoder());
						p.addLast("handler", new NettyHandler(socketClient));
					}
				});

		this.connect();
	}

	public void connect() {
		ChannelFuture connectFuture = bootstrap.connect(new InetSocketAddress(host, port));
		this.channel = connectFuture.awaitUninterruptibly().channel();

		log.debug("[NettyClient] The new channel has been connected. [" + channel.id() + "]");
	}

	public void disconnect() {
		try {
			if (channel.isActive()) { //isConnected -> isActive
				channel.disconnect();
				channel.close();
			}
			log.debug("[NettyClient] The current channel has been disconnected. [" + channel.id() + "]");
		} catch (Exception ex) {
			log.error("[NettyClient] connection closing : {}", ex);
		}
	}

	public boolean isValid() {
		return !(channel == null || !channel.isActive() || !channel.isOpen());
	}

	public boolean write(PushMessageInfoDto message) {
		try {
			if (null != message && this.channel.isActive()) {
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

	public Object writeSync(PushMessageInfoDto message) {
		Object response = null;

		if (null == message) {
			return null;
		}

		ChannelFuture writeFuture = this.channel.write(message);
		writeFuture.awaitUninterruptibly(CONN_TIMEOUT);

		int writeTryTimes = 1;
		Object lock = new Object();

		synchronized (lock) {
			while (writeTryTimes < retryCount && !writeFuture.isSuccess()) {
				try {
					lock.wait(10);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
				writeFuture = channel.write(message);
				writeFuture.awaitUninterruptibly(CONN_TIMEOUT);
				writeTryTimes++;
			}
		}

		if (writeTryTimes >= retryCount) {
			log.error("[NettyClient][Sync] write to server failed afer retry " + retryCount + "times");
			return null;
		}

		long readWaited = 0L;

		while (response == null && readWaited < CONN_TIMEOUT) {
			try {
				Thread.sleep(1L);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			response = getAttachment(ATTACHED_DATA_ID);
			readWaited++;
		}

		// Remove the current attachment
		setAttachment(ATTACHED_DATA_ID, null);

		if(readWaited >= CONN_TIMEOUT) {
			log.error("[NettyClient][Sync] Read from server failed after " + CONN_TIMEOUT + "ms");
			return null;
		}

		return response;
	}

	private void setAttachment(String key, Object value) {
		AttributeKey<Object> attrKey = AttributeKey.valueOf(key);
		this.channel.attr(attrKey).set(value);
	}

	private Object getAttachment(String key) {
		AttributeKey<Object> attrKey = AttributeKey.valueOf(key);
		return this.channel.attr(attrKey).get();
	}

}
