package com.caohua.games.biz.coupon;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.caohua.games.R;
import com.chsdk.utils.ViewUtil;

public class FlyingCardSuccessDialog {
	private Activity activity;
	private AlertDialog dialog;
	private Button btnOk, btnCancle;
	private TextView tvTitle, tvContent;
	private boolean outSide;
	private boolean cancelable;
	private TextView tvContent2;
	private View phoneDivider;

	public FlyingCardSuccessDialog(Activity activity) {
		this(activity, false, false);
	}

	public FlyingCardSuccessDialog(Activity activity, boolean outSide, boolean cancelable) {
		this.activity = activity;
		this.outSide = outSide;
		this.cancelable = cancelable;
	}

	public void dismiss() {
		if (dialog != null)
			dialog.dismiss();
	}

	public void show() {
		dialog = new AlertDialog.Builder(activity, R.style.ch_base_style).create();
		dialog.setCanceledOnTouchOutside(outSide);
		dialog.setCancelable(cancelable);
		dialog.show();
		
		View view = LayoutInflater.from(activity).inflate(R.layout.ch_dialog_flying_success, null);
		dialog.setContentView(view);
		
		initView(view);
	}

	public void setTitle(String title) {
		tvTitle.setText(title);
	}

	public void setContent(String content) {
		tvContent.setText(content);
	}

	public void setContent2(String content2) {
		tvContent2.setVisibility(View.VISIBLE);
		tvContent2.setText(content2);
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
		ViewUtil.setBackground(view, ViewUtil.addBtnBackgroundRound("#f4f4f4", true, ViewUtil.dp2px(activity, 3)));
		tvTitle = ViewUtil.getView(view, R.id.ch_dialog_alert_title);
		tvContent = ViewUtil.getView(view, R.id.ch_dialog_alert_content);
		tvContent2 = ViewUtil.getView(view, R.id.ch_dialog_alert_content_2);
		phoneDivider = ViewUtil.getView(view, R.id.ch_dialog_phone_divider);
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

	public void cancelToGone() {
		btnCancle.setVisibility(View.GONE);
		phoneDivider.setVisibility(View.GONE);
	}

	public AlertDialog getDialog() {
		return dialog;
	}
}
