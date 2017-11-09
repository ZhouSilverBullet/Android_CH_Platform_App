package com.chsdk.model;

/** 
* @author  ZengLei <p>
* @version 2016年9月6日 <p>
*/
public class H5UpdateModel extends BaseModel {

	private String h5Name;
	private String h5Hash;
	
	public H5UpdateModel(String h5Name, String h5Hash) {
		this.h5Name = h5Name;
		this.h5Hash = h5Hash;
	}
	
	@Override
	public void putDataInMap() {
		put(PARAMS_H5_FILE, h5Name);
		put(PARAMS_H5_HASH, h5Hash);
	}
}
