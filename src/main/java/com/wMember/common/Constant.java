package com.wMember.common;

public class Constant {
	
	public static final String TOKEN = "SESSION_TOKEN";	
	public static final String SIGN = "WMEMBER";
	public static final String PASSWORD_FORMAT = "%0128x";
	public static final String JWT_SUBJECT = "wmember";	

	public static final String UNDER_BAR = "_";
	public static final String SLASH = "/";	
	
	public static final String RESULT_CODE_SUCCESS = "0000";
	public static final String RESULT_CODE_DIFF_PASSWORD = "0100";
	public static final String RESULT_CODE_NO_USER = "0101";
	public static final String RESULT_CODE_SERVER_ERROR = "9000";
}
