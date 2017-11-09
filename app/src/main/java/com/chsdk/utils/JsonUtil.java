package com.chsdk.utils;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.chsdk.http.HttpConsts;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * @author ZengLei
 *         <p>
 * @version 2016年8月15日
 *          <p>
 */
public class JsonUtil {
	public static int getStatusCode(JSONObject jsonObject) {
		return getInt(jsonObject, HttpConsts.RESULT_PARAMS_CODE);
	}
	
	public static String getServerMsg(JSONObject jsonObject) {
		return getString(jsonObject, HttpConsts.RESULT_PARAMS_MSG);
	}
	
	public static Object getJsonData(JSONObject jsonObject) {
		if (jsonObject == null)
			return null;
		
		Object o = jsonObject.opt(HttpConsts.RESULT_PARAMS_DATA);
		if (o instanceof JSONArray) {
			JSONArray arr = (JSONArray) o;
			if (arr.length() == 0) {
				return null;
			}
		}
		return jsonObject.opt(HttpConsts.RESULT_PARAMS_DATA);
	}
	
	public static String getCHB(JSONObject jsonObject) {
		if (jsonObject == null)
			return null;
		return getString(jsonObject, HttpConsts.RESULT_PARAMS_CHB);
	}
	
	public static String getSilver(JSONObject jsonObject) {
		if (jsonObject == null)
			return null;
		return getString(jsonObject, HttpConsts.RESULT_PARAMS_SILVER);
	}
	
	public static String getGameUpdateUrl(JSONObject jsonObject) {
		if (jsonObject == null)
			return null;
		
		return getString(jsonObject, HttpConsts.RESULT_PARAMS_GAME_UPDATE_URL);
	}
	
	public static int getServerVersionCode(JSONObject jsonObject) {
		if (jsonObject == null)
			return 0;
		
		return getInt(jsonObject, HttpConsts.RESULT_PARAMS_VERSION_CODE);
	}
	
	public static String getUserName(JSONObject jsonObject) {
		if (jsonObject == null)
			return null;
		
		return getString(jsonObject, HttpConsts.RESULT_PARAMS_USER_NAME);
	}
	
	public static String getUserid(JSONObject jsonObject) {
		if (jsonObject == null) {
			return null;
		}
		return getString(jsonObject, HttpConsts.RESULT_PARAMS_USER_ID);
	}
	
	public static HashMap<String, String> getMapFromJson(JSONObject jsonObject) {
		if (jsonObject == null) 
			return null;

		Iterator<String> iterator = jsonObject.keys();
		if (!iterator.hasNext())
			return null;
		
		HashMap<String, String> hashMap = new HashMap<String, String>();
		
		while (iterator.hasNext()) {
			String key = iterator.next();
			String value = getString(jsonObject, key);
			
			hashMap.put(key, value);
		}
		
		return hashMap;
	}
	
	public static String getString(JSONObject jsonObject, String key) {
		if (jsonObject == null) 
			return "";
		
		return jsonObject.optString(key, "");
	}

	public static int getInt(JSONObject jsonObject, String key) {
		if (jsonObject == null)
			return 0;
		
		return jsonObject.optInt(key);
	}

	public static <T> List<T> fromJsonArray(String json, Class<T> clazz) {
		if (TextUtils.isEmpty(json))
			return null;

		List<T> lst = new ArrayList<T>();

		JsonArray array = new JsonParser().parse(json).getAsJsonArray();
		for (final JsonElement elem : array) {
			lst.add(new Gson().fromJson(elem, clazz));
		}
		return lst;
	}
}
