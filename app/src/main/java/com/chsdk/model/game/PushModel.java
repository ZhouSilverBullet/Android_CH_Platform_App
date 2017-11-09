package com.chsdk.model.game;

import com.chsdk.model.BaseModel;

/** 
* @author  ZengLei <p>
* @version 2016年9月8日 <p>
*/
public class PushModel extends BaseModel {

	private final boolean login;
	private String token;
	private String userId;
	private String msgId;

	public PushModel(String msg, boolean login) {
		token = session.getToken();
		userId = session.getUserId();
		msgId = msg;
		this.login = login;
	}
	
	@Override
	public void putDataInMap() {
		if (login) {
			put(PARAMS_USER_ID,userId);
			put(PARAMS_SERVER_ID, "1");
			put(PARAMS_TOKEN, token);
			put(PARAMS_CHANNEL_USER_ID, "639");
		}
		put(PARAMS_MSG_ID, msgId);
	}
}
