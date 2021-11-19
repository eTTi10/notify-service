package com.lguplus.fleta.provider.rest.multipush;

import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.TreeMap;

/*
import com.dmi.pushagent.common.exception.CustomException;
import com.dmi.pushagent.common.property.MlCommProperties;
import com.dmi.pushagent.common.util.GlobalCom;
import com.dmi.pushagent.common.util.SHA512Hash;
import com.dmi.pushagent.common.vo.PushBaseVo;
*/

public class PushMakeBody {
	
	/**
	 * 개별Push 전문을 만든다.
	 * @param realTransaction	push번호
	 * @param pushVo	push데이터
	 * @return
	 * @throws Exception
	 */
	public static TreeMap makeBodyByNoti(String realTransaction, MessageService.PushBaseVo pushVo)  throws Exception{

		TreeMap resultMap = new TreeMap();
		String serviceId = pushVo.getService_id();
		
		String tServicePwd = "";
		/*
			staging.key1.push.service_id = 20014
			staging.key1.push.service_pwd = hdtv_GCM01

			staging.key3.push.service_id = 20002
			staging.key3.push.service_pwd = smartux
			staging.key3.push.linkage_type = LGUPUSH_OLD
		 */
		tServicePwd = "hdtv_GCM01";
		/*
		try{
			tServicePwd = CommonObject.serviceKeyMap.get(serviceId);
			if(tServicePwd==null || tServicePwd.equals("")){
				CustomException exception = new CustomException();
				exception.setFlag(MlCommProperties.getProperty("flag.pushgw.servicenotfound"));
				exception.setMessage(MlCommProperties.getProperty("message.pushgw.servicenotfound"));
				throw exception;
			}
		}catch(Exception e){
			CustomException exception = new CustomException(e);
			exception.setFlag(MlCommProperties.getProperty("flag.etc"));
			exception.setMessage(MlCommProperties.getProperty("message.etc"));
			throw exception;
		}
		*/

		/*
		String linkageType = "";
		try{
			linkageType = CommonObject.linkageTypeMap.get(serviceId);
		}catch(Exception e){}
		*/
		String linkageType = "";

		resultMap.put("msg_id", "PUSH_NOTI");

		if(StringUtils.isEmpty(linkageType)) {
			resultMap.put("service_key",pushVo.getService_key());//key가 service_key, device_id, device_token
		}
		else if("LGUPUSH_OLD".equals(linkageType)) {//구버전 LGUPUSH
			/*
				push.old.lgupush.pushAppId = smartux0001
				#LGUPUSH 구버전에서 사용하는 noti_type
				push.old.lgupush.notiType = POS
			 */
			resultMap.put("push_app_id", "smartux0001"); //MlCommProperties.getProperty("push.old.lgupush.pushAppId"));
			resultMap.put("noti_type", "POS");//MlCommProperties.getProperty("push.old.lgupush.notiType"));
			resultMap.put("regist_id",pushVo.getService_key());
		}

		/*
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
		byte[] pb = messageDigest.digest(tServicePwd.getBytes());
		StringBuffer sb = new StringBuffer(pb.length << 1);
		for (int i=0, iend=pb.length; i<iend ; i++) {
			int val = (pb[i] + 256) & 0xff;
			sb.append(Integer.toHexString(val>>4))
					.append(Integer.toHexString(val & 0xf));
		}
		String tServicePwdSha512 = sb.toString();
		*/
		MessageDigest digest = MessageDigest.getInstance("SHA-512");
		digest.reset();
		digest.update(tServicePwd.getBytes());
		String tServicePwdSha512 = String.format("%0128x", new BigInteger(1, digest.digest()));
		
		resultMap.put("push_id", realTransaction);	
		resultMap.put("service_id",pushVo.getService_id());
		resultMap.put("service_passwd", tServicePwdSha512);//SHA512Hash.getDigest(tServicePwd));	//SHA512 암호화 값
		resultMap.put("app_id",pushVo.getApp_id());
		resultMap.put("noti_contents",pushVo.getNoti_message());

		if(pushVo.getArrItem().size()>0){
			for(String itemList : pushVo.getArrItem()){
				String[] item = itemList.split("\\!\\^");
				if(item.length >= 2){
					resultMap.put(item[0], item[1]);
				}
			}
		}
		
		return resultMap;
	}
	
	/**
	 * Announcement Push 전문을 만든다.
	 * @param realTransaction
	 * @param pushVo
	 * @return
	 * @throws Exception
	 */
	public static HashMap makeBodyByAnnounce(String realTransaction, MessageService.PushBaseVo pushVo)  throws Exception{
		
		HashMap resultMap = new HashMap();
		String serviceId = pushVo.getService_id();

		String tServicePwd = "";
		/*
			staging.key1.push.service_id = 20014
			staging.key1.push.service_pwd = hdtv_GCM01

			staging.key3.push.service_id = 20002
			staging.key3.push.service_pwd = smartux
			staging.key3.push.linkage_type = LGUPUSH_OLD
		 */
		tServicePwd = "hdtv_GCM01";
		/*
		try{
			tServicePwd = CommonObject.serviceKeyMap.get(serviceId);
			if(tServicePwd==null || tServicePwd.equals("")){
				CustomException exception = new CustomException();
				exception.setFlag(MlCommProperties.getProperty("flag.pushgw.servicenotfound"));
				exception.setMessage(MlCommProperties.getProperty("message.pushgw.servicenotfound"));
				throw exception;
			}
		}catch(Exception e){
			CustomException exception = new CustomException(e);
			exception.setFlag(MlCommProperties.getProperty("flag.etc"));
			exception.setMessage(MlCommProperties.getProperty("message.etc"));
			throw exception;
		}
		*/

		/*
		String linkageType = "";
		try{
			linkageType = CommonObject.linkageTypeMap.get(serviceId);
		}catch(Exception e){}
		*/
		String linkageType = "";

		resultMap.put("msg_id", "PUSH_ANNOUNCEMENT");
		
		if("LGUPUSH_OLD".equals(linkageType)) {//구버전 LGUPUSH
			resultMap.put("push_app_id", "smartux0001"); //MlCommProperties.getProperty("push.old.lgupush.pushAppId"));
			resultMap.put("noti_type","POS");//MlCommProperties.getProperty("push.old.lgupush.notiType"));
		}

		/*
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
		byte[] pb = messageDigest.digest(tServicePwd.getBytes());
		StringBuffer sb = new StringBuffer(pb.length << 1);
		for (int i=0, iend=pb.length; i<iend ; i++) {
			int val = (pb[i] + 256) & 0xff;
			sb.append(Integer.toHexString(val>>4))
					.append(Integer.toHexString(val & 0xf));
		}
		String tServicePwdSha512 = sb.toString();
		*/
		MessageDigest digest = MessageDigest.getInstance("SHA-512");
		digest.reset();
		digest.update(tServicePwd.getBytes());
		String tServicePwdSha512 = String.format("%0128x", new BigInteger(1, digest.digest()));
		
		resultMap.put("push_id", realTransaction);	
		resultMap.put("service_id",pushVo.getService_id());
		resultMap.put("service_passwd", tServicePwdSha512);//SHA512Hash.getDigest(tServicePwd));	//SHA512 암호화 값
		resultMap.put("app_id",pushVo.getApp_id());
		resultMap.put("noti_contents",pushVo.getNoti_message());

		if(pushVo.getArrItem().size()>0){
			for(String itemList : pushVo.getArrItem()){
				String[] item = itemList.split("\\!\\^");
				if(item.length >= 2){
					resultMap.put(item[0], item[1]);
				}
			}
		}
		
		return resultMap;
	}

}
