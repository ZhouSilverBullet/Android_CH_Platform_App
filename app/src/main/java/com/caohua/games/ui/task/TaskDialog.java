package com.caohua.games.ui.task;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.caohua.games.R;
import com.caohua.games.biz.task.TaskEntry;

/**
 * Created by zhouzhou on 2017/4/15.
 */

public abstract class TaskDialog {
    protected Context context;
    protected AlertDialog dialog;

    protected TaskEntry taskEntry;

    public TaskDialog(Context context, TaskEntry taskEntry) {
        this.context = context;
        this.taskEntry = taskEntry;
    }

    protected int stringToInt(String count) {
        if (!TextUtils.isEmpty(count)) {
            return Integer.parseInt(count);
        }
        return 0;
    }

    public void dismiss() {
        if (dialog != null)
            dialog.dismiss();
    }

    public void show() {
        dialog = new AlertDialog.Builder(context, R.style.ch_base_style).create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();

        View view = LayoutInflater.from(context).inflate(getLayoutId(), null);
        dialog.setContentView(view);
        initChildView(view);
    }

    protected abstract void initChildView(View view);

    public abstract int getLayoutId();
}
