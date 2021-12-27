package com.lguplus.fleta.provider.socket.multi;

import com.fasterxml.jackson.databind.JsonNode;
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
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.List;

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

	public static final int PUSH_MSG_HEADER_LEN = 64;
	public static final String SUCCESS = "SC";
	public static final String FAIL = "FA";
	public static final int PROCESS_STATE_REQUEST_ACK = 14;
	public static final int COMMAND_REQUEST_ACK = 16;
	public static final String PUSH_ENCODING = "euc-kr";

	EventLoopGroup workerGroup;
	Bootstrap bootstrap = null;
	Channel channel = null;
	private String host;
	private int port;

	public void initailize(PushMultiClient socketClient, String host, int port) {
		this.host = host;
		this.port = port;

		retryCount = Integer.parseInt(pushCallRetryCnt);

		this.workerGroup =  new NioEventLoopGroup();//test 1

		log.debug("[NettyClient] Server IP : " + host + ", port : " + port);
		bootstrap = new Bootstrap()
				.group(this.workerGroup)
				.channel(NioSocketChannel.class)
				.option(ChannelOption.TCP_NODELAY, true)
				.option(ChannelOption.SO_KEEPALIVE, true)
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Integer.parseInt(timeout))
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) {
						ChannelPipeline p = ch.pipeline();
						p.addLast("decoder", new MessageDecoder());
						p.addLast("encoder", new MessageEncoder());
						p.addLast("handler", new MessageHandler(socketClient));
					}
				});

		this.connect();
	}

	public void connect() {

		ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));
		channel = future.awaitUninterruptibly().channel();

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
				//ChannelFuture writeFuture = this.channel.write(message)
				ChannelFuture writeFuture = this.channel.writeAndFlush(message);
				writeFuture.awaitUninterruptibly(CONN_TIMEOUT);

				if (!writeFuture.isSuccess()) {
					log.error("[NettyClient] write to server failed");
					return false;
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			log.error("[NettyClient] got a exception : {}", e);
			return false;
		}

		return true;
	}

	public Object writeSync(PushMessageInfoDto message) {
		Object response = null;

		if (null == message) {
			return null;
		}

		//ChannelFuture writeFuture = this.channel.write(message)
		ChannelFuture writeFuture = this.channel.writeAndFlush(message);

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

				//writeFuture = channel.write(message)
				writeFuture = channel.writeAndFlush(message);
				writeFuture.awaitUninterruptibly(CONN_TIMEOUT);
				writeTryTimes++;
			}
		}

		if (writeTryTimes >= retryCount) {
			log.error("[NettyClient][Sync] write to server failed afer retry " + retryCount + "times");
			return null;
		}

		long readWaited = 0L;
		long sleepUnit = 2L;

		while (response == null && readWaited < CONN_TIMEOUT) {
			try {
				Thread.sleep(sleepUnit);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			response = getAttachment();
			readWaited += sleepUnit;
		}

		// Remove the current attachment
		if(response == null) {
			this.channel.attr(AttributeKey.valueOf(NettyClient.ATTACHED_DATA_ID)).set(null);
		}

		if(readWaited >= CONN_TIMEOUT) {
			log.error("[NettyClient][Sync] Read from server failed after " + CONN_TIMEOUT + "ms");
			return null;
		}

		return response;
	}

	private Object getAttachment() {

		AttributeKey<Object> attrKey = AttributeKey.valueOf(NettyClient.ATTACHED_DATA_ID);
		Object msg = this.channel.attr(attrKey).get();

		if(msg != null) {
			this.channel.attr(attrKey).set(null);
			log.trace(":: getAttachment:: {}", ((PushMessageInfoDto)msg));
		}
		return msg;
	}

	@Slf4j
	@NoArgsConstructor
	static class MessageEncoder extends MessageToByteEncoder<PushMessageInfoDto> {
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

			if(message.getData().length() > 0) {
				//log.debug("send Json : {}", message.getData())
			}

			out.writeBytes(byteTotalData);
		}
	}

	@Slf4j
	@NoArgsConstructor
	static class MessageDecoder extends ByteToMessageDecoder {

		private final ObjectMapper objectMapper = new ObjectMapper();

		private static final String RESPONSE_ID_NM = "response";
		private static final String RESPONSE_STATUS_CD = "status_code";

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

					JsonNode jsonNodeR = objectMapper.readTree(data);

					if(jsonNodeR != null && jsonNodeR.has(RESPONSE_ID_NM) && jsonNodeR.get(RESPONSE_ID_NM).has(RESPONSE_STATUS_CD)) {
						statusCode = jsonNodeR.get(RESPONSE_ID_NM).get(RESPONSE_STATUS_CD).asText();
					}

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
	static class MessageHandler extends ChannelInboundHandlerAdapter {

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
				setAttachment(ctx.channel(), message);
			}
			else if (message.getMessageID() == COMMAND_REQUEST_ACK) {
				// Push 전송인 경우 response 결과를 임시 Map에 저장함.
				log.trace(":: MessageHandler channelRead : COMMAND_REQUEST_ACK");
				pushMultiClient.receiveAsyncMessage(message);
			}

			log.trace("[MessageHandler] id : " + ctx.channel().id() + ", messageReceived : " + message.getMessageID() + ", " +
					message.getTransactionID() + ", " + message.getResult());
		}

		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			log.trace(":: MessageHandler channelActive");
		}

		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			log.trace(":: MessageHandler channelInactive");
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

		private void setAttachment(Channel channel, Object value) {
			log.trace(":: MessageHandler setAttachment:: {} / {}", channel.id(), ((PushMessageInfoDto)value));
			AttributeKey<Object> attrKey = AttributeKey.valueOf(NettyClient.ATTACHED_DATA_ID);
			channel.attr(attrKey).set(value);
		}
	}

}
