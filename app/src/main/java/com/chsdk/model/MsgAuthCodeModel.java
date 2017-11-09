package com.chsdk.model;

/** 
* @author  ZengLei <p>
* @version 2016年8月20日 <p>
*/
public class MsgAuthCodeModel extends BaseModel{
	private String platformId;
	private String userName; //用户账号
	private String deviceNo;

	public MsgAuthCodeModel() {
		platformId = String.valueOf(session.getPlatformId());
		deviceNo = session.getDeviceNo();
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	@Override
	public void putDataInMap() {
		put(PARAMS_PLATFORM_ID, platformId);
		put(PARAMS_CHANNEL_USER_ID, "639");
		put(PARAMS_USER_NAME, userName);
		put(PARAMS_DEVICE_NO, deviceNo);
		put(PARAMS_SOURCE_ID, "576");
	}
}
