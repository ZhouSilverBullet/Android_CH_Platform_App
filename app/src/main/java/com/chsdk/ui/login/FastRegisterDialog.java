package com.chsdk.ui.login;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.chsdk.api.LoginCallBack;
import com.chsdk.biz.BaseLogic.LogicListener;
import com.chsdk.biz.login.LoginLogic;
import com.chsdk.biz.login.RegisterLogic;
import com.chsdk.ui.h5.LocalH5Activity;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.LoadingDialog;
import com.chsdk.utils.CryptionUtil;
import com.chsdk.utils.DeviceUtil;
import com.chsdk.utils.PicUtil;
import com.chsdk.utils.VerifyFormatUtil;

import java.util.regex.Pattern;

public class FastRegisterDialog extends BaseLoginDialog implements OnClickListener {
	private String autoUserName;
	private TextView ch_dialog_backgame, ch_dialog_regist_item;
	private LinearLayout ch_dialog_fast_register, ch_dialog_phone_register;
	private Button ch_dialog_entergame;
    private EditText ch_dialog_username,ch_dialog_password,inviteCode;
    private View inviteCodeLayout;

    public FastRegisterDialog(Activity activity, String autoUserName, LoginCallBack callback) {
		super(activity, callback, R.layout.ch_dialog_fast_register);
		this.autoUserName = autoUserName;
	}

	@Override
	public void onClick(View v) {
		if (ch_dialog_entergame == v) {
			String userName = ch_dialog_username.getText().toString().trim();
			String password = ch_dialog_password.getText().toString().trim();
			String code = inviteCode.getText().toString().trim();
			if (!VerifyFormatUtil.verifyUserName(userName)) {
				CHToast.show(activity, "用户名需要输入5-12位");
				return;
			}

			if (VerifyFormatUtil.isPhoneNum(userName)) {
				CHToast.show(activity, "不能使用手机号进行快速注册");
				return;
			}
			if (!VerifyFormatUtil.verifyPassWord(password)) {
				CHToast.show(activity, "密码需要输入6-16位");
				return;
			}

			Pattern p = Pattern.compile("[a-zA-Z0-9_.]{5,16}");
			if (!p.matcher(password).matches()) {
				CHToast.show(activity, "密码包含非法字符");
				return;
			}

			DeviceUtil.hideSoftInput(activity.getApplicationContext(), ch_dialog_password);
			password = CryptionUtil.encodeMd5(password);
			doNormalRegister(userName, password, code);
		} else if (v==ch_dialog_backgame) {
			dismiss();
			new LoginDialog(activity, callback).show();
		} else if (v == ch_dialog_phone_register) {
			dismiss();
			new PhoneRegisterDialog(activity, callback).show();
		} else if (v == ch_dialog_regist_item) {
			LocalH5Activity.start(activity, "Protocol");
		}
	}

	private void doNormalRegister(final String userName, final String passwd, String inviteCode) {
		final LoadingDialog dialog = new LoadingDialog(activity, "正在注册");
		dialog.show();

		RegisterLogic logic = new RegisterLogic(new LogicListener() {

			@Override
			public void failed(String errorMsg) {
				if (dialog != null) {
					dialog.dismiss();
				}
				CHToast.show(activity, "注册失败：" + errorMsg);
			}

			@Override
			public void success(String...result) {
				if (dialog != null) {
					dialog.dismiss();
				}

				doLogin(userName, result[0]);
			}
		});
		logic.register(userName, passwd, inviteCode);
	}

	private void doLogin(final String userName, String autoToken) {
		final LoadingDialog loadingDialog = new LoadingDialog(activity, "正在登录");
		loadingDialog.show();

		LoginLogic logic = new LoginLogic(activity, new LogicListener() {

			@Override
			public void success(String...result) {
				if (loadingDialog != null) {
					loadingDialog.dismiss();
				}

				if (userName.equals(session.getAutoUserName())) {
					session.setAutoUserName(null);
				}

				CutPhoto(dialog.getWindow().getDecorView(), userName);
				dismiss();

				callbackSuccess(userName, session.getUserId(), session.getToken());
			}

			@Override
			public void failed(String error) {
				if (loadingDialog != null) {
					loadingDialog.dismiss();
				}
				CHToast.show(activity, "进入失败：" + error);
				callbackFailed(error);
			}
		});
		logic.loginAuto(userName, autoToken);
	}

	private void CutPhoto(final View decorView, final String userName) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (activity != null) {
					Context context = activity.getApplicationContext();
					String savePath = PicUtil.cropPic(context, decorView, userName);
					if (!TextUtils.isEmpty(savePath)) {
						activity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (activity != null)
									CHToast.show(activity, "您的账号密码的截图以保存到您的手机相册中");
							}
						});
					}
				}
			}
		}).start();
	}

	public void dismiss() {
		if (dialog != null)
			dialog.dismiss();
	}

	@Override
	protected void initDialog() {
		setCornerRound();

		ch_dialog_regist_item = getView(R.id.ch_dialog_regist_item);
		ch_dialog_entergame = getView(R.id.ch_dialog_entergame);
		ch_dialog_backgame = getView(R.id.ch_dialog_backgame);
		ch_dialog_fast_register = getView(R.id.ch_dialog_fast_register);
		ch_dialog_phone_register = getView(R.id.ch_dialog_phone_register);
		ch_dialog_username = getView(R.id.ch_dialog_username);
		ch_dialog_password = getView(R.id.ch_dialog_password);
		inviteCode = getView(R.id.ch_dialog_code);
        inviteCodeLayout = getView(R.id.ch_fast_invite_code_ll);
        if (!TextUtils.isEmpty(AppContext.getAppContext().inviteCodeShow)
                && AppContext.getAppContext().inviteCodeShow.equalsIgnoreCase("y")) {
            inviteCodeLayout.setVisibility(View.VISIBLE);
        } else {
            inviteCode.setVisibility(View.GONE);
        }
        ch_dialog_password.setOnEditorActionListener(new EditText.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        if (actionId == EditorInfo.IME_ACTION_DONE) {
		        	ch_dialog_entergame.performClick();
		        }
		        return false;
			}
		});

		ch_dialog_backgame.setOnClickListener(this);
		ch_dialog_fast_register.setOnClickListener(this);
		ch_dialog_phone_register.setOnClickListener(this);
		ch_dialog_entergame.setOnClickListener(this);
		ch_dialog_regist_item.setOnClickListener(this);

		ch_dialog_username.setText(autoUserName);
		if (!TextUtils.isEmpty(autoUserName)) {
			String radomPsw = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
			ch_dialog_password.setText(radomPsw);
		}
	}
}
