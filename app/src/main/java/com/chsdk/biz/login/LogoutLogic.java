package com.chsdk.biz.login;

import java.util.HashMap;

import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.login.LogoutModel;
import com.chsdk.ui.widget.LoadingDialog;

import android.app.Activity;
import android.text.TextUtils;

/** 
* @author  ZengLei <p>
* @version 2016年8月16日 <p>
*/
public class LogoutLogic extends BaseLogic {
	private static final String TAG = LogoutLogic.class.getSimpleName();
	private static final String LOGOUT_PATH = "login/userLogout";
	
	private LogicListener callback;
	private Activity activity;
	
	public LogoutLogic(Activity activity, LogicListener callback) {
		this.activity = activity;
		this.callback = callback;
	}
	
	public void logout() {
		String userId = SdkSession.getInstance().getUserId();
		if (TextUtils.isEmpty(userId)) {
			if (callback != null) {
				callback.success();
			}
			return;
		}
		
		final LoadingDialog dialog = new LoadingDialog(activity, "正在退出游戏");
		dialog.show();
		
		String url = HOST_PASSPORT + LOGOUT_PATH;
		LogoutModel model = new LogoutModel();
		
		RequestExe.post(url, model, new IRequestListener() {
			
			@Override
			public void success(HashMap<String, String> map) {
				dialog.dismiss();
				if (callback != null) {
					callback.success();
				}
			}
			
			@Override
			public void failed(String errorMsg, int errorCode) {
				dialog.dismiss();
				
				if (callback != null) {
					callback.failed(errorMsg);
				}
			}
		});
	}
}
