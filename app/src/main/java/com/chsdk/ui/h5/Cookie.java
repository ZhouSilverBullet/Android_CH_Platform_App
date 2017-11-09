package com.chsdk.ui.h5;

import java.util.HashMap;

/** 
* @author  ZengLei <p>
* @version 2016年9月9日 <p>
*/
public class Cookie {
	private static HashMap<String, String> cookie;
	
	public static void set(String key, String value) {
		if (cookie == null) {
			cookie = new HashMap<String, String>();
		}
		cookie.put(key, value);
	}
	
	public static String get(String key) {
		if (cookie == null) 
			return null;
		
		return cookie.get(key);
	}
	
	public static void clear() {
		if (cookie != null) {
			cookie.clear();
			cookie = null;
		}
	}
}
