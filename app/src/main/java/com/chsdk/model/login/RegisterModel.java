package com.chsdk.model.login;

import com.caohua.games.app.AppContext;
import com.chsdk.model.BaseModel;
import com.umeng.analytics.AnalyticsConfig;

/** 
* @author  ZengLei <p>
* @version 2016年8月15日 <p>
* 注册参数类
*/
public class RegisterModel extends BaseModel{
	public static final int TYPE_NORMAL = 1;
	public static final int TYPE_FAST = 2;
	public static final int TYPE_PHONE = 3;
	
	private int platformId; //平台ID
	private String deviceNo; //设备ID
	// 正常注册
	private String userName; //用户账号
	private String passwd; //账号密码
	// 手机注册
	private String userId;
	private String authCode;
	private String sourceId;
	private String inviteCode;
	private int type;
	
	public RegisterModel(int type) {
		this.type = type;
		platformId = session.getPlatformId();
		deviceNo = session.getDeviceNo();
		sourceId = "0";
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	
	public void setUserId(String userid) {
		this.userId = userid;
	}
	
	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public void setInviteCode(String inviteCode) {
		this.inviteCode = inviteCode;
	}

	@Override
	public void putDataInMap() {
		put(PARAMS_DEVICE_NO, deviceNo);
		put(PARAMS_PLATFORM_ID, String.valueOf(platformId));
		put(PARAMS_CHANNEL_USER_ID, "639");
		if (type == TYPE_NORMAL) {
			put(PARAMS_PASSWORD, passwd);
			put(PARAMS_USER_NAME, userName);
			put(PARAMS_SOURCE_ID, "576");
			put(PARAMS_INVITE_CODE, inviteCode);
		} else if (type == TYPE_PHONE) {
			put(PARAMS_USER_ID, userId);
			put(PARAMS_AUTH_CODE, authCode);
			put(PARAMS_INVITE_CODE, inviteCode);
		}
	}
}
