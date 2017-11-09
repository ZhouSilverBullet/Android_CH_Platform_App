package com.chsdk.utils;

import android.text.TextUtils;
import android.util.Log;

import com.caohua.games.BuildConfig;

/**
 * @author ZengLei
 *         <p>
 * @version 2016年8月15日
 *          <p>
 */
public class LogUtil {
	public static final boolean LOG_SWITCH = BuildConfig.DEBUG;
	private static final String LOG_TAG = "chapp_log_tag";
	private static final String LOG_MSG_PREFIX = "chapp_";

	public static void debugLog(String prefix, String logMsg) {
		if (TextUtils.isEmpty(logMsg))
			return;

		if (LOG_SWITCH)
			Log.d(LOG_TAG, LOG_MSG_PREFIX + (TextUtils.isEmpty(prefix) ? "" : prefix + "_") + logMsg);
	}

	public static void debugLog(String logMsg) {
		debugLog("", logMsg);
	}

	public static void errorLog(String prefix, String logMsg) {
		if (TextUtils.isEmpty(logMsg))
			return;

		if (LOG_SWITCH)
			Log.e(LOG_TAG, LOG_MSG_PREFIX + (TextUtils.isEmpty(prefix) ? "" : prefix + "_") + logMsg);
	}

	public static void errorLog(String logMsg) {
		errorLog("", logMsg);
	}
}
