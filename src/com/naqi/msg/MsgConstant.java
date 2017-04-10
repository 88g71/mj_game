package com.naqi.msg;

public interface MsgConstant {

	public static final int RECEIVE_HEAD_LEN = 3;
	public static final byte SIGN = 127;
	public static final int SHORT_DATA_LEN = 2;
	public static final int INT_DATA_LEN = 4;
	public static final int MASK_KEY_LEN = 4;
	
	public static final String MSG_KEY_SESSION = "session";
	public static final String MSG_KEY_CMD = "cmd";
	public static final String MSG_KEY_DATA = "param";
	
	public static final String SESSION_KEY_USER = "user";
	public static final String SESSION_KEY_GAME_TABLE = "game_table";
}
