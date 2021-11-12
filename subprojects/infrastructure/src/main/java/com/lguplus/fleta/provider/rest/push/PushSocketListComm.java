package com.lguplus.fleta.provider.rest.push;

import java.net.InetAddress;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PushSocketListComm {
	
	private final Log logger = LogFactory.getLog(this.getClass());
	
	public static List<PushSocketVO> pushSocketQueue = Collections.synchronizedList(new LinkedList<PushSocketVO>());//소켓 저장
	
	public static String pushServerIP= "";//IP
	public static int pushServerPort;//Port
	public static int pushSocketMinCnt;		//큐에 미저장 시 최소값일 경우 큐에 바로 저장하기 위한 제한 값
	public static int pushSocketMaxCnt;		//현재 사용중인 소켓이 최대값을 넘었을 경우 큐에 저장할지 체크 하기 위한 제한 값
	public static int pushSocketCloseSecend;	//Push Server에서 강제로 세션을 끊는 시간
	public static int pushSocketSendTimeout;//소켓통신 타임아웃
	
	public static int channelNum = 0;		// 채널 번호
	public static int transactionIDNum = 0;	//transaction 번호(증가)

	public static String channelID= ""; 		// 채널 아이디(서버 호스트이름) - #####Was 여러대 일때 이게 중복되믄 안된다.(서버 호스트 이름 확인)#####
	public static String destinationIP = "";	//Push G/W IP 와 동일하게 값으로 사용하면 됨

	public static int pushSocketInitCnt;	//초기 활성화 시킬 소켓 개수
	public static int pushSocketNowCnt = 0;//현재 사용중인 소켓 개수
	
	/**
	 * push 기본 값을 Propery에 설정되어 있는 값으로 설정
	 */
	public void pushSocketStatusSetting(){
		logger.debug("lgpushSocketSetting START ");
		/*
		PushSocketListComm.pushServerIP = MlCommProperties.getProperty("lgpush.server.ip");
		PushSocketListComm.pushServerPort = Integer.parseInt(MlCommProperties.getProperty("lgpush.server.port"));
		PushSocketListComm.pushSocketMaxCnt = Integer.parseInt(MlCommProperties.getProperty("lgpush.socket.max"));
		PushSocketListComm.pushSocketMinCnt = Integer.parseInt(MlCommProperties.getProperty("lgpush.socket.min"));
		PushSocketListComm.pushSocketCloseSecend = Integer.parseInt(MlCommProperties.getProperty("lgpush.socket.close_secend"));
		PushSocketListComm.pushSocketSendTimeout = Integer.parseInt(MlCommProperties.getProperty("lgpush.socket.timeout"));
		PushSocketListComm.pushSocketInitCnt = Integer.parseInt(MlCommProperties.getProperty("lgpush.socket.initCnt"));
		PushSocketListComm.channelNum = Integer.parseInt(MlCommProperties.getProperty("lgpush.socket.channelNum"));
		*/
		try{
			//ChannelID는 HOST NAME 뒷자리에서 8자리를 자르고 8자리보다 작을경우 0을 붙여준다
			InetAddress addr = InetAddress.getLocalHost();
			String hostname = addr.getHostName();
			logger.debug("lghostname 1 = "+hostname);
			//기존에 8이였고 새로운 연동정이서엔 3으로 되어 있지만 8로 쓰면된다고 담당자가 그랬음.. 네이블 김석환 주임
			if(hostname.length() > 8){
				hostname = hostname.substring((hostname.length()-8),hostname.length());
			}else{
				int hostnameCnt = 8-hostname.length();
				for(int i=0;i<hostnameCnt;i++){
					hostname += "0";
				}
			}
			logger.debug("lghostname = "+hostname);
			
			//Host(3) + Port(2)
			String port = "1119";//GlobalCom.getSystemProperty("JBOSS_PORT");
			try{
				port = port.substring(0,2);
			}catch (Exception e) {	//방어코드
				port = "00";
			}
			logger.debug("lgport = "+port);
			PushSocketListComm.channelID = hostname+port;
		}catch(Exception e){
			PushSocketListComm.channelID = "111";//MlCommProperties.getProperty("lgpush.socket.channelID");
		}
		
		PushSocketListComm.destinationIP = "127.0.0.1";//MlCommProperties.getProperty("lgpush.cp.destination_ip");
		
		logger.debug("lgpushSocketSetting END ");
	}

}