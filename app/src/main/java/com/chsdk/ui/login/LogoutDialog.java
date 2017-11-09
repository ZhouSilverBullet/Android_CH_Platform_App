package com.chsdk.ui.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.caohua.games.R;
import com.caohua.games.ui.StoreSecondActivity;
import com.chsdk.biz.BaseLogic.LogicListener;
import com.chsdk.biz.login.LogoutLogic;
import com.chsdk.configure.DataStorage;
import com.chsdk.configure.SdkSession;
import com.chsdk.ui.WebActivity;
import com.chsdk.utils.ViewUtil;

public class LogoutDialog implements OnClickListener {
	private Activity activity;
	private AlertDialog dialog;
	private Button exitGame, downApp;
	private LogicListener listener;
	private ImageView adImg, imgFake;

	public LogoutDialog(Activity activity, LogicListener listener) {
		this.activity = activity;
		this.listener = listener;
	}

	public void dismiss() {
		if (dialog != null)
			dialog.dismiss();
	}

	public void showDialog() {
		dialog = new AlertDialog.Builder(activity, R.style.ch_translucent_style).create();
		dialog.show();
		View view = LayoutInflater.from(activity).inflate(
				R.layout.ch_dialog_logout, null);
		dialog.setContentView(view);
		initView(view);
		setOnClick();
		setAd();
	}

	private void initView(View view) {
		exitGame = ViewUtil.getView(view, R.id.ch_dialog_logout_continue_exit);
		downApp = ViewUtil.getView(view, R.id.ch_dialog_logout_down_app);
		adImg = ViewUtil.getView(view, R.id.ch_dialog_logout_ad_img);
		imgFake = ViewUtil.getView(view, R.id.ch_dialog_logout_ad_img_fake);
	}

	private void setOnClick() {
		exitGame.setOnClickListener(this);
		downApp.setOnClickListener(this);
		adImg.setOnClickListener(this);
	}

    @Override
    public void onClick(View v) {
        if (v == exitGame) {
            LogoutLogic logic = new LogoutLogic(activity, new LogicListener() {

                @Override
                public void success(String... result) {
                    dismiss();
                    if (listener != null) {
                        listener.success(result);
                    }
                }

                @Override
                public void failed(String errorMsg) {
                    dismiss();
                    if (listener != null) {
                        listener.failed(errorMsg);
                    }
                }
            });
            logic.logout();
        } else if (v == downApp) {
            dialog.dismiss();
        } else if (v == adImg) {
            String link = DataStorage.getAdPicLink(activity);
            if (TextUtils.isEmpty(link)) {
                link = "http://wap.caohua.com/Index.aspx"; // 默认跳转地址
            }
            Intent webIntent = null;
            Context context = v.getContext();
            if (!TextUtils.isEmpty(link) && link.contains(StoreSecondActivity.SHOP_URL)) {
                webIntent = new Intent(context, StoreSecondActivity.class);
                webIntent.putExtra("url", link);
                webIntent.putExtra("type", 1);
                context.startActivity(webIntent);
            } else {
                WebActivity.startWebPage(activity, link);
            }
            dialog.dismiss();
        }
    }

    private void setAd() {
        String url = DataStorage.getAdPicUrl(activity);
        if (!TextUtils.isEmpty(url)) {
            Context context = SdkSession.getInstance().getAppContext();
            if (context == null) {
                context = activity;
            }

            if (context != null) {
                Glide.with(context).load(url).dontAnimate().error(R.drawable.ch_dialog_update_icon).listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        imgFake.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        imgFake.setVisibility(View.GONE);
                        return false;
                    }
                }).into(adImg);
            }
        }
    }
}
