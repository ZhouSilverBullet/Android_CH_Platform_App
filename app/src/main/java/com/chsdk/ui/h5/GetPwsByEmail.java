package com.chsdk.ui.h5;

import org.json.JSONObject;

import com.chsdk.biz.security.MBEmailLogic;
import com.chsdk.utils.NetworkUtils;

import android.app.Activity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

/** 
* @author  ZengLei <p>
* @version 2016年9月6日 <p>
*/
public class GetPwsByEmail extends BaseH5 {

	public GetPwsByEmail(Activity activity, WebView webView) {
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
	public void mbEmailCode(String json) {
		if (!NetworkUtils.isNetworkConnected(context)) {
			handleNetworkError();
			return;
		}
		
		JSONObject object = getJsonObject(json);
		if (object != null) {
			String userName = getJSONObjectValue(object, "username");
			String email = getJSONObjectValue(object, "email");
			MBEmailLogic logic = new MBEmailLogic(userName, email);
			logic.sendEmail();
		}
	}
}
