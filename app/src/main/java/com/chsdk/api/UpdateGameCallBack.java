package com.chsdk.api;

/** 
* @author  ZengLei <p>
* @version 2016年9月29日 <p>
*/

/**
 * 更新游戏的回调
 */
public interface UpdateGameCallBack {
	/**
	 *  不选择更新、更新过程中取消下载，或者下载完成后的回调
	 */
	public void dismiss();
}