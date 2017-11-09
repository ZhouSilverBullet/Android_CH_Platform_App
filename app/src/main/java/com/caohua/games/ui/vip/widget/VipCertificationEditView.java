package com.caohua.games.ui.vip.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caohua.games.R;

/**
 * Created by admin on 2017/8/24.
 */

public class VipCertificationEditView extends RelativeLayout implements Handler.Callback {
    private Context context;
    private String editNameValue;
    private boolean showBtnBoolean;
    private TextView editNameText;
    private EditText editValueText;
    private Button editBtn;
    private String editValueHintText;
    private boolean isNumberInputType;
    private Handler mHandler = new Handler(this);

    OnSendCodeListener sendCodeListener;
    private boolean isMulti;
    private TextView editNameText2;

    public interface OnSendCodeListener {
        void onSendCode(View v);
    }

    public void setSendCodeListener(OnSendCodeListener sendCodeListener) {
        this.sendCodeListener = sendCodeListener;
    }

    public VipCertificationEditView(Context context) {
        this(context, null);
    }

    public VipCertificationEditView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VipCertificationEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.VipCertificationEditView, defStyleAttr, 0);
        editNameValue = a.getString(R.styleable.VipCertificationEditView_editName);
        editValueHintText = a.getString(R.styleable.VipCertificationEditView_editValueHint);
        showBtnBoolean = a.getBoolean(R.styleable.VipCertificationEditView_showBtn, false);
        isNumberInputType = a.getBoolean(R.styleable.VipCertificationEditView_isNumberInputType, false);
        isMulti = a.getBoolean(R.styleable.VipCertificationEditView_isMultiText, false);
        a.recycle();
        init();
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.ch_vip_certification_edit_view, this, true);
        editNameText = (TextView) findViewById(R.id.ch_activity_vip_certification_edit_name);
        editNameText2 = (TextView) findViewById(R.id.ch_activity_vip_certification_edit_name_2);
        editValueText = (EditText) findViewById(R.id.ch_activity_vip_certification_edit_value);
        editValueText.setHint(editValueHintText);
        editBtn = (Button) findViewById(R.id.ch_activity_vip_certification_edit_btn);
        if (isNumberInputType) {
            editValueText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        if (isMulti) {
            editValueText.setGravity(Gravity.TOP);
            editValueText.setMinLines(3);
            editValueText.setImeOptions(EditorInfo.IME_ACTION_DONE);
            editValueText.setLines(3);
            editNameText2.setVisibility(VISIBLE);
            editNameText2.setText(editNameValue);
            editNameText.setText(editNameValue);
            editNameText.setVisibility(View.INVISIBLE);
        } else {
            editNameText.setText(editNameValue);
            editValueText.setSingleLine();
        }
        if (showBtnBoolean) {
            editBtn.post(new Runnable() {
                @Override
                public void run() {
                    int width = editBtn.getWidth();
                    RelativeLayout.LayoutParams layoutParams = (LayoutParams) editValueText.getLayoutParams();
                    layoutParams.rightMargin = width;
                    editValueText.setLayoutParams(layoutParams);
                }
            });
            editBtn.setVisibility(VISIBLE);
            editBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sendCodeListener != null) {
                        sendCodeListener.onSendCode(v);
                    }
                }
            });
        }
    }

    /**
     * 判断手机不为空时，调用该方法
     * 进行按钮验证码控制
     */
    public void doCodeStatus() {
        times = 0;
        editBtn.setClickable(false);
        mHandler.sendEmptyMessageDelayed(100, 1000);
    }

    public String getEditValueText() {
        return editValueText.getText().toString().trim();
    }

    int times;

    @Override
    public boolean handleMessage(Message msg) {
        times = times + 1;
        int total = 59 - times;
        if (total == 0) {
            editBtn.setText("发送");
            editBtn.setClickable(true);
            mHandler.removeMessages(100);
            return true;
        }
        if (msg.what == 100) {
            mHandler.sendEmptyMessageDelayed(100, 1000);
            editBtn.setText("发送" + total);
        }
        return true;
    }
}
