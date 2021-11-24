package com.lguplus.fleta.provider.rest.push;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

@Getter
@Setter
@NoArgsConstructor
public class PushSocketVO extends PushBaseVo {

	private Socket pushSocket; //Push 소켓
	private InputStream pushIn;			//Push InputStream
	private DataInputStream pushDataIn;	//Push DataInputStream
	private OutputStream pushOut;		//Push OutputStream
	private DataOutputStream pushDataOut;//Push DataOutputStream
	
	private long pushSocketCallTime; //Push 소켓 생성/사용 시간 
	private String queueAddChk;	//사용한 소켓정보 클래스를 큐에 저장할지 판단 값(ADD: 큐에 저장   NO_ADD: 큐에 미저장)

	private String channelID;			//Push Header channelID
	private int channelNum;			//Push Header channelNum

	public PushSocketVO(Socket pushSocket) {
		this.pushSocket = pushSocket;
		this.pushSocketCallTime = System.currentTimeMillis()/1000;
		this.queueAddChk = "ADD";
	}

}
