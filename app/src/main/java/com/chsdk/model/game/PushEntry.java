package com.chsdk.model.game;

/** 
* @author  ZengLei <p>
* @version 2016年9月13日 <p>
*/
public class PushEntry {
	public static final String TYPE_TEXT = "1";
	public static final String TYPE_PIC = "2";
	
	public static final String SHOW_TYPE_CLICK_ONCE = "1";
	public static final String SHOW_TYPE_CLICK_ONCE_DAY = "2";
	public static final String SHOW_TYPE_ALWAYS = "3";
	
	public String msgId;
	public String imgUrl;
	public String jumpUrl;
	public int width;
	public int heigth;
	public String expireTime;
	public String type;
	public String showType;
	public int isAward;  // 是否有优惠劵
}
