package com.chsdk.biz.game;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.game.PushEntry;
import com.chsdk.model.game.PushModel;
import com.chsdk.ui.login.PicPushDialog;
import com.chsdk.ui.login.TextPushDialog;
import com.chsdk.utils.LogUtil;
import com.chsdk.utils.NetworkUtils;
import com.chsdk.utils.PicUtil;

import java.util.HashMap;

/** 
* @author  ZengLei <p>
* @version 2016年9月8日 <p>
*/
public class PushLogic extends BaseLogic {
	private static final String TAG = PushLogic.class.getSimpleName();
	private static final String PUSH_PATH = "sdk/msgPush";
	private static final String NOT_LOGIN_PUSH_PATH = "ucenter/msgPush";

	private Activity activity;
	
	public PushLogic(Activity activity) {
		this.activity = activity;
	}

	public void getMsg(String msg, boolean login) {
		final Context context = SdkSession.getInstance().getAppContext();
		if (!NetworkUtils.isNetworkConnected(context)) {
			return;
		}
		
		String url = getPath(login);
		
		PushModel model = new PushModel(msg,login);
		
		RequestExe.post(url, model, new IRequestListener() {
			
			@Override
			public void success(HashMap<String, String> map) {
				if (map != null) {
					String url = map.get(HttpConsts.RESULT_PARAMS_MSG_CONTENT);
					String link = map.get(HttpConsts.RESULT_PARAMS_MSG_LINK);
					String msgId = map.get(HttpConsts.RESULT_PARAMS_MSG_ID);
					String msgExpire = map.get(HttpConsts.RESULT_PARAMS_MSG_EXPIRE_TIME);
					String msgType = map.get(HttpConsts.RESULT_PARAMS_MSG_TYPE);
					String msgShowType = map.get(HttpConsts.RESULT_PARAMS_MSG_SHOW_TYPE);
					int isAward = getInt(map.get("is_award")); //奖励领取
					if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(msgId)) {
						PushEntry entry = new PushEntry();
						entry.imgUrl = url;
						entry.jumpUrl = link;
						entry.type = msgType;
						entry.showType = msgShowType;
						entry.msgId = msgId;
						entry.expireTime = msgExpire;
						entry.isAward = isAward;
						if (PushEntry.TYPE_PIC.equals(msgType)) {
							downloadPic(context, entry);
						} else {
							showTextPushDialog(entry);
						}
						return;
					}
				} 
				LogUtil.errorLog(TAG, HttpConsts.ERROR_CODE_PARAMS_VALID);
			}
			
			@Override
			public void failed(String error, int errorCode) {
				LogUtil.errorLog(TAG, error);
			}
		});
	}

	private int getInt(String value) {
		int i = 0;
		if (TextUtils.isEmpty(value)) {
			return 0;
		}
		try {
			i = Integer.parseInt(value);
		} catch (Exception e) {
		}
		return i;
	}

	public String getPath(boolean login) {
		if(login){
			return  HOST_PASSPORT + PUSH_PATH;
		}else {
			return HOST_APP + NOT_LOGIN_PUSH_PATH;
		}
	}
	private void downloadPic(final Context context, final PushEntry entry) {
		PicUtil.downloadPushPic(context, entry.imgUrl, new LogicListener() {
			
			@Override
			public void success(String... result) {
				if (activity.isFinishing())
					return;

				entry.width = Integer.valueOf(result[0]);
				entry.heigth = Integer.valueOf(result[1]);
				if (entry.width <= 0 || entry.heigth <= 0) {
					return;
				}
				PicPushDialog dialog = new PicPushDialog(activity, entry);
				dialog.show();
			}
			
			@Override
			public void failed(String errorMsg) {
				LogUtil.debugLog(TAG, "downloadPic_fail_" + errorMsg);
			}
		});
	}
	
	private void showTextPushDialog(PushEntry entry) {
		TextPushDialog dialog = new TextPushDialog(activity, entry);
		dialog.show();
	}
}
