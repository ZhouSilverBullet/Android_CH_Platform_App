package com.chsdk.ui.widget;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.caohua.games.R;
import com.chsdk.utils.ViewUtil;

public class CHSingleBtnAlertDialog {
	private Activity activity;
	private Dialog dialog;
	private Button btnCancel;
	private TextView tvTitle, tvContent;
	private boolean outSide;
	private boolean cancelable;

	public CHSingleBtnAlertDialog(Activity activity) {
		this(activity, false, false);
	}

	public CHSingleBtnAlertDialog(Activity activity, boolean outSide, boolean cancelable) {
		this.activity = activity;
		this.outSide = outSide;
		this.cancelable = cancelable;
	}

	public void dismiss() {
		if (dialog != null)
			dialog.dismiss();
	}

	public void show() {
		dialog = new Dialog(activity, R.style.ch_translucent_style);
		dialog.setCanceledOnTouchOutside(outSide);
		dialog.setCancelable(cancelable);
		dialog.show();
		
		View view = LayoutInflater.from(activity).inflate(R.layout.ch_dialog_single_btn_layout, null);
		dialog.setContentView(view);
		
		initView(view);
	}

	public void setTitle(String title) {
		tvTitle.setText(title);
	}

	public void setContent(String content) {
		tvContent.setText(content);
	}

	public void setCancelButton(String btnName, OnClickListener listener) {
		btnCancel.setText(btnName);
		if (listener != null) {
			btnCancel.setOnClickListener(listener);
		}
	}

	private void initView(View view) {
		tvTitle = ViewUtil.getView(view, R.id.ch_dialog_alert_title);
		tvContent = ViewUtil.getView(view, R.id.ch_dialog_alert_content);
		btnCancel = ViewUtil.getView(view, R.id.ch_dialog_alert_cancel);
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}

	public Dialog getDialog() {
		return dialog;
	}
}
