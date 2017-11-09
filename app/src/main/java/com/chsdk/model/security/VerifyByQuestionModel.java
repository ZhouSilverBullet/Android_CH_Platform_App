package com.chsdk.model.security;

import com.chsdk.model.BaseModel;

/** 
* @author  ZengLei <p>
* @version 2016年9月6日 <p>
*/
public class VerifyByQuestionModel extends BaseModel {

	private String userName;
	private String answer;
	private String passwd;
	
	public VerifyByQuestionModel(String userName, String answer, String passwd) {
		this.userName = userName;
		this.answer = answer;
		this.passwd = passwd;
	}
	
	public String getUserName() {
		return userName;
	}
	
	@Override
	public void putDataInMap() {
		put(PARAMS_USER_NAME, userName);
		put(PARAMS_QUESTION_ANSWER, answer);
		put(PARAMS_PASSWORD, passwd);
	}
}
