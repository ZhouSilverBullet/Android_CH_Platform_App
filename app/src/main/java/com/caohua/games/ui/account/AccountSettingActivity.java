package com.caohua.games.ui.account;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.account.AccountLogic;
import com.caohua.games.biz.account.AccountModel;
import com.caohua.games.ui.BaseActivity;
import com.chsdk.api.AppOperator;
import com.chsdk.biz.web.UrlOperatorHelper;
import com.chsdk.configure.UserDBHelper;
import com.chsdk.http.IRequestListener;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.LoadingDialog;
import com.chsdk.utils.LogUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by CXK on 2016/10/18.
 */

public class AccountSettingActivity extends BaseActivity {
    public static boolean showLoginDialog;

    private static final String SEX_MAN = "1";
    private static final String SEX_WOMEN = "2";

    private AccountSettingTextView sexItem, dateItem, accountNm, safeSetting;
    private AccountSettingEditView userNickName, QQItem, userAdress;
    private LinearLayout layout;
    private RadioGroup sexCheck;
    private Button button, dismiss;
    private String sexValue = SEX_MAN;
    private String address, birthDay, nickName, qq, userName, sex;
    private View submitButton;
    private String time;
    private String lastNameValue;
    private String lastAddressValue;
    private String lastQqValue;
    private AccountModel model;
    private boolean submitButtonSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch_activity_accountsetting);
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (showLoginDialog) {
            finish();
        }
    }

    private void init() {
        safeSetting = getView(R.id.ch_activity_setting_safe);
        sexItem = getView(R.id.ch_account_setting_item_sex);
        accountNm = getView(R.id.ch_account_setting_item_account_number);
        dateItem = getView(R.id.ch_account_setting_item_date);
        userNickName = getView(R.id.ch_activity_accountsetting_name);
        QQItem = getView(R.id.ch_account_setting_item_QQ);
        userAdress = getView(R.id.ch_account_setting_item_adress);
        submitButton = getView(R.id.ch_activity_setting_submit_btn);

        final LoginUserInfo userInfo = AppContext.getAppContext().getUser();
        if (userInfo == null) {
            finish();
            CHToast.show(this, "当前账户信息出错,请重新登录");
            return;
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (time == null) {
                    time = userInfo.birthDay;
                }

                String qqValue = QQItem.getEditTextValue();
                if (qqValue == null) {
                    lastQqValue = userInfo.qq;
                } else {
                    if ("未设置".equals(qqValue) || "".equals(qqValue)) {
                        lastQqValue = "";
                    } else {
                        lastQqValue = qqValue;
                    }
                }
                String addressValue = userAdress.getEditTextValue();
                if (addressValue == null) {
                    lastAddressValue = userInfo.address;
                } else {
                    if ("未设置".equals(addressValue) || "".equals(addressValue)) {
                        lastAddressValue = "";
                    } else {
                        lastAddressValue = addressValue;
                    }
                }
                final String tempValue;
                if (sexValue == null) {
                    tempValue = "";
                } else if (SEX_MAN.equals(sexValue)) {
                    tempValue = "男";
                } else if (SEX_WOMEN.equals(sexValue)) {
                    tempValue = "女";
                } else {
                    tempValue = "";
                }

                String nameValue = userNickName.getEditTextValue();
                if (nameValue == null) {
                    lastNameValue = userInfo.nickName;
                } else {
                    if ("未设置".equals(nameValue) || "".equals(nameValue)) {
                        lastNameValue = "";
                    } else {
                        lastNameValue = nameValue;
                    }
                }

                if (model == null) {
                    model = new AccountModel();
                }

                if (TextUtils.isEmpty(lastNameValue)) {
                    CHToast.show(getApplicationContext(), "昵称不能为空！");
                    return;
                }

                final LoadingDialog loadingDialog = new LoadingDialog(AccountSettingActivity.this, "");
                loadingDialog.show();

                if (submitButtonSuccess && model.equalsValue(sexValue, lastAddressValue, time, lastQqValue, lastNameValue)) {
                    loadingDialog.dismiss();
                    CHToast.show(getApplicationContext(), "您没有做任何改动！");
                    return;
                }
                model = new AccountModel();
                model.setUserSex(sexValue);
                model.setUserAderess(lastAddressValue);
                model.setUserBirthday(time);
                model.setUserQQ(lastQqValue);
                model.setNickName(lastNameValue);
                AccountLogic logic = new AccountLogic(AccountSettingActivity.this);
                logic.updataInfo(model, new IRequestListener() {
                    @Override
                    public void success(HashMap<String, String> map) {
                        submitButtonSuccess = true;
                        loadingDialog.dismiss();
                        CHToast.show(getApplicationContext(), "设置成功");
                        updateaDb(false, sexValue);
                        updateaDb(true, time);
                        updateaDb(userAdress.getType());
                        updateaDb(QQItem.getType());
                        updateaDb(userNickName.getType());
                    }

                    @Override
                    public void failed(String error, int errorCode) {
                        submitButtonSuccess = false;
                        loadingDialog.dismiss();
                        CHToast.show(getApplicationContext(), "设置失败:" + error);
                    }
                });
            }
        });

        address = userInfo.address;
        birthDay = userInfo.birthDay;
        nickName = userInfo.nickName;
        qq = userInfo.qq;
        userName = userInfo.userName;
        sex = String.valueOf(userInfo.sex);

        accountNm.setTextValue(userName);
        accountNm.setTextColor(Color.GRAY);
        userNickName.setEditText(nickName);
        userAdress.setEditText(address);
        QQItem.setEditText(qq);
        setDate(birthDay);
        if (sex.equals("0")) {
            sexItem.setTextValue("未设置");
        } else if (sex.equals(SEX_MAN)) {
            sexItem.setTextValue("男");
        } else if (sex.equals(SEX_WOMEN)) {
            sexItem.setTextValue("女");
        }

        dateItem.setOnTextClick(new AccountSettingTextView.onTextClickListener() {
            @Override
            public void textClick() {
                showDateDialog();
            }
        });
        sexItem.setOnTextClick(new AccountSettingTextView.onTextClickListener() {
            @Override
            public void textClick() {
                showSexPopupWindow();
            }
        });

        safeSetting.setOnTextClick(new AccountSettingTextView.onTextClickListener() {
            @Override
            public void textClick() {
                WebActivity.startAppLink(AccountSettingActivity.this, "https://app-" + UrlOperatorHelper.ACCOUNT_SAFE_SETTING);
            }
        });
    }

    private AlertDialog dialog;

    private void showSexPopupWindow() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        View view = LayoutInflater.from(this).inflate(R.layout.ch_dialog_cheack_box, null, false);
        dialog = new AlertDialog.Builder(this).create();
        dialog.show();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        window.setContentView(view);
        layout = (LinearLayout) view.findViewById(R.id.ch_dialog_cheack_layout);
        sexCheck = (RadioGroup) view.findViewById(R.id.ch_sex);
        dismiss = (Button) view.findViewById(R.id.ch_dismiss);
        RadioButton btnMan = (RadioButton) view.findViewById(R.id.ch_man);
        RadioButton btnWomen = (RadioButton) view.findViewById(R.id.ch_women);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        if (sex.equals(SEX_WOMEN)) {
            btnMan.setChecked(false);
            btnWomen.setChecked(true);
        } else {
            btnMan.setChecked(true);
            btnWomen.setChecked(false);
        }
        button = (Button) view.findViewById(R.id.ch_button);
        sexCheck.setOnCheckedChangeListener(new OnCheckedChangeListenerImp());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                String value = "未设置";
                if (sexValue.equals(SEX_MAN)) {
                    value = "男";
                } else if (sexValue.equals(SEX_WOMEN)) {
                    value = "女";
                }

                if (sexItem.getText().equals(value)) {
                    return;
                }

                sexItem.setTextValue(value);
            }
        });
        layout.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                }
                return false;
            }
        });
    }

    private void showDateDialog() {
        final Calendar calendar = Calendar.getInstance();
        if (!TextUtils.isEmpty(birthDay)) {
            try {
                calendar.setTime(new Date(Long.valueOf(birthDay)));
            } catch (Exception e) {
            }
        }

        DatePickDialog dialog = new DatePickDialog(AccountSettingActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {
                calendar.set(year, monthOfYear, dayOfMonth, 0, 0);
                time = String.valueOf(calendar.getTimeInMillis());
                String unixTime = getUnixTime(time);
                dateItem.setTextValue(unixTime);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
    }

    private String getUnixTime(String time) {
        if (time == null) {
            return "";
        }
        String formats = "yyyy-MM-dd";
        Long timestamp = Long.parseLong(time);
        return new SimpleDateFormat(formats, Locale.CHINA).format(new Date(timestamp));
    }

    private void setDate(String time) {
        if (TextUtils.isEmpty(time)) {
            dateItem.setTextValue("未设置");
        } else {
            Calendar calendar = Calendar.getInstance();
            try {
                calendar.setTime(new Date(Long.valueOf(time)));
            } catch (Exception e) {
            }
            dateItem.setTextValue(calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH));
        }
    }

    private void updateaDb(final int type) {
        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                LoginUserInfo info = AppContext.getAppContext().getUser();
                if (type == 1) {
                    // nickname
                    info.nickName = lastNameValue;
                } else if (type == 2) {
                    // add
                    info.address = lastAddressValue;
                } else if (type == 3) {
                    // qq
                    info.qq = lastQqValue;
                }
                UserDBHelper.updateUser(getApplicationContext(), info);
            }
        });
    }

    private void updateaDb(final boolean dateUpdate, final String value) {
        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                LoginUserInfo info = AppContext.getAppContext().getUser();
                if (dateUpdate) {
                    info.birthDay = value;
                } else {
                    info.sex = Integer.valueOf(value);
                }
                UserDBHelper.updateUser(getApplicationContext(), info);
            }
        });
    }

    class DatePickDialog extends DatePickerDialog {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }

        public DatePickDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
            super(context, callBack, year, monthOfYear, dayOfMonth);
        }

        @Override
        protected void onStop() {
            //super.onStop();
        }
    }

    class OnCheckedChangeListenerImp implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (R.id.ch_man == checkedId) {
                sexValue = SEX_MAN;
            } else if (R.id.ch_women == checkedId) {
                sexValue = SEX_WOMEN;
            }
        }
    }
}
