package com.caohua.games.ui.account;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caohua.games.R;

/**
 * Created by CXK on 2016/11/18.
 */

public class PayGridViewItem extends LinearLayout {
    private TextView tv;
    private CallBack mCallback;
    private int value;

    public PayGridViewItem(Context context) {
        super(context);
        loadXml();
    }

    public PayGridViewItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadXml();
    }

    public PayGridViewItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadXml();
    }

    private void loadXml() {
        LayoutInflater.from(getContext()).inflate(R.layout.ch_pay_gridview_item, this, true);
        tv = (TextView) findViewById(R.id.textView_pay);
        tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setSelected(true);
                if (mCallback != null) {
                    mCallback.work(value);
                }
            }
        });
    }

    public void setData(Integer list) {
        value = list;
        tv.setText(list + "元");
    }

    public void cancelSelect() {
        tv.setSelected(false);
    }

    public interface CallBack {
        void work(int value);
    }

    public void setCallBack(CallBack callBack) {
        this.mCallback = callBack;
    }

    public void doWork(int value) {
        mCallback.work(value);
    }

}
