package com.caohua.games.ui.coupon;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.caohua.games.R;
import com.caohua.games.biz.coupon.FlyingCardDialogEntry;
import com.caohua.games.biz.coupon.FlyingCardDialogLogic;
import com.caohua.games.biz.coupon.FlyingCardSuccessDialog;
import com.chsdk.biz.BaseLogic;
import com.chsdk.ui.login.PicPushDialog;
import com.chsdk.ui.widget.BaseAlertDialog;
import com.chsdk.ui.widget.CHAlertDialog;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.LoadingDialog;

/**
 * Created by admin on 2017/8/22.
 */

public class FlyingCardDialog extends BaseAlertDialog {

    private String cardId;
    private int defaultIcon;
    private Bitmap bitmap;
    private Activity context;
    private ImageView imgPush;
    private ImageView imgClose;

    public FlyingCardDialog(Activity context, int defaultIcon, String cardId, Bitmap bitmap) {
        super(context, R.style.ch_base_style);
        this.context = context;
        this.bitmap = bitmap;
        this.defaultIcon = defaultIcon;
        this.cardId = cardId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch_dialog_flying_card);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        initView();
    }

    private void initView() {
        imgPush = getView(R.id.ch_dialog_push_img);
        if (bitmap == null) {
            imgPush.setImageResource(defaultIcon);
        } else {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            imgPush.setImageBitmap(bitmap);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) imgPush.getLayoutParams();
            lp.width = width;
            lp.height = height;
            imgPush.setLayoutParams(lp);
        }
        imgClose = getView(R.id.ch_dialog_push_close);
        imgPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LoadingDialog dialog = new LoadingDialog(context, "");
                dialog.show();
                new FlyingCardDialogLogic().cardDialog(cardId, new BaseLogic.DataLogicListner<FlyingCardDialogEntry>() {
                    @Override
                    public void failed(String errorMsg, int errorCode) {
                        dialog.dismiss();
                        CHToast.show(context, errorMsg);
                        synchronized (PicPushDialog.class) {
                            dismiss();
                        }
                    }

                    @Override
                    public void success(FlyingCardDialogEntry entry) {
                        dialog.dismiss();
                        if (entry != null) {
                            showAwardDialog(true, entry.success_tips, entry.reward_desc);
                        }

                        synchronized (PicPushDialog.class) {
                            dismiss();
                        }
                    }
                });
            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronized (PicPushDialog.class) {
                    dismiss();
                }
            }
        });
    }

    private void showAwardDialog(final boolean success, String content, String content2) { //ok = "如何满足要求" "关闭"

        final FlyingCardSuccessDialog successDialog = new FlyingCardSuccessDialog(context);
        successDialog.show();
        successDialog.cancelToGone();
        successDialog.setContent(content);
        if (!TextUtils.isEmpty(content2)) {
            successDialog.setContent2(content2);
        }
        successDialog.setTitle("提示！");
        String cancel = "关闭";
        String ok = "我知道了";
        AlertDialog dialog = successDialog.getDialog();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        successDialog.setOkButton(ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                successDialog.dismiss();
            }
        });

        successDialog.setCancelButton(cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                successDialog.dismiss();
            }
        });
    }
}
