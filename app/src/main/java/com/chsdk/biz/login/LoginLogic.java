package com.chsdk.biz.login;

import java.util.HashMap;

import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.DataStorage;
import com.chsdk.configure.SdkSession;
import com.chsdk.configure.UserDBHelper;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.login.LoginModel;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.ui.widget.LoadingDialog;
import com.chsdk.utils.LogUtil;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

/** 
* @author  ZengLei <p>
* @version 2016年8月18日 <p>
*/
public class LoginLogic extends BaseLogic{
	private static final String TAG = LoginLogic.class.getSimpleName();
	private static final String LOGIN_NORMAL_PATH = "login/normalLogin";
	private static final String LOGIN_AUTO_PATH = "login/autoLogin";
	private LoadingDialog loadDialog;
	private Activity activity;
	private LogicListener listener;
	
	public LoginLogic(Activity activity, LogicListener listener) {
		this.activity = activity;
		this.listener = listener;
	}
	
	private void showWaitDialog() {
		loadDialog = new LoadingDialog(activity, "正在进入游戏");
		loadDialog.show();
	}
	
	private void dismissWaitDialog() {
		if (loadDialog != null) {
			loadDialog.dismiss();
		}
	}
	
	public void loginNormal(final String userName, final String passwd) {
		showWaitDialog();
		
		String url = HOST_PASSPORT + LOGIN_NORMAL_PATH;
		LoginModel model = new LoginModel(false);
		model.setUserName(userName);
		model.setPasswd(passwd);
		
		RequestExe.post(url, model, new RequestListener(userName, passwd));
	}
	
	public void loginAuto(final String userName, String autoToken) {
		showWaitDialog();
		
		String url = HOST_PASSPORT + LOGIN_AUTO_PATH;
		LoginModel model = new LoginModel(true);
		model.setUserName(userName);
		model.setAutoToken(autoToken);
		
		RequestExe.post(url, model, new RequestListener(userName, null));
	}
	
	class RequestListener implements IRequestListener {

		private String userName;
		private String passwd;
		
		public RequestListener(String userName, String passwd) {
			this.userName = userName;
			this.passwd = passwd;
		}
		
		@Override
		public void success(HashMap<String, String> map) {
			dismissWaitDialog();
			
			if (map != null) {
				String userId = map.get(HttpConsts.RESULT_PARAMS_USER_ID);
				String token = map.get(HttpConsts.RESULT_PARAMS_TOKEN);
				String forum_name = map.get(HttpConsts.RESULT_PARAMS_FORUM_NAME);
				String autoToken = map.get(HttpConsts.RESULT_PARAMS_AUTO_TOKEN);
				String isBind = map.get(HttpConsts.RESULT_PARAMS_IS_BIND_PHONE);
				String nickName = map.get(HttpConsts.RESULT_PARAMS_NICK_NAME);
				String userFlag = map.get(HttpConsts.RESULT_PARAMS_USER_FLAG);
				String accountFace = map.get(HttpConsts.RESULT_PARAMS_ACCOUNT_FACE);
				String qq = map.get(HttpConsts.RESULT_PARAMS_QQ);
				String sex = map.get(HttpConsts.RESULT_PARAMS_SEX);
				String address = map.get(HttpConsts.RESULT_PARAMS_ADDRESS);
				String birth = map.get(HttpConsts.RESULT_PARAMS_BIRTH);
				String pp = map.get(HttpConsts.RESULT_PARAMS_PHONE_PWD);
				String vip_name = map.get("vip_name");
				String auth_identity = map.get("auth_identity");
				String vip_level = map.get("vip_level");
				if (!TextUtils.isEmpty(auth_identity)) {
					int identity = Integer.parseInt(auth_identity);
					if (identity == 1) {
						DataStorage.setPayCheck(activity, userId, true);
					} else {
						DataStorage.setPayCheck(activity, userId, false);
					}
				}

				if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(token) && 
						!TextUtils.isEmpty(autoToken)) {
					Context context = SdkSession.getInstance().getAppContext();
					LoginUserInfo info = UserDBHelper.getUser(context, userName);
					int type = 0;
					if (!TextUtils.isEmpty(passwd)) {
						if (info == null) {
							type = 1;
							info = new LoginUserInfo();
							info.userName = userName;
							info.passwd = passwd;
						} else {
							type = 2;
							info.passwd = passwd;
						}
					} else {
						// 自动登录 之前数据库肯定有保存
						if (info != null) {
							type = 3;
						}
					}

					info.autoToken = autoToken;
					info.vip_level = vip_level;
					info.vip_name = vip_name;
					info.token = token;
					info.nickName = nickName;
					info.userId = userId;
					info.imgUrl = accountFace;
					info.qq = qq;
					if (sex != null) {
						info.sex = Integer.valueOf(sex);
					} else {
						info.sex = 0;
					}
					info.address = address;
					info.birthDay = birth;
					info.userFlag = Integer.valueOf(userFlag);
					info.bindPhone = Integer.valueOf(isBind);
					info.forum_name = forum_name;

					if (!TextUtils.isEmpty(pp)) {
						info.passwd = pp;
					}
					if (type == 1) {
						UserDBHelper.addUser(context, info);
					} else if (type == 2) {
						UserDBHelper.updateUser(context, info);
					} else if (type == 3) {
						UserDBHelper.updateUser(context, info);
					}
					
					SdkSession session = SdkSession.getInstance();
					session.setUserInfo(info);
					session.setIsBoundPhone(isBind);

					if (listener != null) {
						listener.success();
					}
					return;
				}
			}
			
			if (listener != null) {
				listener.failed(HttpConsts.ERROR_CODE_PARAMS_VALID);
			}
		}

		@Override
		public void failed(String error, int errorCode) {
			LogUtil.debugLog(TAG, "failed:" + error);
			dismissWaitDialog();
			
			if (listener != null) {
				listener.failed(error);
			}
		}
	}
}
