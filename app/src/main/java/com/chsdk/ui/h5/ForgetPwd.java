package com.chsdk.ui.h5;

import org.json.JSONObject;

import com.chsdk.biz.security.GateWayLogic;
import com.chsdk.utils.LogUtil;
import com.chsdk.utils.NetworkUtils;

import android.app.Activity;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

/** 
* @author  ZengLei <p>
* @version 2016年9月6日 <p>
*/
public class ForgetPwd extends BaseH5 {
	private static final String TAG = ForgetPwd.class.getSimpleName();

	public ForgetPwd(Activity activity, WebView webView) {
		super(activity, webView);
	}
	
	@JavascriptInterface
	public void setStorage(String json) {
		super.setStorage(json);
	}
	
	@JavascriptInterface
	public String getStorage(String key) {
		return super.getStorage(key);
	}
	
	@JavascriptInterface
	public void gateWay(String json) {
		LogUtil.debugLog(TAG, "handleWebSubmit_" + json);
		String userName = getUserName(json);
		getAccountInfo(userName);
	}
	
	private String getUserName(String json) {
		if (TextUtils.isEmpty(json))
			return null;
		
		JSONObject object = getJsonObject(json);
		if (object != null) {
			return getJSONObjectValue(object, "username");
		}
		return null;
	}
	
	private void getAccountInfo(String userName) {
		if (TextUtils.isEmpty(userName))
			return;
		
		if (!NetworkUtils.isNetworkConnected(context)) {
			handleNetworkError();
			return;
		}
		
		GateWayLogic logic = new GateWayLogic(userName, new Listener());
		logic.post();
	}
}
