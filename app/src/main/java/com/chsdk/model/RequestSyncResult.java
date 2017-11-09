package com.chsdk.model;

/** 
* @author  ZengLei <p>
* @version 2016年8月17日 <p>
* 
*  同步请求的结果类
*/
public class RequestSyncResult {
	public boolean postStatus;
	public String msg;
	public Object obj;
	
	@Override
	public String toString() {
		return " RequestSyncResult: postStatus_" + postStatus + ", msg_" + msg + ", obj_" + obj;
	}
}
