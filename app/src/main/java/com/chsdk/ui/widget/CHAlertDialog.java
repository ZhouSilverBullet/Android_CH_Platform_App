package com.chsdk.ui.widget;

import com.caohua.games.R;
import com.chsdk.utils.ViewUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class CHAlertDialog {
	private Activity activity;
	private Dialog dialog;
	private Button btnOk, btnCancle;
	private TextView tvTitle, tvContent;
	private boolean outSide;
	private boolean cancelable;

	public CHAlertDialog(Activity activity) {
		this(activity, false, false);
	}

	public CHAlertDialog(Activity activity, boolean outSide, boolean cancelable) {
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
		
		View view = LayoutInflater.from(activity).inflate(R.layout.ch_dialog_recommend_app, null);
		dialog.setContentView(view);
		
		initView(view);
	}

	public void setTitle(String title) {
		tvTitle.setText(title);
	}

	public void setContent(String content) {
		tvContent.setText(content);
	}

	public void setOkButton(String btnName, OnClickListener listener) {
		btnOk.setText(btnName);
		if (listener != null) {
			btnOk.setOnClickListener(listener);
		} 
	}

	public void setCancelButton(String btnName, OnClickListener listener) {
		btnCancle.setText(btnName);
		if (listener != null) {
			btnCancle.setOnClickListener(listener);
		}
	}

	private void initView(View view) {
		tvTitle = ViewUtil.getView(view, R.id.ch_dialog_alert_title);
		tvContent = ViewUtil.getView(view, R.id.ch_dialog_alert_content);
		btnOk = ViewUtil.getView(view, R.id.ch_dialog_alert_ok);
		btnCancle = ViewUtil.getView(view, R.id.ch_dialog_alert_cancel);
		btnOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		btnCancle.setOnClickListener(new OnClickListener() {
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
