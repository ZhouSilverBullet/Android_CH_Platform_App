package com.chsdk.ui.widget;

import android.content.Context;
import android.os.Bundle;

import com.caohua.games.R;

/**
 * @author ZengLei
 *         <p>
 * @version 2016年8月19日
 *          <p>
 */
public class LoadingDialog extends BaseAlertDialog {

	private static LoadingDialog dialog;

	private boolean isCancelable = false;
	
	public LoadingDialog(Context context, String msg) {
		super(context, R.style.ch_no_bg_style);
		isCancelable = false;
	}

	public LoadingDialog(Context context, boolean isCancelable) {
		super(context, R.style.ch_no_bg_style);
		this.isCancelable = isCancelable;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ch_dialog_loading);
		setCanceledOnTouchOutside(false);
		setCancelable(isCancelable);
	}

	@Override
	public void setMessage(CharSequence message) {
	}

	public static void start(Context context) {
		if (dialog == null) {
			dialog = new LoadingDialog(context, "");
		}
		dialog.show();
	}

	public static void stop() {
		if (dialog != null) {
			dialog.dismiss();
		}
		dialog = null;
	}
}
