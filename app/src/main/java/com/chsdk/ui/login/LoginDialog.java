package com.chsdk.ui.login;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.vip.VipCertificationNotifyEntry;
import com.chsdk.api.LoginCallBack;
import com.chsdk.biz.BaseLogic.LogicListener;
import com.chsdk.biz.login.AutoCreateAccountTask;
import com.chsdk.biz.login.AutoCreateAccountTask.TaskListener;
import com.chsdk.biz.login.LoginLogic;
import com.chsdk.configure.UserDBHelper;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.ui.h5.LocalH5Activity;
import com.chsdk.ui.login.UserListAdapter.DeleteListener;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.LoadingDialog;
import com.chsdk.utils.CryptionUtil;
import com.chsdk.utils.DeviceUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * 2016/8/17
 * 
 * @author CXK
 *
 */
public class LoginDialog extends BaseLoginDialog implements OnClickListener {
	private static final String SPECIAL_PWD = "###pwd###";
	private Button ch_dialog_entergame;
	private EditText ch_dialog_username, ch_dialog_password;
	private TextView ch_dialog_forget_password, ch_dialog_fastregist;
	private ImageView ch_dialog_pull_down_logo;
	private PopupWindow popupWindow;
	private UserListAdapter myBaseAdapter;
	private ListView lv_group;
	private RelativeLayout ch_dialog_rl_fast;
	private List<LoginUserInfo> listUsers;
	private String lastLoginPasswd;

	public LoginDialog(Activity activity, LoginCallBack callback) {
		super(activity, callback, R.layout.ch_dialog_login);
		if (!EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().register(this);
		}
	}

	@Override
	public void dismiss() {
		super.dismiss();
		EventBus.getDefault().unregister(this);
	}

	@Override
	public void onClick(View v) {
		if (v == ch_dialog_fastregist) {
//			doFastRegister();
			new PhoneRegisterDialog(activity, callback).show();
			dismiss();
		} else if (v == ch_dialog_entergame) {
			enterGameLogin();
		} else if (v == ch_dialog_forget_password) {
			LocalH5Activity.start(activity, "ForgetPwd");
		} else if (v == ch_dialog_pull_down_logo) {
			ch_dialog_pull_down_logo.setImageResource(R.drawable.ch_dialog_pull_up);
			showPopUp(v);
		}
	}

	@Subscribe
	public void enterGameForVipCertification(VipCertificationNotifyEntry entry) {
		if (entry != null) {
			enterGameNotifyLogin();
		}
	}

	private void enterGameNotifyLogin() {
		String username = ch_dialog_username.getText().toString().trim();
		String password = ch_dialog_password.getText().toString().trim();

		if (TextUtils.isEmpty(username)) {
			return;
		}

		if (TextUtils.isEmpty(password)) {
			return;
		}

		DeviceUtil.hideSoftInput(activity.getApplicationContext(), ch_dialog_password);
		if (SPECIAL_PWD.equals(password)) {
			password = lastLoginPasswd;
		} else {
			password = CryptionUtil.encodeMd5(password);
		}
		doLogin(username, password);
	}

	private void enterGameLogin() {
		String username = ch_dialog_username.getText().toString().trim();
		String password = ch_dialog_password.getText().toString().trim();

		if (TextUtils.isEmpty(username)) {
            CHToast.show(activity, "用户名不能为空");
            return;
        }

		if (TextUtils.isEmpty(password)) {
            CHToast.show(activity, "密码不能为空");
            return;
        }

		DeviceUtil.hideSoftInput(activity.getApplicationContext(), ch_dialog_password);
		if (SPECIAL_PWD.equals(password)) {
            password = lastLoginPasswd;
        } else {
            password = CryptionUtil.encodeMd5(password);
        }
		doLogin(username, password);
	}

	private void showPopUp(View v) {
		View contentView = LayoutInflater.from(activity).inflate(R.layout.ch_dialog_login_pop_list,
				null);
		lv_group = (ListView) contentView.findViewById(R.id.ch_pop_list);

		myBaseAdapter = new UserListAdapter(activity, listUsers, new DeleteListener() {
			@Override
			public void delete(String userName, boolean noMoreUser) {
				String inputUserName = ch_dialog_username.getText().toString();
				if (!TextUtils.isEmpty(inputUserName)) {
					if (inputUserName.equals(userName)) {
						ch_dialog_username.setText("");
						ch_dialog_password.setText("");
					}
				}
				if (popupWindow != null && popupWindow.isShowing()) {
					popupWindow.dismiss();
				}
				
				if (noMoreUser)
					ch_dialog_pull_down_logo.setVisibility(View.GONE);
			}
		});
		lv_group.setAdapter(myBaseAdapter);
		
		popupWindow = new PopupWindow(contentView, ch_dialog_rl_fast.getWidth(),
				ViewGroup.LayoutParams.WRAP_CONTENT);
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(false);
		popupWindow.setBackgroundDrawable(new ColorDrawable(Color.argb(50, 52, 53, 55)));
		ch_dialog_pull_down_logo.setImageResource(R.drawable.ch_dialog_pull_up);
		popupWindow.showAsDropDown(ch_dialog_rl_fast);
		popupWindow.update();
		popupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				ch_dialog_pull_down_logo
						.setImageResource(R.drawable.ch_dialog_register_pull_down_logo);
			}
		});

		lv_group.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String name = listUsers.get(position).userName;
				ch_dialog_username.setText(name);
				lastLoginPasswd = listUsers.get(position).passwd;
				if (!TextUtils.isEmpty(lastLoginPasswd)) {
					ch_dialog_password.setText(SPECIAL_PWD);
				}
				if (popupWindow != null) {
					popupWindow.dismiss();
				}
			}
		});
	}

	private void doFastRegister() {
		String autoUserName = session.getAutoUserName();
		if (TextUtils.isEmpty(autoUserName)) {
			final LoadingDialog loadDialog = new LoadingDialog(activity, "正在生成账号");
			loadDialog.show();

			AutoCreateAccountTask task = new AutoCreateAccountTask(new TaskListener() {
				@Override
				public void finished(String msg) {
					loadDialog.dismiss();
					dismiss();

					if (TextUtils.isEmpty(msg)) {
						CHToast.show(activity, "自动生成账号失败, 请手动输入");
					} else {
						session.setAutoUserName(msg);
					}

					new FastRegisterDialog(activity, msg, callback).show();
				}
			});
			task.execute();
		} else {
			dismiss();
			new FastRegisterDialog(activity, autoUserName, callback).show();
		}
	}

	private void doLogin(final String userName, String passwd) {
		LoginLogic logic = new LoginLogic(activity, new LogicListener() {

			@Override
			public void success(String... result) {
				dismiss();
				callbackSuccess(userName, session.getUserId(), session.getToken());
			}

			@Override
			public void failed(String error) {
				CHToast.show(activity, "进入失败：" + error);
				callbackFailed(error);
			}
		});
		logic.loginNormal(userName, passwd);
	}

	@Override
	protected void initDialog() {
		setCornerRound();

		ch_dialog_fastregist = getView(R.id.ch_dialog_fastregist);
		ch_dialog_entergame = getView(R.id.ch_dialog_entergame);
		ch_dialog_username = getView(R.id.ch_dialog_username);
		ch_dialog_password = getView(R.id.ch_dialog_password);
		ch_dialog_forget_password = getView(R.id.ch_dialog_forget_password);
		ch_dialog_pull_down_logo = getView(R.id.ch_dialog_pull_down_logo);
		ch_dialog_rl_fast = getView(R.id.ch_dialog_rl_fast);

		ch_dialog_username.addTextChangedListener(textWatcher);

		ch_dialog_fastregist.setOnClickListener(this);
		ch_dialog_entergame.setOnClickListener(this);
		ch_dialog_forget_password.setOnClickListener(this);
		ch_dialog_pull_down_logo.setOnClickListener(this);
		ch_dialog_password.setOnEditorActionListener(new EditText.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					ch_dialog_entergame.performClick();
				}
				return false;
			}
		});
		
		listUsers = UserDBHelper.getUserList(activity);
		if (listUsers == null || listUsers.size() == 0) {
			ch_dialog_pull_down_logo.setVisibility(View.GONE);
		} else {
			String lastLoginUser = listUsers.get(0).userName;
			lastLoginPasswd = listUsers.get(0).passwd;
			ch_dialog_username.setText(lastLoginUser);
			if (!TextUtils.isEmpty(lastLoginPasswd)) {
				ch_dialog_password.setText(SPECIAL_PWD);
			}
		}
	}
	private TextWatcher textWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			ch_dialog_password.setText("");
		}
	};
}
