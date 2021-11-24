package com.lguplus.fleta.provider.rest.multipush;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Slf4j
public class RequestPushController {

	private static List<String> PUSH_REJECT_REGLIST = Collections.emptyList(); 
	
	private static final int MAX_COUNT_PER_REQUEST = 5000;	
	private static final int SECOND = 1000;
	private final String lock = "";


	/**
	 * Multi Push 발송
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/v1/push/multi", method=RequestMethod.POST, headers="Content-Type=application/xml")
	public void multiPushRequest (
			@RequestParam(value="app_id", required=false) String app_id,
			@RequestParam(value="service_id", required=false) String service_id,
			@RequestParam(value="multi_count", required=false) String multi_count,
			@RequestParam(value="push_type", 	 required=false) String push_type,
			@RequestBody MultiPushListVo hPushList,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log sendlogger = LogFactory.getLog("multiPushRequest");
		final String remotAdr = request.getRemoteAddr();
		
		/*2016.11 push statistics log*/
		Map<String, String>map = new HashMap<String, String>();
		map.put("app_id", app_id);
		map.put("service_id", service_id);
		map.put("multi_count", multi_count);
		map.put("remotAdr", remotAdr );
		map.put("push_type", push_type );
		int soucces_cnt = 0;
				
		try {	
			long currentTime = System.currentTimeMillis();
			
			String msg = hPushList.getMsg();
			List<String> items = new ArrayList<String>();
			
			map.put("msg", msg );
			map.put("itemSize", ""+items.size() );
			map = resultParser(map);
			
			// 초당 최대 Push 전송 허용 갯수 
			int maxLimitPush = 1000;//Integer.parseInt(MlCommProperties.getProperty("push.multi.socket.tps"));	// default
			// 초당 400건
			if (StringUtils.hasText(multi_count) && multi_count.trim().matches("^\\d*$")) {
				int multiCount = Integer.parseInt(multi_count.trim());
				maxLimitPush = (multiCount < maxLimitPush) ? multiCount : maxLimitPush;
			}
			
			// 메시지 items 유효성 체크 && 최대 TPS 설정
			if (hPushList.getItems() != null && hPushList.getItems().getItem() != null) {
				items = hPushList.getItems().getItem();
			}
			
			sendlogger.info("[MultiPushRequest][S] - ["+remotAdr+"]["+app_id+"]["+service_id+"]["+msg+"]["+items.size()+"]["+hPushList.getUsers().getReg_id().size()+"]");
			sendlogger.info("[MultiPushRequest][S] Max number of push notification per sec : " + maxLimitPush);
			
			MessageService messageService = MessageService.getInstance();
			
			List<MultiPushListVo.MultiPushUser> lstUsers = new ArrayList<MultiPushListVo.MultiPushUser>();
			List<String> lstFailUsers = new ArrayList<String>();
			String transactionID = null;
			
			// TransactionID - 메시지 일련 번호 최대값 Reset
			if (MessageService.TRANSACTION_SEQ_NO.get() > MessageService.TRANSACTION_MAX_SEQ_NO) {
				MessageService.TRANSACTION_SEQ_NO.set(0);
			}

			synchronized (lock) {	// Push GW 제한 성능 문제로 동시 발송을 허용하지 않음. 
				// Push GW 서버 connection이 유효한지 확인
				if(!messageService.isConnected()) {
					if(messageService.connect() && !messageService.isConnected()) {
						//exceptionHandler("pushgw.socket");
						throw new Exception("pushgw.socket");
					}
					messageService.channelConnectionRequest();	// 접속 요청 메시지 전송 
				}
				
				// Channel이 유효한지 확인, 아닌 경우 Channel을 Re-Open함 
				if(!messageService.isChannelValid()) {
					sendlogger.info("[MultiPushRequest][C] the current channel is not valid, re-connect again."); 
					messageService.disconnect();
					messageService.connect();	// 재접속 (Channel이 유효하지 않는 경우, 접속 강제 종료 후 재접속함)
					
					if(messageService.channelConnectionRequest() && !messageService.isChannelValid()) {
						//exceptionHandler("pushgw.serviceUnavailable");
						throw new Exception("pushgw.serviceUnavailable");
					}
				}
				
				int count = 1;
				long timestamp = System.currentTimeMillis();
				
				// Push 메시지 전송 via G/W (비동기 호출)
				for (String reg_id : hPushList.getUsers().getReg_id()) {
					// 사용자별 필수 값 체크 & 발송 제외 가번 확인 
					if (!StringUtils.hasText(reg_id) || PUSH_REJECT_REGLIST.contains(reg_id.trim())) {
						continue;
					}
					// Push 메시지 전송
					transactionID = messageService.commandRequest(app_id, service_id, reg_id, msg, items);
					
					if (transactionID == null) {	// Network I/O 오류
						sendlogger.info("[MultiPushRequest][Push] - ["+remotAdr+"]["+reg_id+"]["+app_id+"]["+service_id+"][IO] - [FA]"); 
						lstFailUsers.add(reg_id);
					} else {	// 메시지 전송 완료
						sendlogger.debug("[MultiPushRequest][Push] - ["+remotAdr+"]["+reg_id+"]["+app_id+"]["+service_id+"] - [SENT]");
						lstUsers.add(new MultiPushListVo.MultiPushUser(reg_id, transactionID));
					}
					
					// TPS 설정에 따른 Time delay
					if (count % maxLimitPush == 0) {
						long timeMillis = System.currentTimeMillis() - timestamp;
						if (timeMillis < SECOND) {
							Thread.sleep(SECOND - timeMillis);
							sendlogger.debug("[MultiPushRequest][Push] sleep for " + count + ", id=" + Thread.currentThread().getId());
						}
						timestamp = System.currentTimeMillis();
					}
					count++;	
				}
			}
			
			MessageInfo responseMsg = null;
			//CustomException exception = new CustomException();
			
			// Push 전송 결과 확인 및 처리 (비동기 방식으로 작동함. 서버의 응답을 임시로 메모리(map)에 저장한 후 응답 결과를 메모리에서 읽음)
			for (MultiPushListVo.MultiPushUser user : lstUsers) {
				responseMsg = messageService.removeMessageInfo(user.getTransaction_id());
				
				if (responseMsg == null) {	// 현재 메모리에 결과가 없는 경우 주어진 시간 동안 대기한 후 다시 읽음
					long readWaited = 0L;
					while (responseMsg == null && readWaited < SECOND * 2) {
						Thread.sleep(1L);
						responseMsg = messageService.removeMessageInfo(user.getTransaction_id());
						readWaited++;
					}
					// Push 서버에서 응당을 못방은 경우임.
					if (responseMsg == null) {
						sendlogger.info("[MultiPushRequest][Push] - ["+remotAdr+"]["+user.getP_reg_id()+"]["+app_id+"]["+service_id
								+"]["+user.getTransaction_id()+"][null] - [FA]"); 
						lstFailUsers.add(user.getP_reg_id());
						continue;
					}
				} 
				
				if (MsgEntityCommon.SUCCESS.equals(responseMsg.getResult())) {
					if ("200".equals(responseMsg.getStatusCode())) {
						soucces_cnt++;
						// Push 메시지 전송 성공
						sendlogger.info("[MultiPushRequest][Push] - ["+remotAdr+"]["+user.getP_reg_id()+"]["+app_id+"]["+service_id
							+"]["+user.getTransaction_id()+"]["+responseMsg.getStatusCode()+"] - [SC]"); 
					} else {
						/*
						if("202".equals(responseMsg.getStatusCode())){
							exception.setFlag(MlCommProperties.getProperty("flag.pushgw.accepted"));
							exception.setMessage(MlCommProperties.getProperty("message.pushgw.accepted"));
							throw exception;
						} else if("400".equals(responseMsg.getStatusCode())) {
							exception.setFlag(MlCommProperties.getProperty("flag.pushgw.badRequest"));
							exception.setMessage(MlCommProperties.getProperty("message.pushgw.badRequest"));
							throw exception;
						} else if("401".equals(responseMsg.getStatusCode())) {
							exception.setFlag(MlCommProperties.getProperty("flag.pushgw.unAuthorized"));
							exception.setMessage(MlCommProperties.getProperty("message.pushgw.unAuthorized"));
							throw exception;
						} else if("403".equals(responseMsg.getStatusCode())) {				
							exception.setFlag(MlCommProperties.getProperty("flag.pushgw.forbidden"));
							exception.setMessage(MlCommProperties.getProperty("message.pushgw.forbidden"));
							throw exception;
						} else if("404".equals(responseMsg.getStatusCode())) {
							exception.setFlag(MlCommProperties.getProperty("flag.pushgw.notFound"));
							exception.setMessage(MlCommProperties.getProperty("message.pushgw.notFound"));
							throw exception;
						} else if("410".equals(responseMsg.getStatusCode()) || "412".equals(responseMsg.getStatusCode())) {
							// 유효하지 않은 Reg ID인 경우 오류처리/Retry 없이 그냥 skip함
							sendlogger.info("[MultiPushRequest][Push] - ["+remotAdr+"]["+user.getP_reg_id()+"]["+app_id+"]["+service_id
									+"]["+user.getTransaction_id()+"]["+responseMsg.getStatusCode()+"][RegID] - [FA]"); 
						} else {	
							// 메시지 전송 실패 - Retry 대상
							sendlogger.info("[MultiPushRequest][Push] - ["+remotAdr+"]["+user.getP_reg_id()+"]["+app_id+"]["+service_id
									+"]["+user.getTransaction_id()+"]["+responseMsg.getStatusCode()+"] - [FA]"); 
							lstFailUsers.add(user.getP_reg_id());
						}
						 */
					}
				} else {	
					// 메시지 전송 실패 - Retry 대상
					sendlogger.info("[MultiPushRequest][Push] - ["+remotAdr+"]["+user.getP_reg_id()+"]["+app_id+"]["+service_id
							+"]["+user.getTransaction_id()+"]["+responseMsg.getStatusCode()+"] - [FA]"); 
					lstFailUsers.add(user.getP_reg_id());
				}
			}
			
			map.put("cnt", "" + soucces_cnt);
			if (lstFailUsers.isEmpty()) {
				ResultVo resultVo = new ResultVo();
				resultVo.setFlag("");//MlCommProperties.getProperty("flag.success"));
				resultVo.setMessage("");//MlCommProperties.getProperty("message.success"));
				sendlogger.info("[MultiPushRequest][E] - ["+messageService.getChannelID()+"]["+remotAdr+"]["+app_id+"]["+service_id+"]["+lstUsers.size()+"]["
						+messageService.getMessageInfoSize()+"]["+(System.currentTimeMillis()-currentTime)+"msec] - [SUCCESS]");
				/*2016.11 push statistics log*/
				map.put("push_status", "200");
				printPushLog(map);
				//return resultVo;
			} else {	// 메시지 전송이 한건이라도 실패한 경우
				/*
				MultiResultVo multiResultVo = new MultiResultVo();
				multiResultVo.setCode(MlCommProperties.getProperty("flag.pushgw.sendingfailed"));
				multiResultVo.setMessage(MlCommProperties.getProperty("message.pushgw.sendingfailed"));
				multiResultVo.setFail_users(new MultiPushFailUser(lstFailUsers));
				sendlogger.info("[MultiPushRequest][E] - ["+messageService.getChannelID()+"]["+remotAdr+"]["+app_id+"]["+service_id+"]["+lstUsers.size()+"]["
						+lstFailUsers.size()+"]["+messageService.getMessageInfoSize()+"]["+(System.currentTimeMillis()-currentTime)+"msec] - [FAIL]");

				if( soucces_cnt > 0 )
					map.put("push_status", "200");
				else 
					map.put("push_status", "900");
				printPushLog(map);
				return multiResultVo;
				*/
			}
		} catch(Exception e) {
			/*2016.11 push statistics log*/
			map.put("cnt", "" + soucces_cnt);
			map.put("push_status", "900");
			printPushLog(map);
			sendlogger.info("[MultiPushRequest][E] - ["+remotAdr+"]["+app_id+"]["+service_id+"]["+e.getMessage()+"] - [FAIL]");
			//CustomException ce = new CustomException(e);
			throw e;//new Exception(e.toString(), e.getCause());
		}
	}
	
	public Map<String,String> resultParser(Map<String, String> map){
		Map<String,String> resultMap;

		try{
			if( "G".equals(map.get("push_type")) ){
				// Android [Single, Multi Announcement]
				// Android [Single] : {service_id=30011, push_type=G, request=, reg_id=01022337241, app_id=lguplushdtvgcm, msg=\"PushCtrl\":\"MSG\",\"result\":{\"s_type\":\"I\",\"n_type\":\"A\",\"noti_type\":\"N1\",\"event_type\":\"NON\",\"title\":\"aaaaaaaa\",\"msg\":\"aaaaaaaaaa\",\"id\":\"602\",\"push_id\":\"N1-20161121092454\"}}
				// Android [Announcement] : {service_id=30011, push_type=G, items=, request=, item=gcm_multi_count!^300, remotAdr=127.0.0.1, app_id=lguplushdtvgcm, msg=\"PushCtrl\":\"MSG\",\"result\":{\"s_type\":\"I\",\"n_type\":\"A\",\"noti_type\":\"N1\",\"event_type\":\"NON\",\"title\":\"aaaaaaaaa\",\"msg\":\"aaaaaaaaaaaa\",\"id\":\"618\",\"push_id\":\"N1-20161121091614\"}}
				String temp_msg = map.get("msg").replace("{", "").replace("}", "").replace("\\\"", "");
				String[] msg_tokens = temp_msg.split(":",2);
				List<String> mlist = Arrays.asList(msg_tokens[1].split(","));
				
				// msg 데이터 초기화
				map.put("msg", "");
				
				if(mlist.size()>0){
					// arrary -> map
					for(String item : mlist){
						String[] temp = item.split(":");
						if(temp.length >= 2){
							map.put(temp[0], temp[1]);
						}
					}
				}
			} else if ( "A".equals(map.get("push_type")) ){
				// IOS [Single, Multi Announcement]
				// IOS [Announcement] : {service_id=30021, push_type=A, items=, request=, item=cm!^NEW|I|A|N1|NON||603|||||N1-20161121093259, remotAdr=127.0.0.1, app_id=lguplushdtvapns, msg=\"body\":\"bbbbbbbbbbbb\"}
				String temp_msg = map.get("msg").replace("{", "").replace("}", "").replace("\\\"", "");
				List<String> mlist = Arrays.asList(temp_msg.split(","));
				if(mlist.size()>0){
					// arrary -> map
					for(String item : mlist){
						String[] temp = item.split(":");
						if(temp.length >= 2){
							if( "body".equals(temp[0])){
								map.put("title", temp[1]);
							}
						}
					}
				}
				map.put("msg", map.get("title"));
				
				//String[] nkey = {"cmd", "showType", "netType", "notiGB", "notiType", "imgURL", "regNumber", "p1", "p2", "p3", "p4", "push_id"};
				String[] nkey = {"cmd", "showType", "netType", "noti_type", "event_type", "imgURL", "reg_id", "p1", "p2", "p3", "p4", "push_id"};
				
				String temp_item = map.get("item");
				String[] nlist = temp_item.split("\\|");
				if(nlist.length > 0){
					for(int i=0 ; i < nlist.length; i++){
						map.put(nkey[i], nlist[i]);
					} 
				}
			} 
		}catch (Exception e) {
			
		}
		
		resultMap = map;
		
		return resultMap;
	}
		
	public void printPushLog(Map<String,String> map){
		Log statuslogger = LogFactory.getLog("pushStatus");
		
		//String log_time = GlobalCom.getTodayFormat4_24();
		String log_time = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss");//Today
		String push_id = map.get("push_id")!=null?map.get("push_id"):"";
		String title = map.get("title")!=null?map.get("title"):"";
		String msg = map.get("msg")!=null?map.get("msg"):"";
		String noti_type = map.get("noti_type")!=null?map.get("noti_type"):"";
		String event_type = map.get("event_type")!=null?map.get("event_type"):"";
		String push_type = map.get("push_type")!=null?map.get("push_type"):"";
		String push_time = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"); //GlobalCom.getTodayFormat4_24();
		String push_status = map.get("push_status")!=null?map.get("push_status"):"";
		String cnt = map.get("cnt");
		
		// 새소식 일 경우에만 로그 생성
		// push_id가 없는 것들
		//   PUSH_ID=|PUSH_TITLE=부부수업 파뿌리|PUSH_MSG=부부수업 파뿌리|NOTI_TYPE=54회|EVENT_TYPE=52|OS_TYPE=A|PUSH_TIME=20170103001643|PUSH_STATUS=200|PUSH_COUNT=1
		//   PUSH_ID=|PUSH_TITLE=|PUSH_MSG=|NOTI_TYPE=LAT|EVENT_TYPE=|OS_TYPE=G|PUSH_TIME=20170103180043|PUSH_STATUS=200|PUSH_COUNT=1
		//   (cpm_adm)PUSH_ID=|PUSH_TITLE=[당신 거기 있어줄래요] 구매고객 3000원 감사쿠폰|PUSH_MSG=|NOTI_TYPE=coupon|EVENT_TYPE=|OS_TYPE=G|PUSH_TIME=20170103205420|PUSH_STATUS=200|PUSH_COUNT=1
		//   PUSH_ID=|PUSH_TITLE=<U씨네> 의 새글이 올라왔습니다. 2017 신상 드라마 라인업 총정리!|PUSH_MSG=<U씨네> 의 새글이 올라왔습니다. 2017 신상 드라마 라인업 총정리!|NOTI_TYPE=M041280|EVENT_TYPE=STY-20170103070152|OS_TYPE=A|PUSH_TIME=20170103070152|PUSH_STATUS=900|PUSH_COUNT=1
		if( "N1".equals(noti_type) || "NEW".equals(noti_type) ){
			statuslogger.info(
					"LOG_TIME=" + log_time
					 + "|" + "PUSH_ID=" + push_id
					 + "|" + "PUSH_TITLE=" + title
					 + "|" + "PUSH_MSG=" + msg
					 + "|" + "NOTI_TYPE=" + noti_type
					 + "|" + "EVENT_TYPE=" + event_type
					 + "|" + "OS_TYPE=" + push_type
					 + "|" + "PUSH_TIME=" + push_time
					 + "|" + "PUSH_STATUS=" + push_status
					 + "|" + "PUSH_COUNT=" + cnt
				);
		}
	}
	
}
