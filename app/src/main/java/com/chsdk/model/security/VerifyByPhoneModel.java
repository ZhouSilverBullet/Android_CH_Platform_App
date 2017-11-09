package com.chsdk.model.security;

import com.chsdk.model.BaseModel;

/** 
* @author  ZengLei <p>
* @version 2016年9月6日 <p>
*/
public class VerifyByPhoneModel extends BaseModel {

	private String userName;
	private String code;
	private String passwd;
	
	public VerifyByPhoneModel(String userName, String code, String passwd) {
		this.userName = userName;
		this.code = code;
		this.passwd = passwd;
	}
	
	public String getUserName() {
		return userName;
	}
	
	@Override
	public void putDataInMap() {
		put(PARAMS_USER_NAME, userName);
		put(PARAMS_AUTH_CODE, code);
		put(PARAMS_PASSWORD, passwd);
	}
}
