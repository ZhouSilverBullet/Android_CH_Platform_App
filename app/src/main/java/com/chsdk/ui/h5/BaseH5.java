package com.chsdk.ui.h5;

import java.lang.reflect.Constructor;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.chsdk.biz.BaseLogic.LogicListener;
import com.chsdk.utils.LogUtil;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

/**
 * @author ZengLei
 *         <p>
 * @version 2016年9月6日
 *          <p>
 */
public abstract class BaseH5 {
	protected WebView webView;
	protected Activity activity;
	protected Context context;

	public BaseH5(Activity activity, WebView webView) {
		this.activity = activity;
		this.context = activity.getApplicationContext();
		this.webView = webView;
	}

	protected void handleResult(String json) {
		LogUtil.debugLog("BaseH5_handleResult_" + json);
		webView.loadUrl("javascript:handleAndroidResult('" + json + "')");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static BaseH5 createInstance(String fileName, Activity activity, WebView webView) {
		if (TextUtils.isEmpty(fileName))
			return null;
		
		try {
			Class clazz = Class.forName("com.chsdk.ui.h5." + fileName);
			Constructor constructor = clazz.getConstructor(Activity.class, WebView.class);
			return (BaseH5) constructor.newInstance(activity, webView);
		} catch (Exception e) {
			LogUtil.errorLog("BaseH5 createInstance:" + e.getMessage());
		}
		return null;
	}

	public String getErrorJson(String errorMsg) {
		return "{\"code\": 999,\"msg\": \"" + errorMsg + "\",\"data\":{}}";
	}

	protected String getSuccessJson() {
		return "{\"code\": 200,\"msg\": \"\u6210\u529f\",\"data\":{}}";
	}
	
	protected void handleNetworkError() {
		webView.post(new Runnable() {
			@Override
			public void run() {
				handleResult(getErrorJson("请检查您当前的网络"));				
			}
		});
	}
	
	public void setStorage(String json) {
		JSONObject object = getJsonObject(json);
		if (object == null) 
			return;
		
		Iterator<String> iterator = object.keys();
		if (!iterator.hasNext())
			return;
		
		while (iterator.hasNext()) {
			String key = iterator.next();
			String value = getJSONObjectValue(object, key);
			Cookie.set(key, value);
		}
	}
	
	public String getStorage(String key) {
		return Cookie.get(key);
	}

	protected void finishActivity() {
		activity.finish();
	}

	protected String getJSONObjectValue(JSONObject object, String key) {
		if (object == null || TextUtils.isEmpty(key))
			return null;

		return object.optString(key);
	}

	protected JSONObject getJsonObject(String json) {
		try {
			JSONObject object = new JSONObject(json);
			return object;
		} catch (JSONException e) {
			return null;
		}
	}

	class Listener implements LogicListener {
		@Override
		public void failed(String errorMsg) {
			handleResult(getErrorJson(errorMsg));
		}

		@Override
		public void success(String... result) {
			handleResult(result[0]);
		}
	}
}
