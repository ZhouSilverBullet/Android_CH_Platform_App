package com.chsdk.ui.widget;

import android.content.Context;
import android.widget.Toast;

/**
 * @author ZengLei
 *         <p>
 * @version 2016年8月16日
 *          <p>
 */
public class CHToast {
	private static Toast mToast;
	public static void show(Context context, String msg) {
		show(context, msg, Toast.LENGTH_SHORT);
	}

	public static void show(Context context, String msg, int duration) {
		if (mToast == null) {
			mToast = Toast.makeText(context, msg, duration);
		} else {
			mToast.setText(msg);
		}
		mToast.show();
	}

    public static void cancel() {
        if (mToast != null) {
            mToast.cancel();
        }
    }
}
