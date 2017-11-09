package com.chsdk.biz.login;

import android.text.TextUtils;

import com.chsdk.http.HttpConsts;
import com.chsdk.http.RequestExe;
import com.chsdk.utils.CHAsyncTask;
import com.chsdk.utils.JsonUtil;
import com.chsdk.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/** 
* @author  ZengLei <p>
* @version 2016年8月18日 <p>
*/
public class AutoCreateAccountTask extends CHAsyncTask<Void, Void, String> {
	private static final String TAG = AutoCreateAccountTask.class.getSimpleName();
	private TaskListener listener;
	
	public AutoCreateAccountTask(TaskListener listener) {
		this.listener = listener;
	}
	
	@Override
	protected void onPreExecute() {
		
	}

	@Override
	protected String doInBackground(Void... params) {
		LogUtil.debugLog(TAG, "doInBackground");
		
		RegisterLogic logic = new RegisterLogic();
		String result = null;
		try {
			result = logic.fastRegister();

			if (TextUtils.isEmpty(result)) {
				result = logic.fastRegister();
			}
		} catch (Exception e) {
			String msg = RequestExe.getOkHttpExceptionMsg(e, null);
			LogUtil.errorLog(TAG, "HttpException_" + msg);
		}
		
		String userName = null;
		
		if (!TextUtils.isEmpty(result)) {
			try {
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject != null) {
					int code = JsonUtil.getStatusCode(jsonObject);
					if (code == HttpConsts.CODE_SUCCESS) {
						userName = JsonUtil.getUserName((JSONObject) JsonUtil.getJsonData(jsonObject));
					} else {
						LogUtil.errorLog(TAG, "error_msg:" + JsonUtil.getServerMsg(jsonObject));
					}
				}
			} catch (JSONException e) {
				LogUtil.errorLog(TAG, "postSync: JSONException_" + e.getMessage() + ", result_" + result);
			}
		}
		
		LogUtil.debugLog("AutoCreateAccountTask: userid_" + userName);
		return userName;
	}

	
	@Override
	protected void onPostExecute(String result) {
		if (listener!= null) {
			listener.finished(result);
		}
	}
	
	public static interface TaskListener {
		void finished(String msg);
	}
}
