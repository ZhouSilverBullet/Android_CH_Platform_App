package com.chsdk.api;

/** 
* @author  ZengLei <p>
* @version 2016年9月20日 <p>
*/

/**
 * Splash展示结束后的回调
 */
public interface SplashDismissListener {
	/**
	 * @param needUpdate 是否需要更新游戏.若需要更新,则在Splash dismiss后弹出更新对话框
	 */
	void dismiss(boolean needUpdateGame);
}
