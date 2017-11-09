package com.chsdk.model.login;

import com.chsdk.model.BaseModel;

/**
 * @author ZengLei <p>  
 */
public class LogoutAdModel extends BaseModel {
	private String userId;
	private String token;
	
	public LogoutAdModel() {
		userId = session.getUserId();
		token = session.getToken();
	}
	
	@Override
	public void putDataInMap() {
		put(PARAMS_TOKEN, token);
		put(PARAMS_USER_ID, userId);
		put(PARAMS_SERVER_ID, "1");
	}
}
