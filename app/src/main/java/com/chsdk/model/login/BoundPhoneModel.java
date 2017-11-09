package com.chsdk.model.login;

import com.chsdk.model.BaseModel;

/** 
* @author  ZengLei <p>
* @version 2016年9月1日 <p>
*/
public class BoundPhoneModel extends BaseModel {

	private String userId;
	private String code;
	private String token;
	public BoundPhoneModel(String code) {
		userId = session.getUserId();
		this.code = code;
		token = session.getToken();
	}
	
	@Override
	public void putDataInMap() {
		put(PARAMS_TOKEN, token);
		put(PARAMS_USER_ID, userId);
		put(PARAMS_AUTH_CODE, code);
	}
}
