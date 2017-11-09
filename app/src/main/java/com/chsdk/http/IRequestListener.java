package com.chsdk.http;

import java.util.HashMap;

/**
 * @author ZengLei
 *         <p>
 * @version 2016年8月15日
 *          <p>
 */
public interface IRequestListener {
	void success(HashMap<String, String> map);

	void failed(String error, int errorCode);
}
