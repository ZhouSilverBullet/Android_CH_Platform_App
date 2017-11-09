package com.chsdk.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.ui.BaseActivity;
import com.caohua.games.ui.HomePagerActivity;
import com.chsdk.model.game.UpdateEntry;
import com.chsdk.utils.FileUtil;
import com.chsdk.utils.LogUtil;
import com.chsdk.utils.ViewUtil;

import java.io.File;

import pub.devrel.easypermissions.EasyPermissions;

public class UpdateGameDialog implements OnClickListener {
    private static final String FILE_NAME = "UpdateGame.apk";
    private Activity activity;
    private AlertDialog dialog;
    private Button ch_dialog_update_btn;
    private ImageView ch_dialog_title_exit;
    private UpdateEntry updateEntry;
    private TextView updateContent;

    public UpdateGameDialog(Activity activity, UpdateEntry updateEntry) {
        this.activity = activity;
        this.updateEntry = updateEntry;
    }

    public void showDialog() {
        dialog = new AlertDialog.Builder(activity, R.style.ch_base_style).create();
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
//		Window window = dialog.getWindow();
//		window.clearFlags(LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_ALT_FOCUSABLE_IM);
//		window.setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//
//		LayoutParams lp = dialog.getWindow().getAttributes();
//		lp.dimAmount = 0.3f;
//		window.setAttributes(lp);
//		window.addFlags(LayoutParams.FLAG_DIM_BEHIND);
        View view = LayoutInflater.from(activity).inflate(R.layout.ch_dialog_update, null);
//		window.setContentView(view);
        dialog.setContentView(view);
        initView(view);
    }

    private void initView(View view) {
        ch_dialog_update_btn = ViewUtil.getView(view, R.id.ch_dialog_update_btn);
        ch_dialog_update_btn.setOnClickListener(this);
        ch_dialog_title_exit = ViewUtil.getView(view, R.id.ch_dialog_title_exit);
        if (updateEntry.forceUpdate) {
            ch_dialog_title_exit.setVisibility(View.INVISIBLE);
        }
        ch_dialog_title_exit.setOnClickListener(this);
        updateContent = ViewUtil.getView(view, R.id.ch_dialog_update_content);
        if (!TextUtils.isEmpty(updateEntry.updateState)) {
            String text = updateEntry.updateState.replaceAll("\\|", "\n");
            updateContent.setText(text);
            LogUtil.errorLog("UpdateGameDialog: " + text);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == ch_dialog_update_btn) {
            String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (!EasyPermissions.hasPermissions(activity, perms)) {
                EasyPermissions.requestPermissions(activity, "请授予手机读写权限", HomePagerActivity.PERMISSION_FOR_WRITE_STORAGE, perms);
                return;
            }
            dialog.dismiss();

            String savePath = FileUtil.getCHSdkCacheDirectory(activity) + FILE_NAME;
            DownLoadDialog updateProgress = new DownLoadDialog(activity, updateEntry, savePath);
            updateProgress.showDialog();
        } else if (v == ch_dialog_title_exit) {
            dialog.dismiss();
        }
    }

    public static void deleteApkFile(Context context) {
        String savePath = FileUtil.getCHSdkCacheDirectory(context) + FILE_NAME;
        File file = new File(savePath);
        if (file.exists()) {
            file.delete();
        }
    }
}
