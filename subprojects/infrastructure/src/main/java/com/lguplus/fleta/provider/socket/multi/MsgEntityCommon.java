package com.lguplus.fleta.provider.socket.multi;

public class MsgEntityCommon {

    public final static int HEADER_SIZE = 64;

    public final static String SUCCESS = "SC";
    public final static String FAIL = "FA";

    public final static int CHANNEL_CONNECTION_REQUEST = 1;
    public final static int CHANNEL_CONNECTION_REQUEST_ACK = 2;
    public final static int CHANNEL_RELEASE_REQUEST = 5;
    public final static int CHANNEL_RELEASE_REQUEST_ACK = 6;
    public final static int PROCESS_STATE_REQUEST = 13;
    public final static int PROCESS_STATE_REQUEST_ACK = 14;
    public final static int COMMAND_REQUEST = 15;
    public final static int COMMAND_REQUEST_ACK = 16;

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
