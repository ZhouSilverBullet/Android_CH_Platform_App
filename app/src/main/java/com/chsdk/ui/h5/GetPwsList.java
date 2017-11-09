package com.chsdk.ui.h5;

import android.app.Activity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

/** 
* @author  ZengLei <p>
* @version 2016年9月9日 <p>
*/
public class GetPwsList extends BaseH5 {

	public GetPwsList(Activity activity, WebView webView) {
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

}
