package com.caohua.games.ui.account;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.account.AccountLogic;
import com.caohua.games.biz.account.AccountModel;
import com.chsdk.api.AppOperator;
import com.chsdk.configure.UserDBHelper;
import com.chsdk.http.IRequestListener;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.LoadingDialog;

import java.util.HashMap;

import static com.caohua.games.R.styleable.AccountSettingItemEdit;

/**
 * Created by CXK on 2016/11/7.
 */

public class AccountSettingEditView extends LinearLayout {
    private Drawable iconId;
    private String des;
    private String titleOne;
    private TextView title;
    private EditText editText;
    private ImageView icon;
    private OnEditClickListener listener;
    private int type;
    private String lastValue;

    public AccountSettingEditView(Context context) {
        super(context);
        loadXml(null);
    }

    public AccountSettingEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadXml(attrs);
    }

    public AccountSettingEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadXml(attrs);
    }

    private void loadXml(AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.ch_activity_account_item_edit, this, true);
        init();
        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, AccountSettingItemEdit);
            iconId = array.getDrawable(R.styleable.AccountSettingItemEdit_Ac_edit_icon);
            des = array.getString(R.styleable.AccountSettingItemEdit_Ac_edit_des);
            titleOne = array.getString(R.styleable.AccountSettingItemEdit_Ac_edit_title);
            type = array.getInt(R.styleable.AccountSettingItemEdit_Ac_edit_type, 0);
            array.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    private void init() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        title = (TextView) findViewById(R.id.ch_activity_account_edit_tv);
        icon = (ImageView) findViewById(R.id.ch_activity_account_edit_icon);
        editText = (EditText) findViewById(R.id.ch_activity_account_edit_string);
        editText.setText(des);
        icon.setImageDrawable(iconId);
        title.setText(titleOne);
        final EditFocusListener editFocusListener = new EditFocusListener();
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.editClick();
                }
            }
        });
        setOnFocusChangeListener(editFocusListener);
        editText.setOnFocusChangeListener(editFocusListener);
        editText.setLongClickable(false);
    }

    class EditFocusListener implements OnFocusChangeListener {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                if ("@#@".equals(lastValue)) {
                    editText.setText("");
                }

                if (type == 3) {
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                }

                editText.setFocusable(true);
                editText.setFocusableInTouchMode(true);
                editText.requestFocus();
                editText.findFocus();

                icon.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String s = editText.getText().toString();
                        if (TextUtils.isEmpty(s)) {
                            CHToast.show(getContext(), "输入不能为空");
                            return;
                        }

                        AccountModel model = new AccountModel();
                        if (type == 1) {
                            model.setNickName(s);
                        } else if (type == 2) {
                            model.setUserAderess(s);
                        } else if (type == 3) {
                            if (s.length() < 5 || s.length() > 11) {
                                CHToast.show(getContext(), "请输入5-11位正确的QQ号码");
                            }
                            if (s.length() >= 5 && s.length() <= 11) {
                                model.setUserQQ(s);
                            }
                        }
                    }
                });
            }
        }
    }

    public static interface OnEditClickListener {
        void editClick();
    }

    public void setOnEditClickListener(OnEditClickListener listener) {
        this.listener = listener;
    }

    public void setEditText(String editValue) {
        if (TextUtils.isEmpty(editValue)) {
            editText.setText("未设置");
            lastValue = "@#@";
        } else {
            editText.setText(editValue);
            lastValue = editValue;
        }
    }

    public String getEditTextValue() {
        return editText.getText().toString().trim();
    }

    public int getType() {
        return type;
    }
}
