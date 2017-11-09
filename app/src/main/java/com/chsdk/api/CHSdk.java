package com.chsdk.api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.chsdk.biz.BaseLogic.LogicListener;
import com.chsdk.biz.game.TokenRefreshHelper;
import com.chsdk.biz.pay.UnionPayMgr;
import com.chsdk.biz.pay.WechatPayMgr;
import com.chsdk.configure.InitTask;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.HttpConsts;
import com.chsdk.model.RequestSyncResult;
import com.chsdk.model.game.UpdateEntry;
import com.chsdk.ui.UpdateGameDialog;
import com.chsdk.ui.login.BaseLoginDialog;
import com.chsdk.ui.login.LoginDialog;
import com.chsdk.ui.login.LogoutDialog;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.utils.CHAsyncTask.TaskListener;
import com.chsdk.utils.LogUtil;
import com.chsdk.utils.NetworkUtils;

public class CHSdk {
	public static boolean accountInvalid; // 在启动app时验证当前登录的账号密码是否正常，验证出现异常时弹出登录框

	public static void init(final Activity activity, final SplashDismissListener2 callback) {
		final Context ctx = activity.getApplicationContext();
		SdkSession.clear();

		if (!NetworkUtils.isNetworkConnected(ctx)) {
			CHToast.show(ctx, "请检查当前的网络");
		}

		InitTask task = new InitTask();
		task.setTaskListener(new TaskListener() {

			@Override
			public void finished(long startTime, Object... params) {
				boolean needUpdate = false;
				boolean error = false;
				String content = null;

				if (params != null && params.length > 0 && params[0] instanceof RequestSyncResult) {
					RequestSyncResult result = (RequestSyncResult) params[0];
					boolean status = result.postStatus;
					if (status) {
						UpdateEntry updateEntry = (UpdateEntry) result.obj;
						if (updateEntry != null) {
							needUpdate = true;
							SdkSession.getInstance().setUpdateEntry(updateEntry);
						}
					} else {
						error = true;
						content = "初始化失败:" + result.msg;
					}
				} else {
					error = true;
					content = "初始化失败:" + HttpConsts.ERROR_CODE_PARAMS_VALID;
				}

				if (!error) {
					if (!needUpdate) {
						UpdateGameDialog.deleteApkFile(activity);
					}
				} else {
					LogUtil.errorLog("InitTask error_" + content);
				}
				if (callback != null) {
					long lastTime = System.currentTimeMillis();
                    LogUtil.errorLog("CHSdk init lastTime - startTime >= 5" + (lastTime - startTime >= 5000));
					callback.dismiss(needUpdate, lastTime - startTime >= 5000);
				}
			}

			@Override
			public void canceled(Object... params) {
			}
		});
		task.execute();
	}

	public static boolean updateApp(Activity activity) {
		UpdateEntry entry = SdkSession.getInstance().getUpdateEntry();
		if (entry != null) {
			UpdateGameDialog upgradeUtil = new UpdateGameDialog(activity, entry);
			upgradeUtil.showDialog();
			return true;
		}
		return false;
	}

	public static void login(Activity activity, LoginCallBack callback) {
		BaseLoginDialog dialog = new LoginDialog(activity, callback);
		dialog.show();
	}

	public static void handleBackAction(final Activity activity, final ExitCallBack exitCallback) {
		LogoutDialog dialog = new LogoutDialog(activity, new LogicListener() {
			@Override
			public void success(String... result) {
				exit();
				if (exitCallback != null) {
					exitCallback.exit();
				}
			}

			@Override
			public void failed(String errorMsg) {
				exit();
				if (exitCallback != null) {
					exitCallback.exit();
				}
			}
		});
		dialog.showDialog();
	}

	public static void exit(){
		TokenRefreshHelper.unregister(SdkSession.getInstance().getAppContext());
		SdkSession.clear();
	}

	public static void handleCHPayStatus(Activity activity, Intent intent, String show) {
		if (intent == null) {
			return ;
		}

		Bundle bundle = intent.getExtras();
		if (bundle == null) {
			return;
		}

		WechatPayMgr.handleResult(activity, bundle, show);
		UnionPayMgr.getInstance().handleResult(activity, bundle, show);
	}
}

