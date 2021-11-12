package com.lguplus.fleta.provider.rest.multipush;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
/*
import com.dmi.pushagent.PushUtil.PushMakeBody;
import com.dmi.pushagent.PushUtil.hdtv.PushSocketListComm;
import com.dmi.pushagent.common.exception.CustomException;
import com.dmi.pushagent.common.property.MlCommProperties;
import com.dmi.pushagent.common.util.ByteUtil;
import com.dmi.pushagent.common.util.GlobalCom;
import com.dmi.pushagent.common.util.Json;
import com.dmi.pushagent.common.vo.PushBaseVo;
import com.dmi.pushagent.message.netty.MsgEntityCommon;
import com.dmi.pushagent.message.netty.NettyClient;
import com.dmi.pushagent.message.vo.MessageInfo;
*/

@Slf4j
public class MessageService {
	
	private static MessageService g_Instance = null;

	public static final int TRANSACTION_MAX_SEQ_NO = 999999999;	// should be less than 32Byte int in size
	public static AtomicInteger TRANSACTION_SEQ_NO = new AtomicInteger(0);
	
	private ConcurrentHashMap<String, MessageInfo> messageInfoMap = new ConcurrentHashMap<String, MessageInfo>();

	private NettyClient nettyClient = null;
	private String channelID = null;
	private String host = "127.0.0.1";//MlCommProperties.getProperty("push.server.ip");
	private int port = 8888;//Integer.parseInt(MlCommProperties.getProperty("push.server.port"));

	private final ObjectMapper objectMapper = new ObjectMapper();
	
	public MessageService() {
		super();
	}
	
	public synchronized static MessageService getInstance() {
		if (g_Instance == null) {
			g_Instance = new MessageService();
		}
		return g_Instance;
	}
	
	public boolean connect() throws Exception {
		try {
			if (nettyClient == null) {
				nettyClient = new NettyClient();
				nettyClient.initailize(host, port);
			} else {
				nettyClient.connect();
			}			
		} catch (Exception e) {
			log.error("[MessageService] client got a exception : {} ", e);
			throw e;
		}
		
		return true;
	}
	
	public String getChannelID() {
		return channelID;
	}

	public boolean isConnected() {
		if (nettyClient == null || !nettyClient.isValid()) {
			return false;
		}
		return true;
	}
	
	public void disconnect() {
		nettyClient.disconnect();
	}
	
	public boolean isChannelValid() throws Exception {
		if (this.channelID == null) {
			return false;
		}
		return this.processStateRequest();
	}
	
	/**
	 * Channel 접속 요청 메시지 전송 
	 */
	public boolean channelConnectionRequest() throws Exception {
		String channelID = this.getNextChannelID();
		
		MessageInfo message = new MessageInfo();
		message.setMessageID(MsgEntityCommon.CHANNEL_CONNECTION_REQUEST);
		message.setChannelID(channelID);

		if (nettyClient.write(message)) {
			this.channelID = channelID;
			log.info("[MessageService] ChannelConnectionRequest Success. Channel ID : " + channelID);
			return true;
		}
		
		log.error("[MessageService] ChannelConnectionRequest Fail.");
		return false;
	}
	
	/**
	 * Channel 접속 해지 메시지 전송 
	 */
	public boolean channelReleaseRequest() throws Exception {
		if (this.channelID == null) {
			return false;
		}
		
		MessageInfo message = new MessageInfo();
		message.setMessageID(MsgEntityCommon.CHANNEL_RELEASE_REQUEST);
		message.setChannelID(this.channelID);
		
		return nettyClient.write(message);
	}
	
	/** 
	 * 프로세스 상태 확인 메시지 전송
	 */
	public boolean processStateRequest() throws Exception {
		if (this.channelID == null) {
			return false;
		}

		MessageInfo message = new MessageInfo();
		message.setMessageID(MsgEntityCommon.PROCESS_STATE_REQUEST);		
		message.setChannelID(this.channelID);
				
		MessageInfo response = (MessageInfo) nettyClient.writeSync(message);
		
		if (response != null && MsgEntityCommon.SUCCESS.equals(response.getResult())) {
			log.info("[MessageService] ProcessStateRequest Success. Channel ID : " + channelID);
			return true;
		} 
		
		log.info("[MessageService] ProcessStateRequest Fail. Channel ID : " + channelID);
		return false;
	}
	
	/** 
	 * 명령어 처리 요청 메시지 전송
	 */
	public String commandRequest(String app_id, String service_id, String service_key, 
			String noti_message, List<String> arr_item) throws Exception {
		String transactionDate = DateFormatUtils.format(new Date(), "yyyyMMdd");//Today
		int transactionNum = TRANSACTION_SEQ_NO.incrementAndGet();		
		String transactionID =  transactionDate + Integer.toString(transactionNum);

		byte[] bTransactionDate = transactionDate.getBytes();
		byte[] bTransactionNum = ByteUtil.int2byte(transactionNum);
		byte[] bTransactionID = new byte[12];
		ByteUtil.setbytes(bTransactionID, 0, bTransactionDate);
		ByteUtil.setbytes(bTransactionID, 8, bTransactionNum);
		
		// Known Issue : Push_ID는 transaction ID와 동일한 값으로 사용해야 하나  12자리를 초과하는 경우 오류가 발생하여 12자리로 trunk함. 
		String messageData = this.makeMessageData((transactionID.length() > 12 ? transactionID.substring(0, 12) : transactionID), 
				app_id, service_id, service_key, noti_message, arr_item);

		MessageInfo message = new MessageInfo();
		message.setMessageID(MsgEntityCommon.COMMAND_REQUEST);
		message.setBTransactionID(bTransactionID);
		message.setChannelID(this.channelID);
		message.setData(messageData);
		
		if (!nettyClient.write(message)) {
			return null;
		}

		return transactionID;
	}
	
	//@SuppressWarnings("unchecked")
	private String makeMessageData(String push_id, String app_id, String service_id, String service_key, 
			String noti_message, List<String> arr_item) throws Exception {

		HashMap<String, String> pushBody = null;
		
		//try {
			PushBaseVo psVO = new PushBaseVo();
			psVO.setApp_id(app_id);
			psVO.setService_id(service_id);
			psVO.setService_key(service_key);
			psVO.setNoti_message(noti_message);	
			psVO.setArrItem(arr_item);	
			
			pushBody = PushMakeBody.makeBodyByNoti(push_id, psVO);
			/*
		} catch(CustomException e) {
			if(e.getFlag().equals(MlCommProperties.getProperty("flag.pushgw.servicenotfound"))) {
				log.error("[MessageService] Service ID or Password is Null");
			} else {
				log.error("[MessageService] Service ID or Password Error - ["+e.getException().getClass().getName()
						+"]["+e.getException().getMessage()+"]");
			}
			throw e;
		} catch(Exception e) {
			log.error("[MessageService][makeMessageData]["+e.getClass().getName()+"]["+e.getMessage()+"]");
			throw e;
		}
		*/
		
		//Push Request Body 값 설정
		/*
		Json json = new Json("request");
		json.setKeys(pushBody);		  
		json.addObject(pushBody);

		// Map -> JsonNode
		JsonNode jsonNode1 = objectMapper.valueToTree(map);
		JsonNode jsonNode2 = objectMapper.convertValue(map, JsonNode.class);
		// jsonNode 출력
		System.out.println(jsonNode1); // {"name":"Anna","id":1}
		System.out.println(jsonNode2); // {"name":"Anna","id":1}
		*/

		ObjectNode oNode = objectMapper.createObjectNode();
		oNode.set("request", objectMapper.valueToTree(pushBody));
		return oNode.toString();
	}
	
	public synchronized String getNextChannelID() {
		/*
		PushSocketListComm.channelNum = (PushSocketListComm.channelNum + 1) % 10000; //9999 이하로 cycle
		return PushSocketListComm.channelID + String.format("%04d", PushSocketListComm.channelNum);
		 */
		return "ChannelId:" + "0000";
	}

	public void putMessageInfo(String key, MessageInfo message) {
		messageInfoMap.putIfAbsent(key, message);
	}

	public MessageInfo removeMessageInfo(String key) {
		return messageInfoMap.remove(key);
	}

	public int getMessageInfoSize() {
		return messageInfoMap.size();
	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class PushBaseVo {
		private String app_id;
		private String service_id;
		private String service_key;
		private String noti_message;
		private List<String> arrItem;
	}
}
