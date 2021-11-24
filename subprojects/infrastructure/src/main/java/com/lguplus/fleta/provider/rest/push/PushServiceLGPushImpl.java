package com.lguplus.fleta.provider.rest.push;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.HashMap;
import java.util.NoSuchElementException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

@Slf4j
@ToString
@Service("LGPushServiceImpl")
public class PushServiceLGPushImpl implements PushService{

	private final ObjectMapper objectMapper = new ObjectMapper();

	public  PushResultVO setNoti(PushSocketVO psVO)  throws Exception{
		
		log.debug("[lgpush][setNoti] START");
	
		PushResultVO resultVO = new PushResultVO();
		
		try {
			log.info("[lgpush][" + psVO.getChannelID() + "][S][" + PushSocketListComm.pushSocketQueue.size() + "][" + psVO.getApp_id() + "][" + psVO.getService_id() + "][" + psVO.getService_key() + "]");

			psVO.getPushSocket().setSoTimeout(PushSocketListComm.pushSocketSendTimeout);

			//PushGW Request Header 값 설정		
			//4자리수 넘지 않도록 방어코드
			if (PushSocketListComm.transactionIDNum >= 9999) {
				PushSocketListComm.transactionIDNum = 0;
			}

			String tTransactionDate = DateFormatUtils.format(new Date(), "yyyyMMdd");// GlobalCom.getTodayFormat();
			int tTransactionNum = PushSocketListComm.transactionIDNum++;
			String tRealTransaction = "";
			try {
				tRealTransaction = tTransactionDate + Integer.toString(tTransactionNum);
			} catch (Exception e) {
				log.info("[lgpush][setNoti][TransactionID Error][" + e.getClass().getName() + "][" + e.getMessage() + "]");
				throw e;
			}

			HashMap pushBody;
			try {
				pushBody = PushMakeBody.makeBodyByNoti(tRealTransaction, psVO);

			/*catch(CustomException e){
				if(e.getFlag().equals(MlCommProperties.getProperty("flag.pushgw.servicenotfound"))){
					log.info("[lgpush][setNoti][service_id or service_passwd] Null");
				}else{
					log.info("[lgpush][setNoti][service_id or service_passwd Error]["+e.getException().getClass().getName()+"]["+e.getException().getMessage()+"]");
				}
				throw e;*/
			} catch (Exception ex) {
				log.info("[lgpush][setNoti][makeBodyByNoti][" + ex.getClass().getName() + "][" + ex.getMessage() + "]");
				throw ex;
			}

			byte[] byte_MessageID = ByteUtil.int2byte(15);
			byte[] byte_TransactionDate = tTransactionDate.getBytes();
			byte[] byte_TransactionNum = ByteUtil.int2byte(tTransactionNum);
			byte[] byte_ChannelID = psVO.getChannelID().getBytes();
			byte[] byte_Reserved1 = new byte[2];
			byte[] byte_Reserved2 = new byte[12];

			byte[] byte_DestinationIP = new byte[16];
			ByteUtil.setbytes(byte_DestinationIP, 0, PushSocketListComm.destinationIP.getBytes());

			int destinationIPNullCnt = 16 - PushSocketListComm.destinationIP.length();

			log.debug("[lgpush][setNoti] destinationIPNullCnt = " + destinationIPNullCnt);

			for (int i = 0; i < destinationIPNullCnt; i++) {
				ByteUtil.setbytes(byte_DestinationIP, (PushSocketListComm.destinationIP.length() + i), new byte[1]);
			}
			//byte_DestinationIP = PushSocketListComm.destinationIP.getBytes(); //자릿수 비교 하여 공백 바이트값 추가

			/*
			//Push Request Body 값 설정
			Json json = new Json("request");
			json.setKeys(pushBody);		  
			json.addObject(pushBody);
			String jsonStr = json.toString();
			*/

			ObjectNode oNode = objectMapper.createObjectNode();
			oNode.set("request", objectMapper.valueToTree(pushBody));
			String jsonStr = oNode.toString();

			JsonNode jsonNode = objectMapper.valueToTree(pushBody);

			int dataLen = jsonStr.length();
			log.debug("[lgpush][setNoti] dataLen = " + dataLen);

			byte[] byte_DATALength = ByteUtil.int2byte(jsonStr.getBytes().length);
			byte[] byte_DATA = jsonStr.getBytes();

			byte[] byte_TransactionID = new byte[byte_TransactionDate.length + byte_TransactionNum.length];
			ByteUtil.setbytes(byte_TransactionID, 0, byte_TransactionDate);
			ByteUtil.setbytes(byte_TransactionID, byte_TransactionDate.length, byte_TransactionNum);

			log.debug("[lgpush][setNoti] byte_MessageID=" + byte_MessageID);
			log.debug("[lgpush][setNoti] byte_TransactionID=" + byte_TransactionID);
			log.debug("[lgpush][setNoti] byte_TransactionDate=" + byte_TransactionDate);
			log.debug("[lgpush][setNoti] byte_TransactionNum=" + byte_TransactionNum);
			log.debug("[lgpush][setNoti] byte_ChannelID=" + byte_ChannelID);
			log.debug("[lgpush][setNoti] byte_Reserved1=" + byte_Reserved1);
			log.debug("[lgpush][setNoti] byte_DestinationIP=" + byte_DestinationIP);
			log.debug("[lgpush][setNoti] byte_Reserved2=" + byte_Reserved2);
			log.debug("[lgpush][setNoti] byte_DATALength=" + byte_DATALength);
			log.debug("[lgpush][setNoti] byte_DATA=" + byte_DATA);

			log.debug("[lgpush][setNoti] 서버 요청 byte_MessageID=" + 15);
			log.debug("[lgpush][setNoti] 서버 요청 byte_TransactionDate=" + DateFormatUtils.format(new Date(), "yyyyMMdd"));//GlobalCom.getTodayFormat());
			log.debug("[lgpush][setNoti] 서버 요청 byte_TransactionNum=" + tTransactionNum);
			log.debug("[lgpush][setNoti] 서버 요청 byte_ChannelID=" + psVO.getChannelID());
			log.debug("[lgpush][setNoti] 서버 요청 byte_Reserved1=" + "  ");
			log.debug("[lgpush][setNoti] 서버 요청 byte_DestinationIP=" + PushSocketListComm.destinationIP);
			log.debug("[lgpush][setNoti] 서버 요청 byte_Reserved2=" + "       ");
			log.debug("[lgpush][setNoti] 서버 요청 byte_DATALength=" + jsonStr.length());
			log.debug("[lgpush][setNoti] 서버 요청 byte_DATA=" + jsonStr);

			int byteTotalLen = byte_MessageID.length
					+ byte_TransactionID.length
					+ byte_ChannelID.length
					+ byte_Reserved1.length
					+ byte_DestinationIP.length
					+ byte_Reserved2.length
					+ byte_DATALength.length
					+ byte_DATA.length;

			log.debug("byteTotalLen=" + byteTotalLen);

			byte[] byteTotalData = new byte[byteTotalLen];
			ByteUtil.setbytes(byteTotalData, 0,
					byte_MessageID);
			ByteUtil.setbytes(byteTotalData, byte_MessageID.length,
					byte_TransactionID);
			ByteUtil.setbytes(byteTotalData, (byte_MessageID.length + byte_TransactionID.length),
					byte_ChannelID);
			ByteUtil.setbytes(byteTotalData, (byte_MessageID.length + byte_TransactionID.length + byte_ChannelID.length),
					byte_Reserved1);
			ByteUtil.setbytes(byteTotalData, (byte_MessageID.length + byte_TransactionID.length + byte_ChannelID.length + byte_Reserved1.length),
					byte_DestinationIP);
			ByteUtil.setbytes(byteTotalData, (byte_MessageID.length + byte_TransactionID.length + byte_ChannelID.length + byte_Reserved1.length + byte_DestinationIP.length),
					byte_Reserved2);
			ByteUtil.setbytes(byteTotalData, (byte_MessageID.length + byte_TransactionID.length + byte_ChannelID.length + byte_Reserved1.length + byte_DestinationIP.length + byte_Reserved2.length),
					byte_DATALength);
			ByteUtil.setbytes(byteTotalData, (byte_MessageID.length + byte_TransactionID.length + byte_ChannelID.length + byte_Reserved1.length + byte_DestinationIP.length + byte_Reserved2.length + byte_DATALength.length),
					byte_DATA);

			log.debug("byteTotalData=" + byteTotalData);

			log.info("[lgpush][" + psVO.getChannelID() + "][R][" + PushSocketListComm.pushSocketQueue.size() + "][" + psVO.getApp_id() + "][" + psVO.getService_id() + "][" + psVO.getService_key() + "] - [" + jsonStr + "]");

			psVO.getPushDataOut().write(byteTotalData);
			psVO.getPushDataOut().flush();

			log.debug("======================= Header =========================");
			byte[] byte_response_MessageID = new byte[4];
			psVO.getPushDataIn().read(byte_response_MessageID, 0, byte_response_MessageID.length);
			int response_MessageID = ByteUtil.getint(byte_response_MessageID, 0);
			log.debug("[lgpush][setNoti]  서버 응답 response_MessageID = " + response_MessageID);

			byte[] byte_response_TransactionDate = new byte[8];
			psVO.getPushDataIn().read(byte_response_TransactionDate, 0, byte_response_TransactionDate.length);
			String response_TransactionDate = new String(byte_response_TransactionDate);
			log.debug("[lgpush][setNoti]  서버 응답 response_TransactionDate = " + response_TransactionDate);

			byte[] byte_response_TransactionNum = new byte[4];
			psVO.getPushDataIn().read(byte_response_TransactionNum, 0, byte_response_TransactionNum.length);
			int response_TransactionNum = ByteUtil.getint(byte_response_TransactionNum, 0);
			log.debug("[lgpush][setNoti]  서버 응답 response_TransactionNum = " + response_TransactionNum);

			byte[] byte_response_DestinationIP = new byte[16];
			psVO.getPushDataIn().read(byte_response_DestinationIP, 0, byte_response_DestinationIP.length);
			String response_DestinationIP = new String(byte_response_DestinationIP);
			log.debug("[lgpush][setNoti]  서버 응답 response_DestinationIP = " + response_DestinationIP);

			byte[] byte_response_ChannelID = new byte[14];
			psVO.getPushDataIn().read(byte_response_ChannelID, 0, byte_response_ChannelID.length);
			String response_ChannelID = new String(byte_response_ChannelID);
			log.debug("[lgpush][setNoti]  서버 응답 response_ChannelID = " + response_ChannelID);

			byte[] byte_response_Reserved1 = new byte[2];
			psVO.getPushDataIn().read(byte_response_Reserved1, 0, byte_response_Reserved1.length);
			String response_Reserved1 = new String(byte_response_Reserved1);
			log.debug("[lgpush][setNoti]  서버 응답 response_Reserved1 = " + response_Reserved1);

			byte[] byte_response_Reserved2 = new byte[12];
			psVO.getPushDataIn().read(byte_response_Reserved2, 0, byte_response_Reserved2.length);
			String response_Reserved2 = new String(byte_response_Reserved2);
			log.debug("[lgpush][setNoti]  서버 응답 response_Reserved2 = " + response_Reserved2);

			byte[] byte_response_DataLength = new byte[4];
			psVO.getPushDataIn().read(byte_response_DataLength, 0, byte_response_DataLength.length);
			int response_DataLength = ByteUtil.getint(byte_response_DataLength, 0);
			log.debug("[lgpush][setNoti]  서버 응답 response_DataLength = " + response_DataLength);

			if (response_DataLength > 1000) {
				response_DataLength = 16;
			}

			byte[] byte_response_Data = new byte[response_DataLength];
			psVO.getPushDataIn().read(byte_response_Data, 0, byte_response_Data.length);
			String responseValue = new String(byte_response_Data);
			log.debug("[lgpush][setNoti]  서버 응답 response_Data = " + responseValue);

			String response_code = "FA";
			String response_data = "";
			if (responseValue.length() > 2) {
				response_code = responseValue.substring(0, 2);
				response_data = responseValue.substring(2, responseValue.length());
			}

			log.debug("[lgpush][setNoti]  서버 응답 response_code = " + response_code);
			log.debug("[lgpush][setNoti]  서버 응답 response_data = " + response_data);

			log.info("[" + psVO.getChannelID() + "][W][" + PushSocketListComm.pushSocketQueue.size() + "][" + psVO.getApp_id() + "][" + psVO.getService_id() + "][" + psVO.getService_key() + "] - [" + response_code + "] [" + (response_data.replace(System.getProperty("line.separator"), "")).replace("\r", "") + "]");

			if (response_code.equalsIgnoreCase("SC")) {
				/*
				JSONObject jsonObject = new JSONObject(response_data);
				JSONObject response = jsonObject.getJSONObject("response");
				String status_code = "";//response.getString("status_code");
				String statusmsg = "";
				*/

				JsonNode jsonNodeR = objectMapper.readTree(response_data);
				String status_code = jsonNodeR.get("response").get("status_code").asText();
				String statusmsg = jsonNodeR.get("response").get("statusmsg").asText();
				/*
				try {
					statusmsg = response.getString("statusmsg");
				} catch (Exception e) {
					statusmsg = "statusmsg=null";
					//logger.info("["+psVO.getChannelID()+"]["+PushSocketListComm.pushSocketQueue.size()+"]["+psVO.getApp_id()+"]["+psVO.getService_id()+"]["+psVO.getService_key()+"] [statusmsg-null]");
				}
				*/

				log.debug("[lgpush]======================= Body =========================");
				log.debug("[lgpush][setNoti]  서버 응답 JSON status_code = " + status_code);

				log.debug("[lgpush][setNoti] psVO.getQueueAddChk = " + psVO.getQueueAddChk());
				log.debug("[lgpush][setNoti] PushSocketListComm.pushSocketNowCnt = " + PushSocketListComm.pushSocketNowCnt);
				log.debug("[lgpush][setNoti] PushSocketListComm.pushSocketMaxCnt = " + PushSocketListComm.pushSocketMaxCnt);

				if (psVO.getQueueAddChk().equals("ADD") && PushSocketListComm.pushSocketNowCnt <= PushSocketListComm.pushSocketMaxCnt) {
					log.debug("[lgpush][setNoti] 큐 저장 ");
					psVO.setPushSocketCallTime(System.currentTimeMillis() / 1000);//GlobalCom.getTodayUnixtime());
					addPushQueue(psVO);
				} else {
					log.debug("[lgpush][setNoti] 큐 미저장 ");
					try {
						closeSocket(psVO);
					} catch (Exception e) {
					}
				}

				/*2016.11 push statistics log*/
				resultVO.setStatusCode(status_code);

				if (status_code.equals("200")) {
					log.info("[lgpush][" + psVO.getChannelID() + "][E][" + PushSocketListComm.pushSocketQueue.size() + "][" + psVO.getApp_id() + "][" + psVO.getService_id() + "][" + psVO.getService_key() + "] - [SUCCESS]");
				/*	//성공
					resultVO.setFlag(MlCommProperties.getProperty("flag.success"));
					resultVO.setMessage(MlCommProperties.getProperty("message.success"));

				 */
				} else {
					log.info("[lgpush][" + psVO.getChannelID() + "][E][" + PushSocketListComm.pushSocketQueue.size() + "][" + psVO.getApp_id() + "][" + psVO.getService_id() + "][" + psVO.getService_key() + "] - [" + statusmsg + "] [FAIL]");
				/*	//실패
					CustomException exception = new CustomException();
					if(status_code.equals("202")){
						exception.setFlag(MlCommProperties.getProperty("flag.pushgw.accepted"));
						exception.setMessage(MlCommProperties.getProperty("message.pushgw.accepted"));
					}else if(status_code.equals("400")){
						exception.setFlag(MlCommProperties.getProperty("flag.pushgw.badRequest"));
						exception.setMessage(MlCommProperties.getProperty("message.pushgw.badRequest"));
					}else if(status_code.equals("401")){
						exception.setFlag(MlCommProperties.getProperty("flag.pushgw.unAuthorized"));
						exception.setMessage(MlCommProperties.getProperty("message.pushgw.unAuthorized"));
					}else if(status_code.equals("403")){				
						exception.setFlag(MlCommProperties.getProperty("flag.pushgw.forbidden"));
						exception.setMessage(MlCommProperties.getProperty("message.pushgw.forbidden"));
					}else if(status_code.equals("404")){
						exception.setFlag(MlCommProperties.getProperty("flag.pushgw.notFound"));
						exception.setMessage(MlCommProperties.getProperty("message.pushgw.notFound"));
					}else if(status_code.equals("410")){
						exception.setFlag(MlCommProperties.getProperty("flag.pushgw.notexistregistid"));
						exception.setMessage(MlCommProperties.getProperty("message.pushgw.notexistregistid"));
					}else if(status_code.equals("412")){
						exception.setFlag(MlCommProperties.getProperty("flag.pushgw.preconditionFailed"));
						exception.setMessage(MlCommProperties.getProperty("message.pushgw.preconditionFailed"));
					}else if(status_code.equals("500")){
						exception.setFlag(MlCommProperties.getProperty("flag.pushgw.internalError"));
						exception.setMessage(MlCommProperties.getProperty("message.pushgw.internalError"));
					}else if(status_code.equals("502")){
						exception.setFlag(MlCommProperties.getProperty("flag.pushgw.exceptionoccurs"));
						exception.setMessage(MlCommProperties.getProperty("message.pushgw.exceptionoccurs"));
					}else if(status_code.equals("503")){
						exception.setFlag(MlCommProperties.getProperty("flag.pushgw.serviceUnavailable"));
						exception.setMessage(MlCommProperties.getProperty("message.pushgw.serviceUnavailable"));
					}else{
						exception.setFlag(MlCommProperties.getProperty("flag.etc"));
						exception.setMessage(MlCommProperties.getProperty("message.etc"));
					}

				 */
					//	throw exception;

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
			/*
			else{
				resultVO.setStatusCode("900");
				
				log.info("[lgpush]["+psVO.getChannelID()+"][E]["+PushSocketListComm.pushSocketQueue.size()+"]["+psVO.getApp_id()+"]["+psVO.getService_id()+"]["+psVO.getService_key()+"] - [FAIL]");

				try{
					closeSocket(psVO);
				}catch(Exception e){}
				
				CustomException exception = new CustomException();
				exception.setFlag(MlCommProperties.getProperty("flag.pushgw.fail"));
				exception.setMessage(MlCommProperties.getProperty("message.pushgw.fail"));
				throw exception;
			}
			*/
		/*
		}catch(ConnectException e){
			resultVO.setStatusCode("901");
			
			log.info("[lgpush]["+psVO.getChannelID()+"][E]["+PushSocketListComm.pushSocketQueue.size()+"]["+psVO.getApp_id()+"]["+psVO.getService_id()+"]["+psVO.getService_key()+"] - [FAIL]");
			log.debug("[lgpush][setNoti][ConnectException]["+e.getClass().getName()+"]["+e.getMessage()+"]");
			closeSocket(psVO);
			
			CustomException exception = new CustomException();
			exception.setFlag(MlCommProperties.getProperty("flag.pushgw.socket"));
			exception.setMessage(MlCommProperties.getProperty("message.pushgw.socket"));
			throw exception;
		}catch(SocketException e){
			resultVO.setStatusCode("902");
			
			log.info("[lgpush]["+psVO.getChannelID()+"][E]["+PushSocketListComm.pushSocketQueue.size()+"]["+psVO.getApp_id()+"]["+psVO.getService_id()+"]["+psVO.getService_key()+"] - [FAIL]");
			log.debug("[lgpush][setNoti][SocketException]["+e.getClass().getName()+"]["+e.getMessage()+"]");
			closeSocket(psVO);
			
			CustomException exception = new CustomException();
			exception.setFlag(MlCommProperties.getProperty("flag.pushgw.socket"));
			exception.setMessage(MlCommProperties.getProperty("message.pushgw.socket"));
			throw exception;
			
		}catch(SocketTimeoutException e){
			resultVO.setStatusCode("903");
			
			log.info("[lgpush]["+psVO.getChannelID()+"][E]["+PushSocketListComm.pushSocketQueue.size()+"]["+psVO.getApp_id()+"]["+psVO.getService_id()+"]["+psVO.getService_key()+"] - [FAIL]");
			log.debug("[lgpush][setNoti][SocketTimeoutException]["+e.getClass().getName()+"]["+e.getMessage()+"]");
			closeSocket(psVO);
			
			CustomException exception = new CustomException();
			exception.setFlag(MlCommProperties.getProperty("flag.pushgw.socketime"));
			exception.setMessage(MlCommProperties.getProperty("message.pushgw.socketime"));
			throw exception;
			
		}catch(CustomException e){
			throw e;
		}catch(Exception e){
			log.info("[lgpush]["+psVO.getChannelID()+"][E]["+PushSocketListComm.pushSocketQueue.size()+"]["+psVO.getApp_id()+"]["+psVO.getService_id()+"]["+psVO.getService_key()+"] - [FAIL]");
			log.debug("[lgpush][setNoti][Exception]["+e.getClass().getName() + "]["+e.getMessage()+"]");
			closeSocket(psVO);
			
			CustomException exception = new CustomException();
			exception.setFlag(MlCommProperties.getProperty("flag.etc"));
			exception.setMessage(MlCommProperties.getProperty("message.etc"));
			throw exception;
		}
		*/
		log.debug("[lgpush][setNoti] END");

		return resultVO;
	}

	@Override
	public PushSocketVO getPushSocket() throws Exception{
		synchronized(PushSocketListComm.pushSocketQueue) {

			PushSocketVO psVO = null;
		
			try{
				//소켓 존재 하지 않을경우
				if(PushSocketListComm.pushSocketQueue.size() <= 0){
					psVO = OpenSocket();
					//[변경]생성에서 바로 쓰도록.
					//psVO = PushSocketListComm.pushSocketQueue.remove(0);
				}else{
					try{
						psVO = PushSocketListComm.pushSocketQueue.remove(0);
					} catch(Exception e){
						log.info("[lgpush][getPushSocket]Queue Clear");
						try{
							PushSocketListComm.pushSocketQueue.clear();
						}catch(Exception ex){
							log.info("[lgpush][getPushSocket]Queue Clear Error[" + ex.getMessage() + "]");
						}
						//psVO = OpenSocket();
						//[변경]retry에 의존하도록..
						NoSuchElementException nee = new NoSuchElementException();
						throw nee;
					}

					if(((System.currentTimeMillis()/1000)-psVO.getPushSocketCallTime()) >= PushSocketListComm.pushSocketCloseSecend){
						//소켓 유효시간이 지났을 경우
						log.debug("[lgpush][getPushSocket]소켓 유효 시간 지남");
						try{
							closeSocket(psVO);
						}catch(Exception e){}
						psVO = OpenSocket();
						//유효시간이 지난게 나오기 시작하면 최소 소켓 갯수를 유지하도록 줄여나간다.
						if(PushSocketListComm.pushSocketQueue.size() >= PushSocketListComm.pushSocketMinCnt){
							psVO.setQueueAddChk("NO_ADD");
						}
					}else{
						//소켓이 유효할 경우
						log.debug("[lgpush][getPushSocket] 소켓 유효함");
					}
				}

			}catch(ConnectException e){
				log.info("[lgpush][getPushSocket][ConnectException]["+psVO.getChannelID()+"]["+PushSocketListComm.pushSocketQueue.size()+"]["+psVO.getApp_id()+"]["+psVO.getService_id()+"]["+psVO.getService_key()+"]");
				log.debug("[lgpush][getPushSocket][ConnectException]["+e.getClass().getName()+"]["+e.getMessage()+"]");
				try{
					closeSocket(psVO);
				}catch(Exception e2){}

			}catch(SocketException e){
				log.info("[lgpush][getPushSocket][SocketException]["+psVO.getChannelID()+"]["+PushSocketListComm.pushSocketQueue.size()+"]["+psVO.getApp_id()+"]["+psVO.getService_id()+"]["+psVO.getService_key()+"]");
				log.debug("[lgpush][getPushSocket][SocketException]["+e.getClass().getName()+"]["+e.getMessage()+"]");
				try{
					closeSocket(psVO);
				}catch(Exception e2){}
				
			}catch(SocketTimeoutException e){
				log.info("[lgpush][getPushSocket][SocketTimeoutException]["+psVO.getChannelID()+"]["+PushSocketListComm.pushSocketQueue.size()+"]["+psVO.getApp_id()+"]["+psVO.getService_id()+"]["+psVO.getService_key()+"]");
				log.debug("[lgpush][getPushSocket][SocketTimeoutException]["+e.getClass().getName()+"]["+e.getMessage()+"]");
				try{
					closeSocket(psVO);
				}catch(Exception e2){}
			}catch(Exception e){
				log.info("[lgpush][getPushSocket][Exception]["+psVO.getChannelID()+"]["+PushSocketListComm.pushSocketQueue.size()+"]["+psVO.getApp_id()+"]["+psVO.getService_id()+"]["+psVO.getService_key()+"] ["+e.getMessage()+"]["+e.getClass().getName()+"]");
				log.debug("[lgpush][getPushSocket][Exception]["+e.getClass().getName()+"]["+e.getMessage()+"]");
				try{
					closeSocket(psVO);
				}catch(Exception e2){}
			}
			
			log.debug("[lgpush][getPushSocket]END");
			return psVO;
			
		}
	}
	
	public  PushSocketVO OpenSocket() throws Exception{
		
		log.debug("[lgpush][OpenSocket]START");
		
		PushSocketVO psVO = null;

		try{
			
			PushSocketListComm.pushSocketNowCnt++;
			
			Socket socket = new Socket();
			SocketAddress addr = new InetSocketAddress(PushSocketListComm.pushServerIP,PushSocketListComm.pushServerPort);
			socket.connect(addr, PushSocketListComm.pushSocketSendTimeout);
			psVO = new PushSocketVO(socket);
			openInOutStream(psVO);
			
			PushSocketListComm.channelNum=(PushSocketListComm.channelNum+1)%10000;//9999 이하로 cycle
			String _channelNum = Integer.toString(PushSocketListComm.channelNum);
			if(_channelNum.length() < 4){
				int _channelNumChkLen = 4-_channelNum.length();
				for(int i=0;i<_channelNumChkLen;i++){
					_channelNum = "0"+_channelNum;
				}
			}
			psVO.setChannelID(PushSocketListComm.channelID+_channelNum);

			log.info("[lgpush]["+psVO.getChannelID()+"][OPEN_S]["+PushSocketListComm.pushSocketQueue.size()+"]");
			
			psVO.getPushSocket().setSoTimeout(PushSocketListComm.pushSocketSendTimeout);
			
			//ㅁX16 snowman0800001 ㅁX2 222.231.13.85 ㅁX19
			byte[] byte_MessageID = ByteUtil.int2byte(1);//1을 byte로 변경
			byte[] byte_TransactionID = new byte[12];//채널 연결시엔 값 없음.
			byte[] byte_ChannelID = psVO.getChannelID().getBytes();
			byte[] byte_Reserved1 = new byte[2];
			
			byte[] byte_DestinationIP = new byte[16];
			ByteUtil.setbytes(byte_DestinationIP,0,PushSocketListComm.destinationIP.getBytes());
			
			int destinationIPNullCnt = 16-PushSocketListComm.destinationIP.length();
			
			log.debug("[lgpush][OpenSocket]destinationIPNullCnt = "+destinationIPNullCnt);
			
//			for(int i=0;i<destinationIPNullCnt;i++){
//				System.out.println("123<<<");
//				ByteUtil.setbytes(byte_DestinationIP,(PushSocketListComm.destinationIP.length()+i),new byte[1]);
//			}//바로 위에서 16자리를 잡아서 넣었는데 여디서 다시 처리해 넣어야 하나???
			
			byte[] byte_Reserved2 = new byte[12];
			byte[] byte_DATALength = ByteUtil.int2byte(0);
			
			//44 + byte_ChannelID.length
			int byteTotalLen = byte_MessageID.length
					+byte_TransactionID.length
					+byte_ChannelID.length
					+byte_Reserved1.length
					+byte_DestinationIP.length
					+byte_Reserved2.length
					+byte_DATALength.length;
					
			log.debug("[lgpush][OpenSocket]byteTotalLen="+byteTotalLen);
			
			byte[] byteTotalData = new byte[byteTotalLen];
			ByteUtil.setbytes(byteTotalData, 0, 
					byte_MessageID);
			ByteUtil.setbytes(byteTotalData, byte_MessageID.length, 
					byte_TransactionID);
			ByteUtil.setbytes(byteTotalData, (byte_MessageID.length+byte_TransactionID.length), 
					byte_ChannelID);
			ByteUtil.setbytes(byteTotalData, (byte_MessageID.length+byte_TransactionID.length+byte_ChannelID.length), 
					byte_Reserved1);
			ByteUtil.setbytes(byteTotalData, (byte_MessageID.length+byte_TransactionID.length+byte_ChannelID.length+byte_Reserved1.length), 
					byte_DestinationIP);
			ByteUtil.setbytes(byteTotalData, (byte_MessageID.length+byte_TransactionID.length+byte_ChannelID.length+byte_Reserved1.length+byte_DestinationIP.length), 
					byte_Reserved2);
			ByteUtil.setbytes(byteTotalData, (byte_MessageID.length+byte_TransactionID.length+byte_ChannelID.length+byte_Reserved1.length+byte_DestinationIP.length+byte_Reserved2.length), 
					byte_DATALength);
			
			log.debug("[OpenSocket]byteTotalData="+byteTotalData);
			
			log.debug("[lgpush][OpenSocket]byte_MessageID="+byte_MessageID);
			log.debug("[lgpush][OpenSocket]byte_TransactionID="+byte_TransactionID);
			log.debug("[lgpush][OpenSocket]byte_ChannelID="+byte_ChannelID);
			log.debug("[lgpush][OpenSocket]byte_Reserved1="+byte_Reserved1);
			log.debug("[lgpush][OpenSocket]byte_DestinationIP="+byte_DestinationIP);
			log.debug("[lgpush][OpenSocket]byte_Reserved2="+byte_Reserved2);
			log.debug("[lgpush][OpenSocket]byte_DATALength="+byte_DATALength);
			
			log.debug("[lgpush][OpenSocket]서버 요청 byte_MessageID="+1);
			log.debug("[lgpush][OpenSocket]서버 요청 byte_TransactionDate="+"");
			log.debug("[lgpush][OpenSocket]서버 요청 byte_TransactionNum="+"");
			log.debug("[lgpush][OpenSocket]서버 요청 byte_ChannelID="+psVO.getChannelID());
			log.debug("[lgpush][OpenSocket]서버 요청 byte_Reserved1="+"  ");
			log.debug("[lgpush][OpenSocket]서버 요청 byte_DestinationIP="+PushSocketListComm.destinationIP);
			log.debug("[lgpush][OpenSocket]서버 요청 byte_Reserved2="+"       ");
			
			psVO.getPushDataOut().write(byteTotalData);
			psVO.getPushDataOut().flush();
			
			
			log.debug("======================= Header =========================");
			byte[] byte_response_MessageID = new byte[4];
			psVO.getPushDataIn().read(byte_response_MessageID, 0, byte_response_MessageID.length);//넘어온 DataInputStream에서 필요 byte 자리수 만큼 자르면서 받는다.
			int response_MessageID = ByteUtil.getint(byte_response_MessageID,0);
			log.debug("[lgpush][OpenSocket] 서버 응답 response_MessageID = "+response_MessageID);
			
			byte[] byte_response_TransactionDate = new byte[8];
			psVO.getPushDataIn().read(byte_response_TransactionDate, 0, byte_response_TransactionDate.length);
			String response_TransactionDate = new String(byte_response_TransactionDate);
			log.debug("[lgpush][OpenSocket] 서버 응답 response_TransactionDate = "+response_TransactionDate);
			
			byte[] byte_response_TransactionNum = new byte[4];
			psVO.getPushDataIn().read(byte_response_TransactionNum, 0, byte_response_TransactionNum.length);
			int response_TransactionNum = ByteUtil.getint(byte_response_TransactionNum,0);
			log.debug("[lgpush][OpenSocket] 서버 응답 response_TransactionNum = "+response_TransactionNum);
			
			byte[] byte_response_DestinationIP = new byte[16];
			psVO.getPushDataIn().read(byte_response_DestinationIP, 0, byte_response_DestinationIP.length);
			String response_DestinationIP = new String(byte_response_DestinationIP);
			log.debug("[lgpush][OpenSocket] 서버 응답 response_DestinationIP = "+response_DestinationIP);
			
			byte[] byte_response_ChannelID = new byte[14];
			psVO.getPushDataIn().read(byte_response_ChannelID, 0, byte_response_ChannelID.length);
			String response_ChannelID = new String(byte_response_ChannelID);
			log.debug("[lgpush][OpenSocket] 서버 응답 response_ChannelID = "+response_ChannelID);
			
			byte[] byte_response_Reserved1 = new byte[2];
			psVO.getPushDataIn().read(byte_response_Reserved1, 0, byte_response_Reserved1.length);
			String response_Reserved1 = new String(byte_response_Reserved1);
			log.debug("[lgpush][OpenSocket] 서버 응답 response_Reserved1 = "+response_Reserved1);
			
			byte[] byte_response_Reserved2 = new byte[12];
			psVO.getPushDataIn().read(byte_response_Reserved2, 0, byte_response_Reserved2.length);
			String response_Reserved2 = new String(byte_response_Reserved2);
			log.debug("[lgpush][OpenSocket] 서버 응답 response_Reserved2 = "+response_Reserved2);
			
			byte[] byte_response_DataLength = new byte[4];
			psVO.getPushDataIn().read(byte_response_DataLength, 0, byte_response_DataLength.length);
			int response_DataLength = ByteUtil.getint(byte_response_DataLength,0);
			log.debug("[lgpush][OpenSocket] 서버 응답 response_DataLength = "+response_DataLength);
			
			byte[] byte_response_Data = new byte[response_DataLength];
			psVO.getPushDataIn().read(byte_response_Data, 0, byte_response_Data.length);
			String response_Data = new String(byte_response_Data);
			log.debug("[lgpush][OpenSocket] 서버 응답 response_Data = "+response_Data);
			
			log.info("[lgpush]["+psVO.getChannelID()+"][OPEN_R]["+PushSocketListComm.pushSocketQueue.size()+"] - ["+(response_Data.replace( System.getProperty( "line.separator" ), "" )).replace( "\r", "" )+"]");
			//GlobalCom.testPushGWLog(psVO.getChannelID(), "[OPEN_R]["+PushSocketListComm.pushSocketQueue.size()+"] - ["+response_Data+"]");
			
			if(response_DataLength >= 2){
				if(response_Data.substring(0,2).equalsIgnoreCase("SC")){
					log.debug("[lgpush][OpenSocket]ChannelConnectionRequest 성공");
					log.info("[lgpush]["+psVO.getChannelID()+"][OPEN_E]["+PushSocketListComm.pushSocketQueue.size()+"] - [SUCCESS]");
					//GlobalCom.testPushGWLog(psVO.getChannelID(), "[OPEN_E]["+PushSocketListComm.pushSocketQueue.size()+"] - [SUCCESS]");
					//[변경]생성한다음엔 바로 큐에 넣지 않고 사용 후에 넣도록.
					/*if(addQueue){
						addPushQueue(psVO);
					}*/
				}else{
					//logger.info("["+psVO.getChannelID()+"][OPEN_E]["+PushSocketListComm.pushSocketQueue.size()+"] - [FAIL]");
					//GlobalCom.testPushGWLog(psVO.getChannelID(), "[OPEN_E]["+PushSocketListComm.pushSocketQueue.size()+"] - [FAIL]");
					log.debug("[lgpush][OpenSocket]ChannelConnectionRequest 실패");
					
					SocketException exception = new SocketException();
					throw exception;
				}
			}else{
				log.debug("[lgpush][OpenSocket]ChannelConnectionRequest NoStat 실패");
				
				SocketException exception = new SocketException();
				throw exception;
			}
		}catch(ConnectException e){
			log.info("[lgpush][ConnectException][OPEN_E]["+PushSocketListComm.pushSocketQueue.size()+"] - [FAIL]");
			//GlobalCom.testPushGWLog(psVO.getChannelID(), "[OPEN_E]["+PushSocketListComm.pushSocketQueue.size()+"] - [FAIL]");
			log.debug("[lgpush][OpenSocket]["+e.getClass().getName()+"]["+e.getMessage()+"]");
			closeSocket(psVO);
		}catch(SocketException e){
			log.info("[lgpush][SocketException][OPEN_E]["+PushSocketListComm.pushSocketQueue.size()+"] - [FAIL]");
			//GlobalCom.testPushGWLog(psVO.getChannelID(), "[OPEN_E]["+PushSocketListComm.pushSocketQueue.size()+"] - [FAIL]");
			log.debug("[lgpush][OpenSocket]["+e.getClass().getName()+"]["+e.getMessage()+"]");
			closeSocket(psVO);
		}catch(SocketTimeoutException e){
			log.info("[lgpush][SocketTimeoutException][OPEN_E]["+PushSocketListComm.pushSocketQueue.size()+"] - [FAIL]");
			//GlobalCom.testPushGWLog(psVO.getChannelID(), "[OPEN_E]["+PushSocketListComm.pushSocketQueue.size()+"] - [FAIL]");
			log.debug("[lgpush][OpenSocket]["+e.getClass().getName()+"]["+e.getMessage()+"]");
			closeSocket(psVO);
		}catch(Exception e){
			log.info("[lgpush][Exception][OPEN_E]["+PushSocketListComm.pushSocketQueue.size()+"] - [FAIL]");
			//GlobalCom.testPushGWLog(psVO.getChannelID(), "[OPEN_E]["+PushSocketListComm.pushSocketQueue.size()+"] - [FAIL]");
			log.debug("[lgpush][OpenSocket]["+e.getClass().getName()+"]["+e.getMessage()+"]");
			closeSocket(psVO);
		}
		log.debug("[lgpush][OpenSocket]활성화 된 소켓 수= "+PushSocketListComm.pushSocketQueue.size());
		
		log.debug("[lgpush][OpenSocket]END");
		return psVO;
	}
	
	public void openInOutStream(PushSocketVO psVO) throws Exception{
		
		log.debug("[lgpush][openInOutStream]START");
	
		psVO.setPushIn(psVO.getPushSocket().getInputStream());
		psVO.setPushDataIn(new DataInputStream(psVO.getPushIn()));
		
		psVO.setPushOut(psVO.getPushSocket().getOutputStream());
		psVO.setPushDataOut(new DataOutputStream(psVO.getPushOut()));
		
		log.debug("[lgpush][openInOutStream]END");
	}
	
	public void addPushQueue(PushSocketVO psVO){
		synchronized(PushSocketListComm.pushSocketQueue) {
			PushSocketListComm.pushSocketQueue.add(psVO);
		}
	}
	
	public void closeSocket(PushSocketVO psVO) throws Exception{		
		log.debug("[lgpush][closeSocket]START");
		try{
			PushSocketListComm.pushSocketNowCnt--;
			psVO.getPushSocket().close();
			
			if(!psVO.getPushSocket().isClosed()){
				psVO.getPushIn().close();
				psVO.getPushDataIn().close();
				
				psVO.getPushOut().close();
				psVO.getPushDataOut().close();
				
				psVO.getPushSocket().close();
			}
		
		}catch(Exception e){
			log.debug("[lgpush][closeSocket]"+e.getClass().getName());
			log.debug("[lgpush][closeSocket]"+e.getMessage());
		}
		log.debug("[lgpush][closeSocket]END");
	}
	
	public void pushSocketInitOpen() throws Exception{
		
		log.debug("[lgpush][pushSocketInitOpen]START");
		
		log.debug("[lgpush][pushSocketInitOpen]PushSocketListComm.pushGWSocketQueue.size() = "+PushSocketListComm.pushSocketQueue.size());
		
		//초기 셋팅 갯수 만큼
		for(int i=0;i<PushSocketListComm.pushSocketInitCnt && PushSocketListComm.pushSocketQueue.size() < PushSocketListComm.pushSocketInitCnt;i++){

				addPushQueue(OpenSocket());
		}
		
		log.debug("[lgpush][pushSocketInitOpen]활성화 된 소켓 수 = "+PushSocketListComm.pushSocketQueue.size());
		log.debug("[lgpush][pushSocketInitOpen]END");
	}
	
	public void pushSocketAllClose() throws Exception{
		log.debug("=======[lgpush][pushSocketAllClose] "+"소켓 정리 시작==========");
		log.debug("=======[lgpush][pushSocketAllClose] "+"size "+PushSocketListComm.pushSocketQueue.size()+"==========");
		int push_size = PushSocketListComm.pushSocketQueue.size();
		try{
			for(int i=0;i<push_size;i++){
				try{
					PushSocketVO psVO = PushSocketListComm.pushSocketQueue.remove(0);					
					log.debug("=======[lgpush][pushSocketAllClose] Push "+"소켓 정리["+i+"] "+psVO+"  ==========");
					closeSocket(psVO);
				}catch(Exception e){}
			}
		}catch(Exception e){}	
	}

}