package com.chsdk.biz.login;

import android.content.Context;
import android.text.TextUtils;

import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.SdkSession;
import com.chsdk.configure.UserDBHelper;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.model.login.RegisterModel;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author ZengLei
 *         <p>
 * @version 2016年8月15日
 *          <p>
 */
public class RegisterLogic extends BaseLogic {
	private static final String TAG = RegisterLogic.class.getName();
	private static final String REGISTER_NORMAL_PATH = "register/normalReg";
	private static final String REGISTER_FAST_PATH = "register/fastReg";
	private static final String REGISTER_PHONE_PATH = "register/verifyCode";
	private LogicListener callback;

	public RegisterLogic() {
	}
	
	public RegisterLogic(LogicListener callback) {
		this.callback = callback;
	}

	public void register(final String userName, final String passwd, String inviteCode) {
		String url = HOST_PASSPORT + REGISTER_NORMAL_PATH;
		RegisterModel model = new RegisterModel(RegisterModel.TYPE_NORMAL);
		model.setUserName(userName);
		model.setPasswd(passwd);
		model.setInviteCode(inviteCode);
		RequestExe.post(url, model, new IRequestListener() {

			@Override
			public void success(HashMap<String, String> map) {
				saveData(map, userName, passwd);
			}

			@Override
			public void failed(String error, int errorCode) {
				if (callback != null) {
					callback.failed(error);
				}
			}
		});
	}
	
	public String fastRegister() throws IOException {
		String url = HOST_PASSPORT + REGISTER_FAST_PATH;
		RegisterModel model = new RegisterModel(RegisterModel.TYPE_FAST);
		String result = RequestExe.postSync(url, model.getDataMap());
		return result;
	}
	
	public void phoneRegister(final String phoneNum, String authCode, String inviteCode) {
		String url = HOST_PASSPORT + REGISTER_PHONE_PATH;
		RegisterModel model = new RegisterModel(RegisterModel.TYPE_PHONE);
		model.setAuthCode(authCode);
		model.setInviteCode(inviteCode);
		model.setUserId(SdkSession.getInstance().getUserId());
		RequestExe.post(url, model, new IRequestListener() {
			
			@Override
			public void success(HashMap<String, String> map) {
				saveData(map, phoneNum, null);
			}
			
			@Override
			public void failed(String error, int errorCode) {
				if (callback != null) {
					callback.failed(error);
				}
			}
		});
	}
	
	private void saveData(HashMap<String, String> map, String userName, String passwd) {
		if (callback != null) {
			if (map != null) {
//				String userId = map.get(HttpConsts.RESULT_PARAMS_USER_ID);// TYPE_NORMAL的返回值,登录接口也有返回,这里可以不存
				String autoToken = map.get(HttpConsts.RESULT_PARAMS_AUTO_TOKEN);
				if (!TextUtils.isEmpty(autoToken)) {
					Context context = SdkSession.getInstance().getAppContext();

					LoginUserInfo info = new LoginUserInfo();
					info.userName = userName;
					info.passwd = passwd;
					info.autoToken = autoToken;
					UserDBHelper.addUser(context, info);

//					DataStorage.saveUserName(context, userName);
//					DataStorage.saveAutoToken(context, userName, autoToken);
//					if (!TextUtils.isEmpty(passwd)) {
//						// normal
//						DataStorage.savePasswd(context, userName, passwd);
//					}
					
					callback.success(autoToken);
					return;
				}
			} 
			callback.failed(HttpConsts.ERROR_CODE_PARAMS_VALID);
		}
	}
}
