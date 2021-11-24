package com.lguplus.fleta.provider.rest.push;

public interface PushService{

	/**
	 * Push Noti 메시지 전달
	 * @param psVO	PushSocketVO
	 * @return	결과코드,결과메세지
	 * @throws Exception
	 */
	public PushResultVO setNoti(PushSocketVO psVO)throws Exception;
	
	/**
	 * PushGW 소켓 중 사용가능한 소켓 조회 메서드
	 * @return 결과코드,결과메세지
	 */
	public PushSocketVO getPushSocket() throws Exception;
	
	public PushSocketVO OpenSocket() throws Exception;
	
	/**
	 * PushGW 소켓 해제 메서드
	 * @param psVO PushSocketVO
	 */
	public void closeSocket(PushSocketVO psVO) throws Exception;
	
	/**
	 * PushGW 소켓 In/Out 스트림 활성화 메서드
	 * @param psVO PushSocketVO
	 */
	public void openInOutStream(PushSocketVO psVO) throws Exception;
	
	/**
	 * 초기 소켓 활성화
	 * @throws Exception
	 */
	public void pushSocketInitOpen() throws Exception;
	
	/**
	 * 마지막 소켓을 전부 닫아라
	 * @throws Exception
	 */
	public void pushSocketAllClose() throws Exception;

}
