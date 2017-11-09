package com.chsdk.utils;

import android.text.TextUtils;

/**
 * @author ZengLei
 *         <p>
 * @version 2016年8月19日
 *          <p>
 */
public class VerifyFormatUtil {
	private static final String REGEX_PHONE_NUM = "[1][34578]\\d{9}";

	public static boolean isPhoneNum(String phone) {
		if (TextUtils.isEmpty(phone))
			return false;

		return phone.matches(REGEX_PHONE_NUM);
	}
/**
 * 输入用户名的限制 
 * @param userName
 * @return
 */
	public static boolean verifyUserName(String userName) {
		if (TextUtils.isEmpty(userName))
			return false;

		if (userName.length() >= 5 && userName.length() <= 12) {
			return true;
		}
		return false;
	}

	public static boolean verifyPassWord(String passWord) {
		if (TextUtils.isEmpty(passWord))
			return false;

		if (passWord.length() >= 6 && passWord.length() <= 16) {
			return true;
		}
		return false;
	}
}
