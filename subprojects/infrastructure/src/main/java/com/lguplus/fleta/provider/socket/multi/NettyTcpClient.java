package com.lguplus.fleta.provider.socket.multi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.primitives.Ints;
import com.lguplus.fleta.client.PushMultiClient;
import com.lguplus.fleta.data.dto.response.inner.PushMessageInfoDto;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.AttributeKey;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;

@Slf4j
@Component
@RequiredArgsConstructor
public class NettyTcpClient {

	@Value("${push.gateway.default.ip}")
	private String host;

	@Value("${push.gateway.default.port}")
	private int port;

	@Value("${push.gateway.default.socket.timeout}")
	private int timeout;

	@Value("${push.gateway.default.destination}")
	private String destinationIp;

	@Value("${server.port}")
	private int wasPort;

	@Value("${push.gateway.default.socket.channelId}")
	private String defaultSocketChannelId;

	@Value("${push.gateway.default.retry}")
	private int callRetryCount;

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
	private SocketChannel socketChannel = null;
	private PushMultiClient pushMultiClient;

	private final AtomicInteger commChannelNum = new AtomicInteger(0);

	private void initialize() {

		int threadCount = Runtime.getRuntime().availableProcessors() * 2;

		log.debug("[NettyClient] Server IP : " + host + ", port : " + port);

		bootstrap = new Bootstrap()
				.group(new NioEventLoopGroup(threadCount))
				.channel(NioSocketChannel.class)
				.remoteAddress(host, port)
				.option(ChannelOption.TCP_NODELAY, true)
				.option(ChannelOption.SO_KEEPALIVE, true)
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout)
				.handler(new ChannelInitializer<SocketChannel>() {
					public void initChannel(SocketChannel ch) {
						ChannelPipeline p = ch.pipeline();
						p.addLast("decoder", new MessageDecoder());
						p.addLast("encoder", new MessageEncoder());
						p.addLast("handler", new MessageHandler(pushMultiClient));
					}
				});
	}

	public String connect(PushMultiClient pushMultiClient) {

		initBootStrap(pushMultiClient);

		//Connect
		ChannelFuture future = getConnectionFuture(); // ChannelOption.CONNECT_TIMEOUT_MILLIS 만큼 대기

		if (future.isDone() && future.isSuccess()) {
			socketChannel = (SocketChannel) future.channel();
		}
		else {
			log.debug("[NettyClient] The new socketChannel has been not connected. ");
			socketChannel = null;
			return null;
		}

		log.debug("[NettyClient] The new socketChannel has been connected. [" + socketChannel.id() + "]");

		String genChannelID = this.getNextChannelID();
		Optional<PushMessageInfoDto> response = sendConnectRequest(genChannelID);

		if(SUCCESS.equals(response.orElse(PushMessageInfoDto.builder().result("FA").build()).getResult())) {
			log.debug("[PushMultiClient] channelConnectionRequest Success. Channel ID : " + genChannelID);
			return genChannelID;
		}

		log.error("[PushMultiClient] ChannelConnectionRequest Fail.");
		return null;

	}

	public void initBootStrap(PushMultiClient pushMultiClient) {
		if(bootstrap == null) {
			this.pushMultiClient = pushMultiClient;
			initialize();
		}
	}

	public ChannelFuture getConnectionFuture() {
		return bootstrap.connect().awaitUninterruptibly();
	}

	public Optional<PushMessageInfoDto> sendConnectRequest(String channelId) {
		return writeSync(PushMessageInfoDto.builder()
				.messageId(CHANNEL_CONNECTION_REQUEST)
				.channelId(channelId).destinationIp(destinationIp)
				.build());
	}

	public void disconnect() {
		if (getSocketChannel() != null && getSocketChannel().isActive()) { //isConnected -> isActive
			getSocketChannel().disconnect();
			getSocketChannel().close();
			socketChannel = null;
			log.debug("[NettyClient] The current socketChannel has been disconnected.");
		}
	}

	public boolean isInValid() {
		if(getSocketChannel() == null)
			return true;
		//isActive : isOpen() && isConnected()
		return !getSocketChannel().isActive();
	}

	public Channel getSocketChannel() {
		return socketChannel;
	}

	public void write(final PushMessageInfoDto pushMessageInfoDto) {
		if (isInValid()) {
			log.error("[NettyClient] write0 isNotActive");
			return;
		}
		getSocketChannel().write(pushMessageInfoDto);
	}

	public void flush() {
		//log.debug("[NettyClient] flush socketChannel")
		getSocketChannel().flush();
	}

	public Optional<PushMessageInfoDto> writeSync(PushMessageInfoDto message) {
		PushMessageInfoDto response = null;

		int retryCount = callRetryCount;

		//Clear
		setAttachment(message.getMessageId());

		AtomicBoolean isFutureSuccess = new AtomicBoolean(false);

		//Send
		IntStream.range(0, retryCount).takeWhile(value -> !isFutureSuccess.get()).forEach(sendCnt -> {
			ChannelFuture awaitFuture = getSocketChannel().writeAndFlush(message);
			waitFutureDone(awaitFuture);
			isFutureSuccess.set(awaitFuture.isSuccess());
			log.debug("[NettyClient][Sync] writeAndFlush {}/{} : {}/{}", sendCnt, retryCount, awaitFuture.isSuccess(), isFutureSuccess.get());
		});

		if (!isFutureSuccess.get()) {
			log.error("[NettyClient][Sync] write to server failed ");
			return Optional.ofNullable(null);
		}

		long readWaited = 0L;
		long sleepUnit = 2L;

		while (response == null && readWaited < CONN_TIMEOUT) {
			try {
				sleep(sleepUnit);
			} catch (InterruptedException e) {
				currentThread().interrupt();
			}
			response = getAttachment(message.getMessageId());
			log.trace("try getAttachement: {} {}", readWaited, response);

			readWaited += sleepUnit;
		}

		if(response == null) {
			setAttachment(message.getMessageId());
		}

		if(readWaited >= CONN_TIMEOUT) {
			log.error("[NettyClient][Sync] Read from server failed after " + CONN_TIMEOUT + "ms");
			return Optional.ofNullable(null);
		}

		return Optional.ofNullable(response);
	}

	public void waitFutureDone(final ChannelFuture future)  {
		final CountDownLatch latch = new CountDownLatch(1);
		future.addListener(f -> latch.countDown());

		try {
			log.debug("waitLatch start 0");
			boolean result = waitLatch(latch);
			if(!result) {
				log.error("waitFutureDone awit timeout!");
			}
			log.debug("waitLatch end 0");
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	public boolean waitLatch(CountDownLatch latch) throws InterruptedException {
		return latch.await(2000, TimeUnit.MILLISECONDS);
	}

	public void setAttachment(int messageId) {
		if (messageId == CHANNEL_CONNECTION_REQUEST) {
			getSocketChannel().attr(AttributeKey.valueOf(NettyTcpClient.ATTACHED_CONN_ID)).set(null);
		}
		else if(messageId == PROCESS_STATE_REQUEST) {
			getSocketChannel().attr(AttributeKey.valueOf(NettyTcpClient.ATTACHED_DATA_ID)).set(null);
		}
		else {
			log.error("unknown messagId {}", messageId);
		}
	}

	private PushMessageInfoDto getAttachment(int messageId) {
		PushMessageInfoDto msg;
		if (messageId == CHANNEL_CONNECTION_REQUEST) {
			msg = (PushMessageInfoDto) getSocketChannel().attr(AttributeKey.valueOf(NettyTcpClient.ATTACHED_CONN_ID)).get();
			getSocketChannel().attr(AttributeKey.valueOf(NettyTcpClient.ATTACHED_CONN_ID)).set(null);
		}
		else {
			msg = (PushMessageInfoDto) getSocketChannel().attr(AttributeKey.valueOf(NettyTcpClient.ATTACHED_DATA_ID)).get();
			getSocketChannel().attr(AttributeKey.valueOf(NettyTcpClient.ATTACHED_DATA_ID)).set(null);
		}

		return msg;
	}

	public String getNextChannelID() {
		String hostName;
		try {
			hostName = getHostName();
		} catch (IOException e) {
			hostName = defaultSocketChannelId;
		}

		hostName = hostName.replace("DESKTOP-", "");
		hostName = hostName + hostName;

		String channelHostNm = (hostName + "00000000").substring(0, 6);
		String channelPortNm = (wasPort + "0000").substring(0, 4);

		channelHostNm = "M" +  channelHostNm.substring(1);

		return channelHostNm + channelPortNm + String.format("%04d", commChannelNum.updateAndGet(x ->(x+1 < 10000) ? x+1 : 0));
	}

	public String getHostName() throws IOException {
		InetAddress addr = InetAddress.getLocalHost();
		return addr.getHostName();
	}

	@Slf4j
	@NoArgsConstructor
	private static class MessageEncoder extends MessageToByteEncoder<PushMessageInfoDto> {
		@Override
		protected void encode(ChannelHandlerContext ctx, PushMessageInfoDto message, ByteBuf out) throws Exception {

			/* client -> server header
			 * Message Header Structure (64Byte)
			 * ------------------------------------------------------------------------------
			 *   Message ID(4)  |  Transaction ID(12)  |  Channel ID(14)    | Reserved 1(2)
			 * ------------------------------------------------------------------------------
			 *             Destination IP(16)          |  Reserved 2(12)  |  Data Length(4)
			 * ------------------------------------------------------------------------------
			 */

			byte[] dataInfo = message.getData().getBytes(PUSH_ENCODING);

			byte[] byteTotalData = new byte[PUSH_MSG_HEADER_LEN + dataInfo.length];
			System.arraycopy(Ints.toByteArray(message.getMessageId()), 0, byteTotalData, 0, 4);                    //Message Id
			System.arraycopy(message.getTransactionId().getBytes(PUSH_ENCODING), 0, byteTotalData, 4, message.getTransactionId().getBytes(PUSH_ENCODING).length);	//Transaction Id
			System.arraycopy(message.getChannelId().getBytes(PUSH_ENCODING), 0, byteTotalData, 16, message.getChannelId().getBytes(PUSH_ENCODING).length);			// Channel Id
			System.arraycopy(message.getDestinationIp().getBytes(PUSH_ENCODING), 0, byteTotalData, 32, message.getDestinationIp().getBytes(PUSH_ENCODING).length);	//Destination IP
			System.arraycopy(Ints.toByteArray(dataInfo.length), 0, byteTotalData, 60, 4);                 //Data Length
			System.arraycopy(dataInfo, 0, byteTotalData, 64, dataInfo.length);

			log.debug("MessageEncoder {}", message);

			out.writeBytes(byteTotalData);
		}
	}

	@Slf4j
	@NoArgsConstructor
	public static class MessageDecoder extends ByteToMessageDecoder {

		private final ObjectMapper objectMapper = new ObjectMapper();

		@Override
		protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

			/* server -> client header
			 * Message Header Structure (64Byte)
			 * ------------------------------------------------------------------------------
			 *   Message ID(4)  |  Transaction ID(12)  |         Destination IP(16)
			 * ------------------------------------------------------------------------------
			 *      Channel ID(14)     | Reserved 1(2) |  Reserved 2(12)  |  Data Length(4)
			 * ------------------------------------------------------------------------------
			 */

			Channel channel = ctx.channel();

			log.debug("MessageDecoder in.readableBytes:{}", in.readableBytes());

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

			if (in.readableBytes() < dataLength) {
				in.resetReaderIndex();
				return;
			}

			// Message body
			byte[] byteData = new byte[dataLength];
			in.readBytes(byteData);

			String result;
			String channelId = new String(byteHeader, 32, 14, PUSH_ENCODING);

			switch (messageID) {
				case CHANNEL_CONNECTION_REQUEST_ACK:
					log.trace("** decode message: CHANNEL_CONNECTION_REQUEST_ACK {}", CHANNEL_CONNECTION_REQUEST_ACK);

					result = new String(byteData,0, 2, PUSH_ENCODING);
					out.add(PushMessageInfoDto.builder()
							.messageId(messageID)
							.channelId(channelId)
							.result(result)
							.build());
					break;

				case PROCESS_STATE_REQUEST_ACK:
					log.trace("** decode message: PROCESS_STATE_REQUEST_ACK {}", PROCESS_STATE_REQUEST_ACK);

					result = byteToShort(byteData) == 1 ? SUCCESS : FAIL;
					out.add(PushMessageInfoDto.builder()
							.messageId(messageID)
							.channelId(channelId)
							.result(result)
							.build());
					break;

				case COMMAND_REQUEST_ACK:
					log.trace("** decode message: COMMAND_REQUEST_ACK {}", COMMAND_REQUEST_ACK);

					result = new String(byteData,0, 2, PUSH_ENCODING);
					String transactionID = new String(byteHeader, 4, 12, PUSH_ENCODING);
					String data = new String(byteData,2, byteData.length - 2, PUSH_ENCODING);

					PushRcvStatusMsgWrapperVo msgWrapperVo = objectMapper.readValue(data, PushRcvStatusMsgWrapperVo.class);
					String statusCode = msgWrapperVo.getResponse().getStatusCode();
					out.add(PushMessageInfoDto.builder()
							.messageId(messageID)
							.transactionId(transactionID)
							.channelId(channelId)
							.result(result)
							.data(data)
							.statusCode(statusCode)
							.build());
					break;
				default:
					log.error("MessageDecoder unknown message {}", messageID);
					break;
			}

			log.debug("** decode end: {}", out.size());
		}

		private short byteToShort(byte[] src) {
			return (short) ((src[0] & 0xff) << 8 | src[1] & 0xff);
		}

		private int byteToInt(byte[] src, int offset) {
			return (src[offset] & 0xff) << 24 | (src[offset + 1] & 0xff) << 16 | (src[offset + 2] & 0xff) << 8 | src[offset + 3] & 0xff;
		}

	}

	@Slf4j
	@NoArgsConstructor
	public static class MessageHandler extends SimpleChannelInboundHandler<PushMessageInfoDto> {

		private PushMultiClient pushMultiClient = null;

		public MessageHandler(PushMultiClient pushMultiClient) {
			this.pushMultiClient = pushMultiClient;
		}

		@Override
		public void channelRead0(ChannelHandlerContext ctx, PushMessageInfoDto message) {

			if (message.getMessageId() == PROCESS_STATE_REQUEST_ACK) {
				// 메시지 전송을 Sync 방식으로 작동하게 하기 위함.
				log.debug(":: MessageHandler channelRead : PROCESS_STATE_REQUEST_ACK");
				ctx.channel().attr(AttributeKey.valueOf(NettyTcpClient.ATTACHED_DATA_ID)).set(message);
			}
			else if (message.getMessageId() == CHANNEL_CONNECTION_REQUEST_ACK) {
				// 메시지 전송을 Sync 방식으로 작동하게 하기 위함.
				log.debug(":: MessageHandler channelRead : CHANNEL_CONNECTION_REQUEST_ACK");
				ctx.channel().attr(AttributeKey.valueOf(NettyTcpClient.ATTACHED_CONN_ID)).set(message);
			}
			else if (message.getMessageId() == COMMAND_REQUEST_ACK) {
				// Push 전송인 경우 response 결과를 임시 Map에 저장함.
				log.debug(":: MessageHandler channelRead : COMMAND_REQUEST_ACK {}", message);
				pushMultiClient.receiveAsyncMessage(PushMultiClient.MsgType.RECIVED_MSG, message);
			}
			else {
				log.error("unknown channelRead ! {}", message.getMessageId());
			}

			//log.trace("[MessageHandler] id : " + ctx.socketChannel().id() + ", messageReceived : " + message.getMessageId() + ", " +
			//		message.getTransactionId() + ", " + message.getResult())
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {

			log.error("[MessageHandler] id : " + ctx.channel().id() + ", exceptionCaught : " + e.toString());

			if (ctx.channel().isActive()) { // isConnected -> isActive
				ctx.channel().disconnect();
				ctx.channel().close();
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
