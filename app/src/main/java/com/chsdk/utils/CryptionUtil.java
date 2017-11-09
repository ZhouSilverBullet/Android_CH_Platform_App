package com.chsdk.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;

import android.text.TextUtils;
import android.util.Base64;

/**
 * @author ZengLei
 *         <p>
 * @version 2016年8月15日
 *          <p>
 *          加解密
 */
public class CryptionUtil {
	private static final String ALGORITHM = "RSA";
	private static final String SIGN_ALGORITHMS = "SHA1WithRSA";
	private static final String DEFAULT_CHARSET = "UTF-8";

	/**
	 * RSA
	 */
	public static String rsaSign(String content, String privateKey) {
		try {
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(deBase64(privateKey));
			KeyFactory keyf = KeyFactory.getInstance(ALGORITHM, "BC");
			PrivateKey priKey = keyf.generatePrivate(priPKCS8);

			Signature signature = Signature.getInstance(SIGN_ALGORITHMS);

			signature.initSign(priKey);
			signature.update(content.getBytes(DEFAULT_CHARSET));

			byte[] signed = signature.sign();

			return enBase64(signed);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/*
	 * base64
	 */
	public static String enBase64(String val) {
		if (TextUtils.isEmpty(val))
			return null;

		return new String(Base64.encode(val.getBytes(), Base64.NO_WRAP));
	}

	public static String enBase64(byte[] bytes) {
		if (bytes == null || bytes.length == 0)
			return null;

		return new String(Base64.encode(bytes, Base64.NO_WRAP));
	}

	public static byte[] deBase64(String val) {
		if (TextUtils.isEmpty(val))
			return null;

		return Base64.decode(val, Base64.NO_WRAP); // 默认不换行
	}

	public static String deBase64(byte[] bytes) {
		if (bytes == null || bytes.length == 0)
			return null;

		return new String(Base64.decode(bytes, Base64.NO_WRAP)); // 默认不换行
	}
	
	public static String deBase64String(String val) {
		if (TextUtils.isEmpty(val))
			return null;

		return new String(Base64.decode(val.getBytes(), Base64.NO_WRAP)); // 默认不换行行
	}

	/*
	 * md5
	 */
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
			'E', 'F' };

	public static String encodeMd5(String msg) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(msg.getBytes());
			byte messageDigest[] = digest.digest();
			return toHexString(messageDigest);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	private static String toHexString(byte[] b) {
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
			sb.append(HEX_DIGITS[b[i] & 0x0f]);
		}
		return sb.toString();
	}
	
	public static String encodeFileMd5(String filename) {
		InputStream fis;
		byte[] buffer = new byte[1024 * 4];
		int numRead = 0;
		MessageDigest md5;
		try {
			fis = new FileInputStream(filename);
			md5 = MessageDigest.getInstance("MD5");
			while ((numRead = fis.read(buffer)) > 0) {
				md5.update(buffer, 0, numRead);
			}
			fis.close();
			return toHexString(md5.digest());
		} catch (Exception e) {
			return null;
		}
	}
}
