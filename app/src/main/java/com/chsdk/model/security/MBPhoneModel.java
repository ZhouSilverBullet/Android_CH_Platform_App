package com.chsdk.model.security;

import com.chsdk.model.BaseModel;

/** 
* @author  ZengLei <p>
* @version 2016年9月6日 <p>
*/
public class MBPhoneModel extends BaseModel {

	private String userName;
	
	public MBPhoneModel(String userName) {
		this.userName = userName;
	}
	
	@Override
	public void putDataInMap() {
		put(PARAMS_USER_NAME, userName);
	}
}
