package com.chsdk.ui.login;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.TransmitDataInterface;
import com.caohua.games.biz.coupon.PushCouponLogic;
import com.caohua.games.ui.StoreSecondActivity;
import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.DataStorage;
import com.chsdk.configure.SdkSession;
import com.chsdk.model.game.PushEntry;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.BaseAlertDialog;
import com.chsdk.ui.widget.CHAlertDialog;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.utils.LogUtil;
import com.chsdk.utils.PicUtil;

/**
 * @author ZengLei
 *         <p>
 * @version 2016年8月19日
 *          <p>
 */
public class PicPushDialog extends BaseAlertDialog {
    private Activity context;
    private ImageView imgPush;
	private ImageView imgClose;
	private PushEntry entry;

	public PicPushDialog(Activity context, PushEntry entry) {
		super(context, R.style.ch_base_style);
        this.context = context;
        this.entry = entry;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ch_dialog_push);
		setCanceledOnTouchOutside(false);
		setCancelable(false);
		initView();
	}

	private void initView() {
		imgPush = getView(R.id.ch_dialog_push_img);
		imgClose = getView(R.id.ch_dialog_push_close);
		imgPush.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				saveClickStatus();
                if (entry.isAward == 1) {
                    if (!AppContext.getAppContext().isLogin()) {
                        AppContext.getAppContext().login(context, new TransmitDataInterface() {
                            @Override
                            public void transmit(Object o) {

                            }
                        });
                        return;
                    }
                    awardCoupon();
                } else {
                    jump();
                }
                synchronized (PicPushDialog.class) {
                    dismiss();
                }
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

        String path = PicUtil.getPushPicPath(getContext(), entry.imgUrl);
        Bitmap bitmap = getLocalPic(path);
        if (bitmap == null) {
            imgPush.post(new Runnable() {
                @Override
                public void run() {
                    dismiss();
                }
            });
            return;
        }

        setImageLayoutParams(entry.width, entry.heigth);
        imgPush.setImageBitmap(bitmap);
    }

    private void awardCoupon() {
        new PushCouponLogic().getCoupon(entry.msgId, new BaseLogic.DataLogicListner() {
            @Override
            public void failed(String errorMsg, int errorCode) {
                if (errorCode == 502) {
                    showAwardDialog(false, "哎哟，发放失败，请到客服中心进行反馈哦", "去反馈");
                } else {
                    CHToast.show(AppContext.getAppContext(), errorMsg);
                }
            }

            @Override
            public void success(Object entryResult) {
                showAwardDialog(true, "优惠券已悄悄藏入您的券袋，可在【个人中心-优惠券】查看", "去查看");
            }
        });
    }

    private void showAwardDialog(final boolean success, String content, String ok) {
        final CHAlertDialog chAlertDialog = new CHAlertDialog(context);
        chAlertDialog.show();
        chAlertDialog.setContent(content);
        chAlertDialog.setTitle("提示！");
        Dialog dialog = chAlertDialog.getDialog();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        chAlertDialog.setOkButton(ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (success) {
                    //我的优惠劵入口
                    WebActivity.startAppLink(context, "https://app-sdk.caohua.com/coupon/myCoupon");
                } else {
                    WebActivity.startAppLink(context, "https://app-sdk.caohua.com/ucenter/contactKefu");
                }
                chAlertDialog.dismiss();
            }
        });
        chAlertDialog.setCancelButton("关闭", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chAlertDialog.dismiss();
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean focus) {
        super.onWindowFocusChanged(focus);
        int w = imgPush.getWidth();
        int h = imgPush.getHeight();
        if (w <= 0 || h <= 0) {
            imgPush.post(new Runnable() {
                @Override
                public void run() {
                    dismiss();
                }
            });
            return;
        }
        WindowManager windowManager = (WindowManager) imgPush.getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        if (height > width) {
            h = (int) (w * entry.heigth / (float) entry.width);
            setImageLayoutParams(w, h);
        } else {
            w = (int) (h * entry.width / (float) entry.heigth);
            setImageLayoutParams(w, h);
        }
    }

    private Bitmap getLocalPic(String filePath) {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inPreferredConfig = Bitmap.Config.RGB_565;
            o.inSampleSize = PicUtil.computeSampleSize(o, -1, entry.width * entry.heigth);
            o.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(filePath, o);
        } catch (OutOfMemoryError error) {
            LogUtil.errorLog("getPushPic OutOfMemoryError");
        } catch (Exception e) {
            LogUtil.errorLog("getPushPic Exception:" + e.getMessage());
        }
        return null;
    }

    private void setImageLayoutParams(int w, int h) {
        RelativeLayout.LayoutParams params = (LayoutParams) imgPush.getLayoutParams();
        if (params != null) {
            params.width = w;
            params.height = h;
            imgPush.setLayoutParams(params);
        }
    }

    private void jump() {
        String url = entry.jumpUrl;
        Context context = getContext();
        if (!TextUtils.isEmpty(url) && url.contains(StoreSecondActivity.SHOP_URL)) {
            Intent webIntent = new Intent(context, StoreSecondActivity.class);
            webIntent.putExtra("url", url);
            webIntent.putExtra("type", 1);
            context.startActivity(webIntent);
        } else {
            WebActivity.startWebPage(context, url);
        }
    }

    private void saveClickStatus() {
        if (PushEntry.SHOW_TYPE_ALWAYS.equals(entry.showType))
            return;

        String time = entry.expireTime;
        String id = entry.msgId;
        String user = SdkSession.getInstance().getUserName();
        final boolean login = AppContext.getAppContext().isLogin();
        // 保存 showType 以及 当前时间
        if (login) {
            DataStorage.savePush(getContext(), id, user, time, entry.showType);
        } else {
            DataStorage.saveNotUserPush(getContext(), id, time, entry.showType);
        }
    }

    @SuppressWarnings("deprecation")
    private void removeLayoutListenerPre16(ViewTreeObserver observer, OnGlobalLayoutListener listener) {
        observer.removeGlobalOnLayoutListener(listener);
    }

    @TargetApi(16)
    private void removeLayoutListenerPost16(ViewTreeObserver observer, OnGlobalLayoutListener listener) {
        observer.removeOnGlobalLayoutListener(listener);
    }
}
