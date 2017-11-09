package com.chsdk.model;

/** 
* @author  ZengLei <p>
* @version 2016年8月31日 <p>
*/
public class DeviceActiveModel extends BaseModel {

	private String deviceNo;

	public DeviceActiveModel(String deviceNo) {
		this.deviceNo = deviceNo;
	}
	
	@Override
	public void putDataInMap() {
		put(PARAMS_DEVICE_NO, deviceNo);
		put(PARAMS_SOURCE_ID, "576");
	}

}
