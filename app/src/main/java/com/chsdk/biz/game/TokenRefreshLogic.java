package com.chsdk.biz.game;

import java.util.HashMap;

import android.content.Context;

import com.caohua.games.app.AppContext;
import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.DataStorage;
import com.chsdk.configure.SdkSession;
import com.chsdk.configure.UserDBHelper;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.game.TokenRefreshModel;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.utils.LogUtil;
import com.chsdk.utils.NetworkUtils;

import org.greenrobot.eventbus.EventBus;

/***
 * Token令牌刷新请求接口
 * 
 * @author CXK
 *
 */
public class TokenRefreshLogic extends BaseLogic {
	private static final String TAG = TokenRefreshLogic.class.getSimpleName();
	private static final String TOKEN_REFRSH_PATH = "sdk/refreshToken";
	private static long lastRefreshTime = 0;
	
	private LogicListener listener;

	private TokenRefreshLogic(LogicListener listener) {
		this.listener = listener;
	}

	private void tokenRefresh() {
		String url = HOST_PASSPORT + TOKEN_REFRSH_PATH;
		TokenRefreshModel model = new TokenRefreshModel();

		RequestExe.post(url, model, new IRequestListener() {

			@Override
			public void success(HashMap<String, String> map) {
				LogUtil.errorLog(TAG, "TokenRefresh success");
				
				if (map != null) {
					String token = map.get(HttpConsts.RESULT_PARAMS_TOKEN);
					LoginUserInfo info = UserDBHelper.getUser(AppContext.getAppContext(), SdkSession.getInstance().getUserName());
					if (info == null) {
						failed("", 0);
						return;
					}

					info.token = token;
					UserDBHelper.updateUser(AppContext.getAppContext(), info);
					SdkSession.getInstance().setUserInfo(info);

					if (listener != null) {
						listener.success();
					}
				}
			}

			@Override
			public void failed(String error, int errorCode) {
				LogUtil.errorLog(TAG, "TokenRefresh fail：" + error);
				if (errorCode != 0) {
					DataStorage.setAppLogin(SdkSession.getInstance().getAppContext(), false);
					EventBus.getDefault().post("1");
				}
				if (listener != null) {
					listener.failed(error);
				}
			}
		});
	}

	public static synchronized void refresh(final LogicListener listener) {
		final Context context = SdkSession.getInstance().getAppContext();
		if (!NetworkUtils.isNetworkConnected(context)) {
			LogUtil.errorLog(TAG, "refresh token: no network");
			if (listener != null) {
				listener.failed(null);
			}
			return;
		}

		LogUtil.errorLog(TAG, "refresh token");
		
		if (System.currentTimeMillis() - lastRefreshTime < 0) {
			lastRefreshTime = System.currentTimeMillis();
		} else if (System.currentTimeMillis() - lastRefreshTime > 10000) {
			lastRefreshTime = System.currentTimeMillis();
		} else if (System.currentTimeMillis() - lastRefreshTime < 10000) {
			LogUtil.errorLog(TAG, "refresh token 10s interval");
			if (listener != null) {
				listener.failed(null);
			}
			return;
		}

		if (!AppContext.getAppContext().isLogin()) {
			return;
		}

		TokenRefreshLogic logic = new TokenRefreshLogic(listener);
		logic.tokenRefresh();
	}
}
