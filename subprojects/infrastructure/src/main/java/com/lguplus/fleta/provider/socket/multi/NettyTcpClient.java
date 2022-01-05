package com.lguplus.fleta.provider.socket.multi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.primitives.Ints;
import com.lguplus.fleta.client.PushMultiClient;
import com.lguplus.fleta.data.dto.response.inner.PushMessageInfoDto;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;

@Slf4j
@Component
@RequiredArgsConstructor
public class NettyTcpClient {

	@Value("${push-comm.push.server.ip}")
	private String host;

	@Value("${push-comm.push.server.port}")
	private String port;

	@Value("${push-comm.push.socket.timeout}")
	private String timeout;

	@Value("${push-comm.push.cp.destination_ip}")
	private String destinationIp;

	@Value("${server.port}")
	private String channelPort;

	@Value("${push-comm.push.socket.channelID}")
	private String defaultChannelHost;

	@Value("${push-comm.push.call.retryCnt}")
	private String pushCallRetryCnt;

	private static final int CONN_TIMEOUT = 1000;
	public static final String ATTACHED_DATA_ID = "MessageInfo.state";
	public static final String ATTACHED_CONN_ID = "MessageInfo.conn";

	private static final int PUSH_MSG_HEADER_LEN = 64;
	private static final String SUCCESS = "SC";
	private static final String FAIL = "FA";
	private static final int CHANNEL_CONNECTION_REQUEST = 1;
	private static final int CHANNEL_CONNECTION_REQUEST_ACK = 2;
	private static final int PROCESS_STATE_REQUEST = 13;
	private static final int PROCESS_STATE_REQUEST_ACK = 14;
	private static final int COMMAND_REQUEST_ACK = 16;
	private static final String PUSH_ENCODING = "euc-kr";

	private Bootstrap bootstrap = null;
	private Channel channel = null;
	private PushMultiClient pushMultiClient;

	private final AtomicInteger commChannelNum = new AtomicInteger(0);

	private void initialize() {

		int threadCount = Runtime.getRuntime().availableProcessors() * 2;

		log.debug("[NettyClient] Server IP : " + host + ", port : " + port);

		if(Epoll.isAvailable()) { // Linux Epoll
			bootstrap = new Bootstrap()
					.group(new EpollEventLoopGroup(threadCount))
					.channel(EpollSocketChannel.class)
					.option(ChannelOption.TCP_NODELAY, true)
					.option(ChannelOption.SO_KEEPALIVE, true)
					.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Integer.parseInt(timeout))
					.handler(new ChannelInitializer<SocketChannel>() {
						public void initChannel(SocketChannel ch) {
							ChannelPipeline p = ch.pipeline();
							p.addLast("decoder", new MessageDecoder());
							p.addLast("encoder", new MessageEncoder());
							p.addLast("handler", new MessageHandler(pushMultiClient));
						}
					});
		}
		else {
			bootstrap = new Bootstrap()
					.group(new NioEventLoopGroup(threadCount))
					.channel(NioSocketChannel.class)
					.option(ChannelOption.TCP_NODELAY, true)
					.option(ChannelOption.SO_KEEPALIVE, true)
					.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Integer.parseInt(timeout))
					.handler(new ChannelInitializer<SocketChannel>() {
						public void initChannel(SocketChannel ch) {
							ChannelPipeline p = ch.pipeline();
							p.addLast("decoder", new MessageDecoder());
							p.addLast("encoder", new MessageEncoder());
							p.addLast("handler", new MessageHandler(pushMultiClient));
						}
					});
		}
	}

	public String connect(PushMultiClient pushMultiClient) {
		if(bootstrap == null) {
			this.pushMultiClient = pushMultiClient;
			initialize();
		}

		//Connect
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, Integer.parseInt(port)));
		future.awaitUninterruptibly(); // ChannelOption.CONNECT_TIMEOUT_MILLIS 만큼 대기

		if (future.isDone() && future.isSuccess()) {
			channel = future.channel();
		}
		else {
			log.debug("[NettyClient] The new channel has been not connected. {}", future.cause().getMessage());
			channel = null;
			return null;
		}

		log.debug("[NettyClient] The new channel has been connected. [" + channel.id() + "]");

		String genChannelID = this.getNextChannelID();
		PushMessageInfoDto response = (PushMessageInfoDto) writeSync(
				PushMessageInfoDto.builder().messageID(CHANNEL_CONNECTION_REQUEST)
						.channelID(genChannelID).destIp(destinationIp)
						.build());

		if (response != null && SUCCESS.equals(response.getResult())) {
			log.debug("[PushMultiClient] channelConnectionRequest Success. Channel ID : " + genChannelID);
			return genChannelID;
		}

		log.error("[PushMultiClient] ChannelConnectionRequest Fail.");
		return null;

	}

	public void disconnect() {
		try {
			if (channel != null && channel.isActive()) { //isConnected -> isActive
				channel.disconnect();
				channel.close();
				channel = null;
				log.debug("[NettyClient] The current channel has been disconnected.");
			}
		} catch (Exception ex) {
			log.error("[NettyClient] connection closing : {}", ex.getMessage());
		}
	}

	public boolean isInValid() {
		return (channel == null || !channel.isActive() || !channel.isOpen());
	}

	public void write(final PushMessageInfoDto message) {
		if (!this.channel.isActive()) {
			log.error("[NettyClient] write0 isNotActive");
			return;
		}
		ChannelFuture writeFuture = this.channel.write(message);

		writeFuture.addListener((ChannelFutureListener) future -> {
			if(future.isSuccess())
				pushMultiClient.receiveAsyncMessage(PushMultiClient.MsgType.SEND_SUCCESS_MSG, message);
			else
				pushMultiClient.receiveAsyncMessage(PushMultiClient.MsgType.SEND_FAIL_MSG, message);
		});
	}

	public void flush() {
		//log.debug("[NettyClient] flush channel")
		this.channel.flush();
	}

	public Object writeSync(PushMessageInfoDto message) {
		Object response = null;

		if (null == message) {
			return null;
		}

		int retryCount = Integer.parseInt(pushCallRetryCnt);

		//Clear
		setAttachment(message.getMessageID());

		int writeTryTimes = 0;
		AtomicBoolean isFutureSuccess = new AtomicBoolean(false);

		//Send
		while(writeTryTimes < retryCount && !isFutureSuccess.get()) {

			ChannelFuture awaitFuture = this.channel.writeAndFlush(message);
			waitFutureDone(awaitFuture);

			isFutureSuccess.set(awaitFuture.isSuccess());

			writeTryTimes++;
		}

		if (writeTryTimes >= retryCount) {
			log.error("[NettyClient][Sync] write to server failed afer retry " + retryCount + "times");
		}

		if (!isFutureSuccess.get()) {
			log.error("[NettyClient][Sync] write to server failed ");
			return null;
		}

		long readWaited = 0L;
		long sleepUnit = 2L;

		while (response == null && readWaited < CONN_TIMEOUT) {
			try {
				sleep(sleepUnit);
			} catch (InterruptedException e) {
				currentThread().interrupt();
			}
			response = getAttachment(message.getMessageID());
			log.trace("try getAttachement: {} {}", readWaited, response);

			readWaited += sleepUnit;
		}

		if(response == null) {
			setAttachment(message.getMessageID());
		}

		if(readWaited >= CONN_TIMEOUT) {
			log.error("[NettyClient][Sync] Read from server failed after " + CONN_TIMEOUT + "ms");
			return null;
		}

		return response;
	}

	private <T extends Future<?>> void waitFutureDone(final T future) {
		final CountDownLatch latch = new CountDownLatch(1);
		future.addListener(f -> latch.countDown());
		//Uninterruptibles.awaitUninterruptibly(latch, 1000, TimeUnit.MILLISECONDS)

		try {
			boolean result = latch.await(2000, TimeUnit.MILLISECONDS);
			if(!result) {
				log.error("waitFutureDone awit Failure!");
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private void setAttachment(int messageId) {
		if (messageId == CHANNEL_CONNECTION_REQUEST) {
			this.channel.attr(AttributeKey.valueOf(NettyTcpClient.ATTACHED_CONN_ID)).set(null);
		}
		else if(messageId == PROCESS_STATE_REQUEST) {
			this.channel.attr(AttributeKey.valueOf(NettyTcpClient.ATTACHED_DATA_ID)).set(null);
		}
	}

	private Object getAttachment(int messageId) {
		Object msg;
		if (messageId == CHANNEL_CONNECTION_REQUEST) {
			msg = this.channel.attr(AttributeKey.valueOf(NettyTcpClient.ATTACHED_CONN_ID)).get();
			this.channel.attr(AttributeKey.valueOf(NettyTcpClient.ATTACHED_CONN_ID)).set(null);
		}
		else {
			msg = this.channel.attr(AttributeKey.valueOf(NettyTcpClient.ATTACHED_DATA_ID)).get();
			this.channel.attr(AttributeKey.valueOf(NettyTcpClient.ATTACHED_DATA_ID)).set(null);
		}

		return msg;
	}

	private String getNextChannelID() {
		String hostname;
		try {
			InetAddress addr = InetAddress.getLocalHost();
			hostname = addr.getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			hostname = defaultChannelHost;
		}

		hostname = hostname.replace("DESKTOP-", "");
		hostname = hostname + hostname;

		String channelHostNm = (hostname + "00000000").substring(0, 6);
		String channelPortNm = (channelPort + "0000").substring(0, 4);

		channelHostNm = "M" +  channelHostNm.substring(1);

		return channelHostNm + channelPortNm + String.format("%04d", commChannelNum.updateAndGet(x ->(x+1 < 10000) ? x+1 : 0));
	}

	@Slf4j
	@NoArgsConstructor
	private static class MessageEncoder extends MessageToByteEncoder<PushMessageInfoDto> {
		@Override
		protected void encode(ChannelHandlerContext ctx, PushMessageInfoDto message, ByteBuf out) throws Exception {

			/*
			 * Message Header Structure (64Byte)
			 * ------------------------------------------------------------------------------
			 *   Message ID(4)  |  Transaction ID(12)  |  Channel ID(14)    | Reserved 1(2)
			 * ------------------------------------------------------------------------------
			 *             Destination IP(16)          |  Reserved 2(12)  |  Data Length(4)
			 * ------------------------------------------------------------------------------
			 */

			byte[] dataInfo = message.getData().getBytes(PUSH_ENCODING);

			byte[] byteTotalData = new byte[PUSH_MSG_HEADER_LEN + dataInfo.length];
			System.arraycopy(Ints.toByteArray(message.getMessageID()), 0, byteTotalData, 0, 4);                    //Message Id
			System.arraycopy(message.getTransactionID().getBytes(PUSH_ENCODING), 0, byteTotalData, 4, message.getTransactionID().getBytes(PUSH_ENCODING).length);//12);   //Transaction Id
			System.arraycopy(message.getChannelID().getBytes(PUSH_ENCODING), 0, byteTotalData, 16, message.getChannelID().getBytes(PUSH_ENCODING).length);//14);             //Channel Id
			System.arraycopy(message.getDestIp().getBytes(PUSH_ENCODING), 0, byteTotalData, 32, message.getDestIp().getBytes(PUSH_ENCODING).length);//Destination IP
			System.arraycopy(Ints.toByteArray(dataInfo.length), 0, byteTotalData, 60, 4);                 //Data Length
			System.arraycopy(dataInfo, 0, byteTotalData, 64, dataInfo.length);

			//log.debug("sendHeader Len =" + byteTotalData.length)

			out.writeBytes(byteTotalData);
		}
	}

	@Slf4j
	@NoArgsConstructor
	private static class MessageDecoder extends ByteToMessageDecoder {

		private final ObjectMapper objectMapper = new ObjectMapper();

		@Override
		protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

			/*
			 * Message Header Structure (64Byte)
			 * ------------------------------------------------------------------------------
			 *   Message ID(4)  |  Transaction ID(12)  |         Destination IP(16)
			 * ------------------------------------------------------------------------------
			 *      Channel ID(14)     | Reserved 1(2) |  Reserved 2(12)  |  Data Length(4)
			 * ------------------------------------------------------------------------------
			 */

			Channel channel = ctx.channel();

			if (!channel.isActive()) {
				log.debug(":: MessageDecoder : isActive Error");
				return;
			}

			in.markReaderIndex();
			if (in.readableBytes() < PUSH_MSG_HEADER_LEN) {
				log.trace(":: MessageDecoder : less than PUSH_MSG_HEADER_LEN");
				return;
			}

			// Message header
			byte[] byteHeader = new byte[PUSH_MSG_HEADER_LEN];
			in.readBytes(byteHeader);

			int messageID = byteToInt(byteHeader, 0);
			int dataLength = byteToInt(byteHeader, PUSH_MSG_HEADER_LEN - 4);

			if (!isValidMessageType(messageID)) {
				in.resetReaderIndex();
				return;
			}

			if (in.readableBytes() < dataLength) {
				in.resetReaderIndex();
				return;
			}

			// Message body
			byte[] byteData = new byte[dataLength];
			in.readBytes(byteData);

			String transactionID = null;
			String result;
			String data = null;
			String statusCode = null;
			String channelId = new String(byteHeader, 32, 14, PUSH_ENCODING);

			if (messageID == PROCESS_STATE_REQUEST_ACK) {
				result = byteToShort(byteData) == 1 ? SUCCESS : FAIL;
				//log.debug(":: MessageDecoder : PROCESS_STATE_REQUEST_ACK {} {}", messageID, result)
			} else {
				result = new String(byteData,0, 2, PUSH_ENCODING);

				if (messageID == COMMAND_REQUEST_ACK) {
					//log.debug(":: MessageDecoder : COMMAND_REQUEST_ACK {}", messageID)
					transactionID = new String(byteHeader, 4, 12, PUSH_ENCODING);
					data = new String(byteData,2, byteData.length - 2, PUSH_ENCODING);

					PushRcvStatusMsgWrapperVo msgWrapperVo = objectMapper.readValue(data, PushRcvStatusMsgWrapperVo.class);
					statusCode = msgWrapperVo.getResponse().getStatusCode();
				}
			}

			PushMessageInfoDto msg = PushMessageInfoDto.builder()
					.messageID(messageID)
					.transactionID(transactionID)
					.channelID(channelId)
					.result(result)
					.data(data)
					.statusCode(statusCode)
					.build();

			//log.debug(":: MessageDecoder : decode end~ : {}", msg)
			out.add(msg);
		}

		private short byteToShort(byte[] src) {
			return (short) ((src[0] & 0xff) << 8 | src[1] & 0xff);
		}

		private int byteToInt(byte[] src, int offset) {
			return (src[offset] & 0xff) << 24 | (src[offset + 1] & 0xff) << 16 | (src[offset + 2] & 0xff) << 8 | src[offset + 3] & 0xff;
		}

		private boolean isValidMessageType(int type) {
			switch (type) {
				case 1:		//CHANNEL_CONNECTION_REQUEST
				case 2:		//CHANNEL_CONNECTION_REQUEST_ACK
				case 5:		//CHANNEL_RELEASE_REQUEST
				case 6:		//CHANNEL_RELEASE_REQUEST_ACK
				case 13:	//PROCESS_STATE_REQUEST
				case 14:	//PROCESS_STATE_REQUEST_ACK
				case 15:	//COMMAND_REQUEST
				case 16:	//COMMAND_REQUEST_ACK
					return true;
				default:
					return false;
			}
		}

	}

	@Slf4j
	@NoArgsConstructor
	private static class MessageHandler extends ChannelInboundHandlerAdapter {

		private PushMultiClient pushMultiClient = null;

		public MessageHandler(PushMultiClient pushMultiClient) {
			this.pushMultiClient = pushMultiClient;
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) {

			PushMessageInfoDto message;
			if (msg instanceof PushMessageInfoDto) {
				message = (PushMessageInfoDto) msg;
			} else {
				log.error("[MessageHandler] message is not valid");
				return;
			}

			if (message.getMessageID() == PROCESS_STATE_REQUEST_ACK) {
				// 메시지 전송을 Sync 방식으로 작동하게 하기 위함.
				log.trace(":: MessageHandler channelRead : PROCESS_STATE_REQUEST_ACK");
				ctx.channel().attr(AttributeKey.valueOf(NettyTcpClient.ATTACHED_DATA_ID)).set(message);
			}
			else if (message.getMessageID() == CHANNEL_CONNECTION_REQUEST_ACK) {
				// 메시지 전송을 Sync 방식으로 작동하게 하기 위함.
				log.trace(":: MessageHandler channelRead : CHANNEL_CONNECTION_REQUEST_ACK");
				ctx.channel().attr(AttributeKey.valueOf(NettyTcpClient.ATTACHED_CONN_ID)).set(message);
			}
			else if (message.getMessageID() == COMMAND_REQUEST_ACK) {
				// Push 전송인 경우 response 결과를 임시 Map에 저장함.
				log.trace(":: MessageHandler channelRead : COMMAND_REQUEST_ACK {}", message);
				pushMultiClient.receiveAsyncMessage(PushMultiClient.MsgType.RECIVED_MSG, message);
			}

			log.trace("[MessageHandler] id : " + ctx.channel().id() + ", messageReceived : " + message.getMessageID() + ", " +
					message.getTransactionID() + ", " + message.getResult());
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {

			log.error("[MessageHandler] id : " + ctx.channel().id() + ", exceptionCaught : " + e.toString());

			try {
				if (ctx.channel().isActive()) { // isConnected -> isActive
					ctx.channel().disconnect();
					ctx.channel().close();
				}
			} catch (Exception ex) {
				log.error("[MessageHandler] connection closing : {}", ex.toString());
			}
		}

		@Override
		public void channelRegistered(ChannelHandlerContext ctx) {
			log.debug("channelRegistered!");
			ctx.fireChannelRegistered();
		}

		@Override
		public void channelUnregistered(ChannelHandlerContext ctx) {
			log.debug("channelUnregistered!");
			ctx.fireChannelUnregistered();
		}

		@Override
		public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
			log.debug("userEventTriggered!");
			ctx.fireUserEventTriggered(evt);
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	@ToString
	static class PushRcvStatusMsgVo {
		@JsonProperty("msg_id")
		private String msgId;

		@JsonProperty("push_id")
		private String pushId;

		@JsonProperty("status_code")
		private String statusCode;

		@JsonProperty("statusmsg")
		private String statusMsg;
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	@ToString
	static class PushRcvStatusMsgWrapperVo {
		@JsonProperty("response")
		private PushRcvStatusMsgVo response;
	}

}
