package com.chsdk.model.login;

import com.chsdk.model.BaseModel;
import com.chsdk.utils.VerifyFormatUtil;

/** 
* @author  ZengLei <p>
* @version 2016年8月18日 <p>
*/
public class LogoutModel extends BaseModel{
	private String userId;
	private String userName;
	private String userType;
	private String token;
	
	public LogoutModel() {
		userName = session.getUserName();
		userId = session.getUserId();
		userType = String.valueOf(VerifyFormatUtil.isPhoneNum(userName) ? 2 : 1);
		token = session.getToken();
	}

	@Override
	public void putDataInMap() {
		put(PARAMS_USER_ID, userId);
		put(PARAMS_USER_NAME, userName);
		put(PARAMS_USER_TYPE, userType);
		put(PARAMS_TOKEN, token);
	}
	
}
