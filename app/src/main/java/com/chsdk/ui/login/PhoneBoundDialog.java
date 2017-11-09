package com.chsdk.ui.login;

import com.caohua.games.R;
import com.chsdk.api.LoginCallBack;
import com.chsdk.biz.BaseLogic.LogicListener;
import com.chsdk.biz.login.BoundPhoneLogic;
import com.chsdk.biz.SendSmsTask;
import com.chsdk.configure.DataStorage;
import com.chsdk.configure.SdkSession;
import com.chsdk.configure.UserDBHelper;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.ui.widget.CHAlertDialog;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.LoadingDialog;
import com.chsdk.utils.DeviceUtil;
import com.chsdk.utils.NetworkUtils;
import com.chsdk.utils.VerifyFormatUtil;
import com.chsdk.utils.ViewUtil;

import android.app.Activity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PhoneBoundDialog extends BaseLoginDialog implements OnClickListener {
	private TextView tvSendMsg;
	private Button btnBound, btnCancel;
	private EditText ch_dialog_verification_code, ch_dialog_phone_number;

	public PhoneBoundDialog(Activity activity, LoginCallBack callback) {
		super(activity, callback, R.layout.ch_dialog_phone_bound);
	}

	@Override
	protected void initDialog() {
		setCornerRound();
		
		tvSendMsg = getView(R.id.ch_dialog_sendmsg);
		btnBound = getView(R.id.ch_dialog_phone_bound);
		btnCancel = getView(R.id.ch_dialog_phone_cancel);
		ch_dialog_phone_number = getView(R.id.ch_dialog_phone_number);
		ch_dialog_verification_code = getView(R.id.ch_dialog_verification_code);
	
		tvSendMsg.setOnClickListener(this);
		btnBound.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		ch_dialog_verification_code.setOnClickListener(this);
		ch_dialog_verification_code.setOnEditorActionListener(new EditText.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					btnBound.performClick();
				}
				return false;
			}
		});
	}

	@Override
	public void onClick(View v) {
		if (v == btnCancel) {
			dismiss();
		} else if (v == btnBound) {
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
			DeviceUtil.hideSoftInput(activity.getApplicationContext(), ch_dialog_verification_code);
			doPhoneBound(phone_number, verification_code);
		} else if (v == tvSendMsg) {
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
		} 
	}

	private void doPhoneBound(final String phoneNum, String authCode) {
		final LoadingDialog dialog = new LoadingDialog(activity, "正在绑定");
		dialog.show();

		BoundPhoneLogic logic = new BoundPhoneLogic(authCode, new LogicListener() {
			@Override
			public void success(String... result) {
				if (dialog != null) {
					dialog.dismiss();
				}
				dismiss();
				CHToast.show(activity, "绑定成功");
				
				String userName = SdkSession.getInstance().getUserName();
				DataStorage.deleteLoginCountInfo(activity, userName);
			}

			@Override
			public void failed(String errorMsg) {
				if (dialog != null) {
					dialog.dismiss();
				}
				CHToast.show(activity, "绑定失败：" + errorMsg);
			}
		});
		logic.boundPhone();
	}

	private void sendMsg(String phoneNum) {
		if (!NetworkUtils.isNetworkConnected(activity)) {
			CHToast.show(activity, "请检查您当前的网络");
			return;
		}
		
		SendSmsTask task = new SendSmsTask(activity, phoneNum, SendSmsTask.TYPE_BOUND_PHONE, new LogicListener() {
			
			@Override
			public void success(String... result) {
				ViewUtil.setClickLimit(tvSendMsg, 5000, "亲 不要频繁发送哟");				
			}
			
			@Override
			public void failed(String errorMsg) {
				ViewUtil.setClickLimit(tvSendMsg, 3000, "亲 不要频繁发送哟");				
			}
		});
		task.execute();
	}
	
	public static void addLoginCount(final Activity activity) {
		boolean isBound = SdkSession.getInstance().isBoundPhone();
		if (isBound) {
			return;
		}
		
		String userName = SdkSession.getInstance().getUserName();
		LoginUserInfo info = UserDBHelper.getUser(activity, userName);
		if (info == null) {
			return;
		}
		int loginCount = info.loginCount;

		if (loginCount >= 3) {
			return;
		}
		info.loginCount = loginCount + 1;
		UserDBHelper.updateUser(activity, info);
	}
	
	public static void showTip(final Activity activity) {
		boolean isBound = SdkSession.getInstance().isBoundPhone();
		if (isBound) {
			return;
		}
		
		String userName = SdkSession.getInstance().getUserName();
		LoginUserInfo info = UserDBHelper.getUser(activity, userName);
		if (info == null) {
			return;
		}
		int loginCount = info.loginCount;

		if (loginCount > 3) {
			return;
		}

		if (loginCount < 3) {
			return;
		}

		info.loginCount = loginCount + 1;
		UserDBHelper.updateUser(activity, info);

		final CHAlertDialog boundPhoneDialog = new CHAlertDialog(activity);
		boundPhoneDialog.show();
		boundPhoneDialog.setTitle("绑定手机  享受特权");
		boundPhoneDialog.setContent("特权1：手机号找回密码\n特权2：专属游戏礼包\n特权3：专属游戏活动\n特权4：专属MM客服");
		boundPhoneDialog.setCancelButton("不再提示", null);
		boundPhoneDialog.setOkButton("立即绑定", new OnClickListener() {
			@Override
			public void onClick(View v) {
				boundPhoneDialog.dismiss();
				PhoneBoundDialog dialog = new PhoneBoundDialog(activity, null);
				dialog.show();
			}
		});
	}
}
