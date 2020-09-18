package com.wMember.common;

public class Constant {
	
	public static final String TOKEN = "SESSION_TOKEN";	
	public static final String SIGN = "WMEMBER";
	public static final String PASSWORD_FORMAT = "%0128x";
	public static final String JWT_SUBJECT = "wmember";
	
	
	public static final String UTF_8 = "UTF-8";
	public static final String SHA_512 = "SHA-512";
	public static final String UNDER_BAR = "_";
	public static final String SLASH = "/";
	
	public static final String IS_LOGIN = "isLogin";	
	public static final String USER_ID = "userId";
	public static final String USER_NO = "userNo";
	public static final String PASSWORD = "password";
	public static final String REQ_PASSWORD = "reqPassword";
	public static final String SALT = "salt";
	public static final String JWT = "jwt";
	
	public static final String RESULT_CODE = "resultCode";
	public static final String RESULT_CODE_SUCCESS = "0100";
	public static final String RESULT_CODE_DIFF_PASSWORD = "0100";
	public static final String RESULT_CODE_NO_USER = "0101";
	public static final String RESULT_CODE_SERVER_ERROR = "9000";
}
