package com.wMember.common;

public class Constant {
	
	/* const */
	public static final String YYYYMMDD = "(19|20)\\d{2}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])";
	public static final String YYYYMMDDHH = "(19|20)\\d{2}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])(0[0-9]|1[0-9]|2[0-3])";
	public static final String YYYYMMDDHHMI = "(19|20)\\d{2}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])(0[0-9]|1[0-9]|2[0-3])([0-5][0-9])";
	public static final String YYYYMMDDHHMISS = "(19|20)\\d{2}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])(0[0-9]|1[0-9]|2[0-3])([0-5][0-9])([0-5][0-9])";
	public static final String HH= "(0[0-9]|1[0-9]|2[0-3])";
	public static final String MMDD = "(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])";
	public static final String HHMI = "(0[0-9]|1[0-9]|2[0-3])([0-5][0-9])";	
	
	/* common */
	public static final String Y = "Y";
	public static final String N = "N";	
	
	public static final String CODE_SUCCESS = "0000";
	public static final String CODE_SERVER_ERROR = "9000";
	public static final String CODE_REPOSITORY_ERROR = "9001";
	public static final String CODE_INSERT_EMPTY_ERROR = "9001";
	public static final String CODE_UNKNOWN_ERROR = "9444";
	
	/* member */
	public static final String TOKEN = "SESSION_TOKEN";	
	public static final String SIGN = "WMEMBER";
	public static final String PASSWORD_FORMAT = "%0128x";
	public static final String JWT_SUBJECT = "wmember";
	
	public static final String CODE_DIFF_PASSWORD = "3001";
	public static final String CODE_NO_USER = "3002";
	public static final String CODE_LOING_CHECK_ERROR = "3003";
	
	/* assets */
	public static final String CODE_VALIDATION_ACCOUNT = "4001";
}
