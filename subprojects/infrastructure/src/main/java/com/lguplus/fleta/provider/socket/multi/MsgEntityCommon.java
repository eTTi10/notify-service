package com.lguplus.fleta.provider.socket.multi;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MsgEntityCommon {

    public static final int PUSH_MSG_HEADER_LEN = 64;
    public static final String SUCCESS = "SC";
    public static final String FAIL = "FA";
    public static final int CHANNEL_CONNECTION_REQUEST = 1;
    public static final int CHANNEL_CONNECTION_REQUEST_ACK = 2;
    public static final int CHANNEL_RELEASE_REQUEST = 5;
    public static final int CHANNEL_RELEASE_REQUEST_ACK = 6;
    public static final int PROCESS_STATE_REQUEST = 13;
    public static final int PROCESS_STATE_REQUEST_ACK = 14;
    public static final int COMMAND_REQUEST = 15;
    public static final int COMMAND_REQUEST_ACK = 16;
    public static final String PUSH_ENCODING = "euc-kr";

    public static boolean isValidMessageType(int type) {
        switch(type) {
            case CHANNEL_CONNECTION_REQUEST :
            case CHANNEL_CONNECTION_REQUEST_ACK :
            case CHANNEL_RELEASE_REQUEST :
            case CHANNEL_RELEASE_REQUEST_ACK :
            case PROCESS_STATE_REQUEST :
            case PROCESS_STATE_REQUEST_ACK :
            case COMMAND_REQUEST :
            case COMMAND_REQUEST_ACK :
                return true;
            default:
                break;
        }

        return false;
    }

}
