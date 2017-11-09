package com.chsdk.model.login;

/** 
* @author  ZengLei <p>
* @version 2016年8月17日 <p>
*/
public class SourceInfo {
	public int userId; // 渠道ID, 不确定是否需要
	public int sourceId; //子渠道ID, 需要上传接口
	public float version; //游戏版本号, 用于上传给接口判断是否需要强更
}
