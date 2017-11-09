package com.caohua.games.ui.emoji;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.utils.BPUtil;

/**
 * Created by admin on 2017/1/17.
 */

public class VerificationCodeAlertDialog {
    private Context mContext;
    private VerificationCallback mVerificationCallback;

    public VerificationCodeAlertDialog(Context context) {
        mContext = context;
    }

    public void setVerificationCallback(VerificationCallback verificationCallback) {
        mVerificationCallback = verificationCallback;
    }

    public interface VerificationCallback {
        void onSuccess();
        void onFailure();
    }

    public void showVerificationCodeDialog() {
        LinearLayout inflate = (LinearLayout) LayoutInflater.from(mContext)
                .inflate(R.layout.verification_code_layout, null);
        final ImageView imageView = (ImageView) inflate.findViewById(R.id.ch_verification_code_image);
        TextView submitButton = (TextView) inflate.findViewById(R.id.ch_verification_code_sumbmit);
        TextView cancelButton = (TextView) inflate.findViewById(R.id.ch_verification_code_cancel);
        final EditText codeEditText = (EditText) inflate.findViewById(R.id.ch_verification_code_edit_text);
        imageView.setImageBitmap(BPUtil.getInstance().createBitmap());
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                imageView.setImageBitmap(BPUtil.getInstance().createBitmap());
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.ch_base_style);
        final AlertDialog dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setAttributes(lp);
        window.setContentView(inflate);
        builder.setView(inflate);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BPUtil.getInstance().getCode().equalsIgnoreCase(codeEditText.getText().toString().trim())) {
                    dialog.dismiss();
                    if (mVerificationCallback != null) {
                        mVerificationCallback.onSuccess();
                    }
                } else {
                    if (mVerificationCallback != null) {
                        mVerificationCallback.onFailure();
                    }
                    if ("".equals(codeEditText.getText().toString().trim())) {
                        CHToast.show(mContext, "验证码为空");
                        return;
                    }
                    CHToast.show(mContext, "验证码输入有误");
                    imageView.setImageBitmap(BPUtil.getInstance().createBitmap());
                    codeEditText.setText("");
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                CHToast.show(mContext, "评论未发送！");
            }
        });
    }

}
