package com.chsdk.api;

/** 
* @author  ZengLei <p>
* @version 2016年9月20日 <p>
*/

/**
 * 登录结果的回调
 */
public interface LoginCallBack {
	/**
	 * @param msg 错误信息
	 */
	void failed(String msg);
	/**
	 * @param userName 用户名
	 * @param userId 用户ID
	 * @param token token
	 */
	void success(String userName, String userId, String token);
	/**
	 * 登录过程退出游戏
	 */
	void exit();
}
