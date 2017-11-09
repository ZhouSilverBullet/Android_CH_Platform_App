package com.chsdk.ui.login;

import android.app.Activity;
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
import com.chsdk.biz.SendSmsTask;
import com.chsdk.biz.login.LoginLogic;
import com.chsdk.biz.login.RegisterLogic;
import com.chsdk.configure.SdkSession;
import com.chsdk.ui.h5.LocalH5Activity;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.LoadingDialog;
import com.chsdk.utils.DeviceUtil;
import com.chsdk.utils.NetworkUtils;
import com.chsdk.utils.VerifyFormatUtil;
import com.chsdk.utils.ViewUtil;

public class PhoneRegisterDialog extends BaseLoginDialog implements OnClickListener {
	private TextView ch_dialog_backgame, ch_dialog_sendmsg, ch_dialog_regist_item;
//	private LinearLayout ch_dialog_fast_register, ch_dialog_phone_register;
	private Button ch_dialog_enter_game;
	private EditText ch_dialog_verification_code, ch_dialog_phone_number, inviteCode;
	private View inviteCodeLayout;

	public PhoneRegisterDialog(Activity activity, LoginCallBack callback) {
		super(activity, callback, R.layout.ch_dialog_phone_register);
	}
	
	@Override
	protected void initDialog() {
		setCornerRound();
		
		ch_dialog_backgame = getView(R.id.ch_dialog_backgame);
//		ch_dialog_fast_register = getView(R.id.ch_dialog_fast_register);
//		ch_dialog_phone_register = getView(R.id.ch_dialog_phone_register);
		ch_dialog_sendmsg = getView(R.id.ch_dialog_sendmsg);
		ch_dialog_enter_game = getView(R.id.ch_dialog_enter_game);
		ch_dialog_phone_number = getView(R.id.ch_dialog_phone_number);
		ch_dialog_verification_code = getView(R.id.ch_dialog_verification_code);
		ch_dialog_regist_item = getView(R.id.ch_dialog_regist_item);
		inviteCode = getView(R.id.ch_dialog_ph_code);
		inviteCodeLayout = getView(R.id.ch_ph_invite_code_ll);
		if (!TextUtils.isEmpty(AppContext.getAppContext().inviteCodeShow)
				&& AppContext.getAppContext().inviteCodeShow.equalsIgnoreCase("y")) {
			inviteCodeLayout.setVisibility(View.VISIBLE);
		} else {
			inviteCodeLayout.setVisibility(View.GONE);
		}

		ch_dialog_backgame.setOnClickListener(this);
//		ch_dialog_fast_register.setOnClickListener(this);
//		ch_dialog_phone_register.setOnClickListener(this);
		ch_dialog_sendmsg.setOnClickListener(this);
		ch_dialog_enter_game.setOnClickListener(this);
		ch_dialog_verification_code.setOnClickListener(this);
		ch_dialog_verification_code.setOnEditorActionListener(new EditText.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					ch_dialog_enter_game.performClick();
				}
				return false;
			}
		});
		ch_dialog_regist_item.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == ch_dialog_enter_game) {
			String phone_number = ch_dialog_phone_number.getText().toString().trim();
			if (!VerifyFormatUtil.isPhoneNum(phone_number)) {
				CHToast.show(activity, "请输入正确的手机号码");
				return;
			}

			String verification_code = ch_dialog_verification_code.getText().toString().trim();
			if (TextUtils.isEmpty(verification_code)) {
				CHToast.show(activity, "请输入验证码");
				return;
			}
			String code = inviteCode.getText().toString().trim();
			DeviceUtil.hideSoftInput(activity.getApplicationContext(), ch_dialog_verification_code);
			doPhoneRegister(phone_number, verification_code, code);
		}
		if (v == ch_dialog_backgame) {
			dismiss();
			new LoginDialog(activity, callback).show();
		} else if (v == ch_dialog_sendmsg) {
			String phone_number = ch_dialog_phone_number.getText().toString().trim();
			if (TextUtils.isEmpty(phone_number)) {
				CHToast.show(activity, "请输入手机号码");
				return;
			}

			if (!VerifyFormatUtil.isPhoneNum(phone_number)) {
				CHToast.show(activity, "请输入正确的手机号码");
				return;
			}
			sendMsg(phone_number);
		} else if (v == ch_dialog_regist_item) {
			LocalH5Activity.start(activity, "Protocol");
		}
	}

	private void doPhoneRegister(final String phoneNum, String authCode, String inviteCode) {
		String userId = SdkSession.getInstance().getUserId();
		if (TextUtils.isEmpty(userId)) {
			CHToast.show(activity, "请先发送验证码");
			return;
		}
		
		final LoadingDialog dialog = new LoadingDialog(activity, "正在注册");
		dialog.show();

		RegisterLogic logic = new RegisterLogic(new LogicListener() {
			@Override
			public void success(String... result) {
				if (dialog != null) {
					dialog.dismiss();
				}
				doLogin(phoneNum, result[0]);
			}

			@Override
			public void failed(String errorMsg) {
				if (dialog != null) {
					dialog.dismiss();
				}
				CHToast.show(activity, "注册失败：" + errorMsg);
			}
		});
		logic.phoneRegister(phoneNum, authCode, inviteCode);
	}

	private void doLogin(final String phoneNum, final String autoToken) {
		final LoadingDialog dialog = new LoadingDialog(activity, "正在登录");
		dialog.show();

		LoginLogic logic = new LoginLogic(activity, new LogicListener() {

			@Override
			public void success(String... result) {
				if (dialog != null) {
					dialog.dismiss();
				}

				dismiss();
				
				callbackSuccess(phoneNum, session.getUserId(), session.getToken());
			}

			@Override
			public void failed(String error) {
				if (dialog != null) {
					dialog.dismiss();
				}

				CHToast.show(activity, "进入失败：" + error);
				callbackFailed(error);
			}
		});
		logic.loginAuto(phoneNum, autoToken);
	}

	private void sendMsg(String phoneNum) {
		if (!NetworkUtils.isNetworkConnected(activity)) {
			CHToast.show(activity, "请检查您当前的网络");
			return;
		}
		
		SendSmsTask task = new SendSmsTask(activity, phoneNum, SendSmsTask.TYPE_REGISTER, new LogicListener() {
			
			@Override
			public void success(String... result) {
				ViewUtil.setClickLimit(ch_dialog_sendmsg, 5000, "亲 不要频繁发送哟");				
			}
			
			@Override
			public void failed(String errorMsg) {
				ViewUtil.setClickLimit(ch_dialog_sendmsg, 3000, "亲 不要频繁发送哟");				
			}
		});
		task.execute();
	}

}
