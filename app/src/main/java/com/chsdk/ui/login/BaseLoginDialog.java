package com.chsdk.ui.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import com.caohua.games.R;
import com.chsdk.api.CHSdk;
import com.chsdk.api.LoginCallBack;
import com.chsdk.biz.BaseLogic.LogicListener;
import com.chsdk.biz.login.AdLogoutLogic;
import com.chsdk.configure.SdkSession;
import com.chsdk.utils.ViewUtil;

/**
 * @author ZengLei
 *         <p>
 */
public abstract class BaseLoginDialog {

	protected Activity activity;
	protected LoginCallBack callback;
	protected AlertDialog dialog;
	private View contentView;
	private int layoutId;
	private int gravity;
	protected SdkSession session;

	public BaseLoginDialog(Activity activity, LoginCallBack callback, int layoutId) {
		this.activity = activity;
		this.callback = callback;
		this.layoutId = layoutId;
		session = SdkSession.getInstance();
	}

	protected void callbackSuccess(String userName, String userId, String token) {
		if (callback != null) {
			callback.success(userName, userId, token);
		}
	}

	protected void callbackFailed(String msg) {
		if (callback != null) {
			callback.failed(msg);
		}
	}
	
	protected void callbackExit() {
		CHSdk.exit();
		if (callback != null) {
			callback.exit();
		}
	}
	
	protected void setGravity(int gravity) {
		this.gravity = gravity;
	}

	public void show() {
		dialog = new AlertDialog.Builder(activity, R.style.ch_base_style).create();
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);
//		dialog.setOnKeyListener(new BackListener());
		dialog.show();
		
		Window window = dialog.getWindow();
		window.clearFlags(LayoutParams.FLAG_FULLSCREEN | LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		window.addFlags(LayoutParams.FLAG_DIM_BEHIND);
		window.setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		LayoutParams lp = window.getAttributes();
		lp.dimAmount = 0.3f;
		if (gravity > 0) {
			lp.gravity = gravity;
			lp.y = 50;
		}
		window.setAttributes(lp);
		
		contentView = LayoutInflater.from(activity).inflate(layoutId, null);
		window.setContentView(contentView);

		initDialog();
	}

	public void dismiss() {
		if (dialog != null)
			dialog.dismiss();
	}

	@SuppressWarnings("unchecked")
	protected <T extends View> T getView(int id) {
		return (T) contentView.findViewById(id);
	}

	protected void setCornerRound() {
		ViewUtil.setBackground(contentView, ViewUtil.addBtnBackgroundRound("#f4f4f4", true, ViewUtil.dp2px(activity, 3)));
	}

	protected abstract void initDialog();
	
	class BackListener implements OnKeyListener {

		@Override
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {  
				final LogoutDialog logoutDialog = new LogoutDialog(activity, new LogicListener() {
					@Override
					public void success(String... result) {
						dismiss();
						callbackExit();
					}

					@Override
					public void failed(String errorMsg) {
						dismiss();
						callbackExit();
					}
				});
				logoutDialog.showDialog();
				return true;
            }
			
			return false;
		}
	}
}
