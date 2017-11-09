package com.caohua.games.ui.vip;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.vip.VipAuthSubmitEntry;
import com.chsdk.utils.ViewUtil;

public class VipCertificationDialog {
    private VipAuthSubmitEntry entry;
    private Activity activity;
    private AlertDialog dialog;
    private boolean outSide;
    private boolean cancelable;
    private TextView levelText;
    private TextView name;
    private TextView left;
    private TextView right;

    public VipCertificationDialog(Activity activity, VipAuthSubmitEntry entry) {
        this.activity = activity;
        this.entry = entry;
    }

    public void dismiss() {
        if (dialog != null)
            dialog.dismiss();
    }

    public void show() {
        dialog = new AlertDialog.Builder(activity, R.style.ch_base_style).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        Display display = activity.getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        //设置dialog的宽高为屏幕的宽高
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
        View view = LayoutInflater.from(activity).inflate(R.layout.ch_dialog_vip_certification, null);
        dialog.setContentView(view, layoutParams);

        initView(view);
    }

    private void initView(View view) {
        levelText = ViewUtil.getView(view, R.id.ch_vip_certification_level_text);
        name = ViewUtil.getView(view, R.id.ch_vip_certification_name);
        left = ViewUtil.getView(view, R.id.ch_vip_certification_left);
        right = ViewUtil.getView(view, R.id.ch_vip_certification_right);
        View btn = ViewUtil.getView(view, R.id.ch_vip_certification_btn);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CHVipActivity.startForDialog(activity);
                dismiss();
            }
        });
        if (entry != null) {
            left.setText("VIP值" + entry.vip_exp);
            right.setText("还差" + (s2int(entry.next_exp) - s2int(entry.vip_exp)) + "升级");
            name.setText(entry.nickname);
            levelText.setText("VIP" + entry.vip_level);
        }
    }

    public long s2int(String value) {
        long i = 0;
        try {
            i = Long.parseLong(value);
        } catch (Exception e) {
        }
        return i;
    }

    public AlertDialog getDialog() {
        return dialog;
    }
}
