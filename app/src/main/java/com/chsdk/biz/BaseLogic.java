package com.chsdk.biz;

import android.text.TextUtils;

import com.caohua.games.BuildConfig;
import com.chsdk.configure.SdkSession;
import com.chsdk.utils.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ZengLei
 *         <p>
 * @version 2016年8月15日
 *          <p>
 */
public abstract class BaseLogic {
//	public static final String HOST_PASSPORT = "http://passport.sdk.caohua.com/";
//	public static final String HOST_PAY = "http://pay.sdk.caohua.com/";
//	public static final String HOST_APP = "http://app.sdk.caohua.com/";
    public static final boolean LOGIC_SWITCH = BuildConfig.DEBUG;
	public static final String HOST_PASSPORT = LOGIC_SWITCH ? "http://passport.sdk.caohua.com/" : "https://passport-sdk.caohua.com/";
    public static final String HOST_PAY = LOGIC_SWITCH ? "http://pay.sdk.caohua.com/" : "https://pay-sdk.caohua.com/";
    public static final String HOST_APP = LOGIC_SWITCH ? "http://app.sdk.caohua.com/" : "https://app-sdk.caohua.com/";
	public static final String HOSTP_APP_API = "https://api-sdk.caohua.com/";

	private static final String TOKEN_INVALID = "[206]";

	public static interface LogicListener {
		void failed(String errorMsg);
		void success(String...result);
	}

	public static interface CommentLogicListener {
		void failed(String errorMsg, int errorCode);
		void success(String...result);
	}

	public static interface AppLogicListner {
		void failed(String errorMsg);
		void success(Object entryResult);
	}

	public static interface DataLogicListner<T> {
		void failed(String errorMsg, int errorCode);
		void success(T entryResult);
	}

	public static boolean isTokenInvialid(String msg) {
		if (TextUtils.isEmpty(msg))
			return false;

		return msg.startsWith(TOKEN_INVALID);
	}

	protected <T> List<T> fromJsonArray(String json, Class<T> clazz) {
		return JsonUtil.fromJsonArray(json, clazz);
	}

	public String getOpenUID() {
		return SdkSession.getInstance().getUserId();
	}
}
