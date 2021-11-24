package com.lguplus.fleta.provider.rest.push;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PushCall{
	private final Log logger = LogFactory.getLog("sendRequestPush");
	
	private PushService psService;
	private int retry;
	private String app_id;
	private String service_id;
	private String service_key;
	private String noti_message;
	private List<String> arrItem;
	
	/**
	 * PushGW Call 생성자
	 */
	public PushCall(String s_app_id, String s_service_id, String s_service_key, int s_retry, String s_noti_message, List<String> s_arrItem){
		
		this.app_id = s_app_id;
		this.service_id = s_service_id;
		this.service_key = s_service_key;
		this.retry = s_retry;
		this.noti_message = s_noti_message;
		this.arrItem = s_arrItem;
		
		//LGPush일 경우
		if(true) { //MlCommProperties.getProperty("lgpush.service_id").equals(s_service_id)){
			psService = new PushServiceLGPushImpl();//com.dmi.pushagent.PushUtil.lgpush.service.impl.PushServiceImpl();
		}else{//아닐경우
			//psService = new com.dmi.pushagent.PushUtil.hdtv.service.impl.PushServiceImpl();
		}
	}
	
	public String call() throws Exception {
		logger.debug("[PushCall]메서드 시작");
		logger.debug("[PushCall]메서드 Retry 카운트 = "+retry);
		
		String result = "";
		
		try {
			PushSocketVO psVO = psService.getPushSocket();
			psVO.setApp_id(app_id);
			psVO.setService_id(service_id);
			psVO.setService_key(service_key);
			psVO.setNoti_message(noti_message);	
			psVO.setArrItem(arrItem);		
//			psService.setNoti(psVO);
			
			/*2016.11 push statistics log*/
			PushResultVO vo = psService.setNoti(psVO);
			result = vo.getStatusCode();
		/*
		} catch (CustomException e) {
			//retry 제외목록에 있는 코드가 넘어오면 retry하지 않는다.
			boolean retYn = true;
			String pushCode = e.getFlag();
			for(String exCode : (String[]) MlCommProperties.getEtcProperty("retry.exclud.codeList")){
				if(exCode.equals(pushCode)){
					retYn = false;
					break;
				}
			}
			if(retYn){
				result = retryCall(e);
			}else{
				result = "900";
				logger.info("[PushCall]PushMsg: "+e.getMessage());
				throw e;
			}
			*/
	    } catch (Exception e) {
			retryCall(e);
		}
		logger.debug("PushCall  메서드 종료");
		return result;
	}
	
	public String retryCall(Exception e) throws Exception{
		String result = "";
		if(true) //e instanceof CustomException)
			logger.info("[PushCall][RETRY]["+retry--+"]"+e.getMessage());
		else
			logger.info("[PushCall][RETRY]["+retry--+"]PushMsg:"+e.getClass().getName()+" : "+e.getMessage());
		
		if(retry >= 0){
			result = call();
		}else{
			throw e;
		}
		return result;
	}
	
}
