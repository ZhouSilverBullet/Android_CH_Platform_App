package com.caohua.games.ui.account;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.caohua.games.R;

/**
 * Created by CXK on 2016/11/18.
 */

public class PayGridViewEditItem extends LinearLayout {
    private EditText edit;
    private CallBack mCallback;
    private ChangeListener changeListener;

    public PayGridViewEditItem(Context context) {
        super(context);
        loadXml();
    }

    public PayGridViewEditItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadXml();
    }

    public PayGridViewEditItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadXml();
    }

    private void loadXml() {
        LayoutInflater.from(getContext()).inflate(R.layout.ch_pay_gridview_edit, this, true);
        edit = (EditText) findViewById(R.id.textView_pay9);
        edit.addTextChangedListener(changeListener = new ChangeListener());
    }

    public interface CallBack {
        void work(int value);
    }

    public void setCallBack(CallBack callBack) {
        this.mCallback = callBack;
    }

    public void clearText() {
        if (changeListener != null) {
            edit.removeTextChangedListener(changeListener);
        }
        edit.setText("");
        edit.setHint("其他金额");
        edit.clearFocus();
        if (changeListener != null) {
            edit.addTextChangedListener(changeListener);
        }
    }

    class ChangeListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            int value = 0;
            String ss = s.toString().trim();
            int len = ss.length();
            if (len == 1 && ss.equals("0")) {
                s.clear();
            }
            try {
                value = Integer.valueOf(String.valueOf(s));
            } catch (NumberFormatException e) {
            }

            if (mCallback != null)
                mCallback.work(value);
        }
    }

}
