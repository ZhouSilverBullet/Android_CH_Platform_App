package com.chsdk.model.login;

import com.chsdk.model.BaseModel;

/** 
* @author  ZengLei <p>
* @version 2016年9月1日 <p>
*/
public class BoundPhoneCodeModel extends BaseModel{
	private String userId;
	private String phone;
	private String token;
	
	public BoundPhoneCodeModel(String phoneNum) {
		this.phone = phoneNum;
		userId = session.getUserId();
		token = session.getToken();
	}

	@Override
	public void putDataInMap() {
		put(PARAMS_USER_ID, userId);
		put(PARAMS_TOKEN, token);
		put(PARAMS_BIND_PHONE, phone);
	}
	
}
