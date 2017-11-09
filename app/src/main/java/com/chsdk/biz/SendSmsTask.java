package com.chsdk.biz;

import com.chsdk.biz.BaseLogic.LogicListener;
import com.chsdk.biz.game.TokenRefreshLogic;
import com.chsdk.configure.DataStorage;
import com.chsdk.configure.SdkSession;
import com.chsdk.configure.UserDBHelper;
import com.chsdk.model.RequestSyncResult;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.LoadingDialog;
import com.chsdk.utils.CHAsyncTask;
import com.chsdk.utils.LogUtil;

import android.content.Context;
import android.text.TextUtils;

/** 
* @author  ZengLei <p>
* @version 2016年8月18日 <p>
*/
public class SendSmsTask extends CHAsyncTask<Void, Void, RequestSyncResult> {
	private static final String TAG = SendSmsTask.class.getSimpleName();
	public static final int TYPE_REGISTER = 1; // 手机注册
	public static final int TYPE_BOUND_PHONE = 2; // 绑定手机
	public static final int TYPE_MB_MOBILE = 3; // 密保手机找回密码
	public static final int TYPE_MODIFY_MB_MOBILE = 4; // 更改密保手机
    private String phoneNum;
    private Context context;
    private LoadingDialog loadingDialog;
    private int type;
    private LogicListener listener;
	
	public SendSmsTask(Context context, String phoneNum, int type, LogicListener listener) {
		this.phoneNum = phoneNum;
		this.context = context;
		this.type = type;
		this.listener = listener;
	}
	
	@Override
	protected void onPreExecute() {
		loadingDialog = new LoadingDialog(context, "正在发送验证码");
		loadingDialog.show();
	}

	@Override
	protected RequestSyncResult doInBackground(Void... params) {
		LogUtil.debugLog(TAG, "doInBackground");
		SendSmsLogic logic = new SendSmsLogic(phoneNum, type);
		return logic.sendSms();
	}

	
	@Override
	protected void onPostExecute(RequestSyncResult result) {
		LogUtil.debugLog(TAG, "onPostExecute result:" + result);

		if (loadingDialog != null) {
			loadingDialog.dismiss();
		}
		
		if (result != null) {
			boolean status = result.postStatus;
			String msg = result.msg;
			if (status) {
				if (!TextUtils.isEmpty(msg) && type == TYPE_REGISTER) {
					LoginUserInfo info = UserDBHelper.getUser(context, phoneNum);
					if (info == null) {
						info = new LoginUserInfo();
						info.userName = phoneNum;
						info.userId = msg;
						UserDBHelper.addUser(context, info);
					} else {
						info.userName = phoneNum;
						info.userId = msg;
						UserDBHelper.updateUser(context, info);
					}
					SdkSession.getInstance().setUserInfo(info);
				}
				CHToast.show(context, "验证码发送成功");
				if (listener != null) {
					listener.success();
				}
				return;
			} else {
				if (BaseLogic.isTokenInvialid(msg)) {
					handleTokenError(msg);
					return;
				}
				
				if (!TextUtils.isEmpty(msg)) {
					CHToast.show(context, msg);
					if (listener != null) {
						listener.failed(msg);
					}
					return;
				} 
				
			}
		}
		
		CHToast.show(context, "验证码发送失败");
		if (listener != null) {
			listener.failed("验证码发送失败");
		}
	}
	
	private void handleTokenError(final String msg){
		if (loadingDialog != null) {
			loadingDialog.show();
		}
		
		TokenRefreshLogic.refresh(new LogicListener() {
			
			@Override
			public void success(String... result) {
				if (loadingDialog != null) {
					loadingDialog.dismiss();
				}
				
				SendSmsTask smsTask = new SendSmsTask(context, phoneNum, type, listener);
				smsTask.execute();
			}
			
			@Override
			public void failed(String errorMsg) {
				if (loadingDialog != null) {
					loadingDialog.dismiss();
				}
				
				CHToast.show(context, msg);
				if (listener != null) {
					listener.failed(msg);
				}
			}
		});
	}
}
