package com.chsdk.model.security;

import com.chsdk.model.BaseModel;

/** 
* @author  ZengLei <p>
* @version 2016年9月7日 <p>
*/
public class MBEmailModel extends BaseModel {
	private String userName;
	private String email;
	
	public MBEmailModel(String userName, String email) {
		this.userName = userName;
		this.email = email;
	}

	@Override
	public void putDataInMap() {
		put(PARAMS_USER_NAME, userName);
		put(PARAMS_EMAIL, email);
	}
}
