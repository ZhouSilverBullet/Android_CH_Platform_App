package com.chsdk.http;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.caohua.games.app.AppContext;
import com.chsdk.configure.SdkSession;
import com.chsdk.model.BaseModel;
import com.chsdk.utils.CryptionUtil;
import com.chsdk.utils.LogUtil;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author ZengLei
 *         <p>
 * @version 2016年8月15日
 *          <p>
 */
public class RequestExe {
	private static final String TAG = RequestExe.class.getSimpleName();
	public static final int HTTP_EXPIRTY_TIME_S = 6 * 1000;
	private static OkHttpClient client ;
	private static Handler mainThreadHandler;
	private static OkHttpClient.Builder builder;
	private static OkHttpClient.Builder builderInit;

	public static OkHttpClient getSingleClient() {
		if (client == null) {
			synchronized (RequestExe.class) {
				if (client == null) {
					client = new OkHttpClient();
				}
			}
		}
		return client;
	}

	public static OkHttpClient getClient(int timeOut) {
		File cacheDir = AppContext.getAppContext().getCacheDir();  //缓存目录
		int cacheSize = 10 * 1024 * 1024;  // 做缓存
		if (builder == null) {
			builder = getSingleClient().newBuilder()
					.cache(new Cache(cacheDir, cacheSize))
					.readTimeout(timeOut, TimeUnit.MILLISECONDS)
					.writeTimeout(timeOut, TimeUnit.MILLISECONDS);
		}
		builder.connectTimeout(timeOut, TimeUnit.MILLISECONDS); //设置自控超时
		return builder.build();
	}

	public static OkHttpClient getClient() {
		File cacheDir = AppContext.getAppContext().getCacheDir();  //缓存目录
		int cacheSize = 10 * 1024 * 1024;  // 做缓存
		if (builderInit == null) {
			builderInit = getSingleClient().newBuilder()
					.connectTimeout(HTTP_EXPIRTY_TIME_S, TimeUnit.MILLISECONDS) //6s超时
					.cache(new Cache(cacheDir, cacheSize))
					.readTimeout(HTTP_EXPIRTY_TIME_S, TimeUnit.MILLISECONDS)
					.writeTimeout(HTTP_EXPIRTY_TIME_S, TimeUnit.MILLISECONDS);
		}
		return builderInit.build();
	}

	public static Handler getOkMainThreadHandler() {
		if (mainThreadHandler == null) {
			synchronized (RequestExe.class) {
				if (mainThreadHandler == null) {
					mainThreadHandler = new Handler(Looper.getMainLooper());
				}
			}
		}
		return mainThreadHandler;
	}

	public static void upload(String url, File file, BaseModel model, final String key, final IRequestListener listener) {
		RequestBody requestBody = getRequestBody(file, model.getDataMap(), key);
		Request request = new Request.Builder().post(requestBody).url(url).build();
		getClient().newCall(request).enqueue(new OkHttpCallBack(listener));
	}

    private static RequestBody getRequestBody(File file, Map<String, String> data, String key) {
		String bodyParameter = getBodyParameter(data);
		RequestBody fileBody = RequestBody.create(MediaType.parse("image/png") , file);
		RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
				.addFormDataPart(HttpConsts.POST_PARAMS_DATA, bodyParameter)
                .addFormDataPart(key, "account.jpg", fileBody).build();
        return requestBody;
    }

	public static void post(String url, BaseModel model, IRequestListener listener) {
		LogUtil.debugLog(TAG, "post: url_" + url);

		String bodyParameter = getBodyParameter(model.getDataMap());
		RequestBody requestBody = new FormBody.Builder().add(HttpConsts.POST_PARAMS_DATA, bodyParameter).build();
		Request request = new Request.Builder().post(requestBody).url(url).build();
		getClient().newCall(request).enqueue(new OkHttpCallBack(listener));
	}

	public static void post(String url, BaseModel model, IRequest2Listener listener) {
		LogUtil.debugLog(TAG, "post: url_" + url);

		String bodyParameter = getBodyParameter(model.getDataMap());
		RequestBody requestBody = new FormBody.Builder().add(HttpConsts.POST_PARAMS_DATA, bodyParameter).build();
		Request request = new Request.Builder().post(requestBody).url(url).build();
		getClient().newCall(request).enqueue(new OkHttpCallBack2(listener));
	}

	public static void postToData(String url, String data, IRequestListener listener) {
		LogUtil.debugLog(TAG, "post: url_" + url);

		RequestBody requestBody = new FormBody.Builder().add(HttpConsts.POST_PARAMS_DATA, data).build();
		Request request = new Request.Builder().post(requestBody).url(url).build();
		getClient().newCall(request).enqueue(new OkHttpCallBack(listener));
	}

	public static void localHtmlPost(String url, Map<String, String> map, IRequestListener listener) {
		LogUtil.debugLog(TAG, "localHtmlPost: url_" + url);

		String bodyParameter = getBodyParameter(map);
		RequestBody requestBody = new FormBody.Builder().add(HttpConsts.POST_PARAMS_DATA, bodyParameter).build();
		Request request = new Request.Builder().post(requestBody).url(url).build();
		getClient().newCall(request).enqueue(new OkLocalHtmlCallBack(listener));
	}

	public static String postSync(String url, Map<String, String> map)  {
		LogUtil.debugLog(TAG, "postSync: url_" + url);

		String bodyParameter = getBodyParameter(map);
		RequestBody requestBody = new FormBody.Builder().add(HttpConsts.POST_PARAMS_DATA, bodyParameter).build();
		Request request = new Request.Builder().url(url).post(requestBody).build();
		try {
			Response response = getClient().newCall(request).execute();
			String jsonMsg = response.body().string();

			LogUtil.debugLog(TAG, "postSync: result_" + (TextUtils.isEmpty(jsonMsg) ? "" : jsonMsg.replace("\n", "")));
			return jsonMsg;
		} catch (IOException e) {
			LogUtil.errorLog(TAG, "postSync: url_" + url + ", IOException_" + e.getMessage());
			return "";
		}
	}

	public static String postSync(int timeOut, String url, Map<String, String> map)  {
		LogUtil.debugLog(TAG, "postSync: url_" + url);

		String bodyParameter = getBodyParameter(map);
		RequestBody requestBody = new FormBody.Builder().add(HttpConsts.POST_PARAMS_DATA, bodyParameter).build();
		Request request = new Request.Builder().url(url).post(requestBody).build();
		try {

			Response response = getClient(timeOut).newCall(request).execute();
			String jsonMsg = response.body().string();

			LogUtil.debugLog(TAG, "postSync: result_" + (TextUtils.isEmpty(jsonMsg) ? "" : jsonMsg.replace("\n", "")));
			return jsonMsg;
		} catch (IOException e) {
			LogUtil.errorLog(TAG, "postSync: url_" + url + ", IOException_" + e.getMessage());
			return "";
		}
	}

    /**
     * 多图上传
     * @param url
     * @param stringList
     * @param listener
     */
	public static void postMultiPic(String url, List<String> stringList,BaseModel model, IRequestListener listener) {
		MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
		for (String s : stringList) {
			File f = new File(s);
            if (f != null && f.length() > 0) {
                builder.addFormDataPart("img", f.getName(), RequestBody.create(MediaType.parse("image/png"), f));
            }
		}
		builder.addFormDataPart(HttpConsts.POST_PARAMS_DATA, getBodyParameter(model.getDataMap()));
        MultipartBody requestBody = builder.build();
        Request request = new Request.Builder().url(url).post(requestBody).build();
        getClient().newCall(request).enqueue(new OkHttpCallBack(listener));
    }

	// 1. map ({(a,1),(b,2),(c,3)}) 转成字符串 str (a=1&b=2&c=3)
	// 2. str = str + appkey
	// 3. 计算str的md5
	// 4. 将 md5存入map: map.put("sn", md5)
	// 5. map 转成 json
	// 6. 对json字符串进行base64加密
	public static String getBodyParameter(Map<String, String> map) {
		String strData = convertMapToString(map);

		LogUtil.debugLog(TAG, "post: params_" + strData);

		String signAppKey = signAppKey(strData);
		map.put("sn", signAppKey);

		JSONObject json = new JSONObject(map);
		String jsonStr = json.toString();

		return CryptionUtil.enBase64(jsonStr);
	}

	// 请求参数字符串拼接appkey后计算md5, 同时转换成大写
	private static String signAppKey(String data) {
		String str = data + SdkSession.getInstance().getAppKey();
		str = CryptionUtil.encodeMd5(str);
		return str.toUpperCase(Locale.ENGLISH);
	}

	// map(按key排序) 转成字符串
	private static String convertMapToString(Map<String, String> map) {
		if (map == null || map.size() == 0)
			return "";

		StringBuilder sb = new StringBuilder();

		// key按a~z排序
		Object[] keyArr = map.keySet().toArray();
		Arrays.sort(keyArr);

		for (int i = 0; i < keyArr.length; i++) {
			if (i != 0) {
				sb.append(HttpConsts.STR_SPLICE_SYMBOL);
			}

			Object key = keyArr[i];
			sb.append(key).append(HttpConsts.STR_EQUAL_OPERATION).append(map.get(key));
		}

		return sb.toString();
	}

	public static String getOkHttpExceptionMsg(Exception exception, String errorMsg) {
		String defaultMsg = "未知错误";
		if (exception != null) {
			LogUtil.errorLog(TAG, "Request Exception:" + exception.getMessage());
			if (exception instanceof UnknownHostException) {
				defaultMsg = "您的网络可能有问题,请确认连接上有效网络后重试(101)";
			} else if (exception instanceof ConnectTimeoutException) {
				defaultMsg = "连接超时,您的网络可能有问题,请确认连接上有效网络后重试(102)";
			} else if (exception instanceof SocketTimeoutException) {
				defaultMsg = "请求超时,您的网络可能有问题,请确认连接上有效网络后重试(103)";
			} else {
				defaultMsg = "未知的网络错误, 请重试(105)";
			}
		} else {
			if (!TextUtils.isEmpty(errorMsg)) {
				LogUtil.errorLog(TAG, "Request Exception errorMsg: " + errorMsg);
				String lowerMsg = errorMsg.toLowerCase(Locale.ENGLISH);
				if (lowerMsg.contains("java")
						|| lowerMsg.contains("exception")
						|| lowerMsg.contains(".net")
						|| lowerMsg.contains("java")) {
					defaultMsg = "未知错误, 请重试(107)";
				} else {
					defaultMsg = "未知错误, 请重试(108)";
				}
			}
		}
		return defaultMsg;
	}
}
