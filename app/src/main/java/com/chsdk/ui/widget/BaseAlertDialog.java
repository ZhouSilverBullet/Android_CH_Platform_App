package com.chsdk.ui.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

/** 
* @author  ZengLei <p>
* @version 2016年8月19日 <p>
*/
public class BaseAlertDialog extends AlertDialog{

	protected BaseAlertDialog(Context context) {
		super(context);
	}

	protected BaseAlertDialog(Context context, int theme) {
        super(context, theme);
    }
	
	@SuppressWarnings("unchecked")
	protected <T extends View> T getView(int id) {
		return (T) findViewById(id);
	}
}
