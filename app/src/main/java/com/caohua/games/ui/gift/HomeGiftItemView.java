package com.caohua.games.ui.gift;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.DataInterface;
import com.caohua.games.biz.TransmitDataInterface;
import com.caohua.games.biz.gift.GiftEntry;
import com.caohua.games.biz.gift.ShowCardnoLogic;
import com.caohua.games.biz.gift.ShowCardnoModel;
import com.caohua.games.ui.giftcenter.GiftDetailActivity;
import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.app.AnalyticsHome;
import com.chsdk.configure.SdkSession;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.LoadingDialog;
import com.chsdk.ui.widget.RiffEffectRelativeLayout;
import com.chsdk.utils.ApkUtil;
import com.chsdk.utils.LogUtil;
import com.chsdk.utils.PicUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by CXK on 2016/10/22.
 */

public class HomeGiftItemView extends RiffEffectRelativeLayout implements DataInterface {

    private GiftEntry entry;
    private ImageView imgIcon;
    private TextView tvTitle, tvDes;
    private PopupWindow popWindow;
    private GiftEntry mEntry;
    private TextView giftChangeBtn;

    public HomeGiftItemView(Context context) {
        super(context);
        loadXml();
    }

    public HomeGiftItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadXml();
    }

    public HomeGiftItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadXml();
    }

    private void loadXml() {
        LayoutInflater.from(getContext()).inflate(R.layout.ch_view_home_gift_item, this, true);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        init();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    private void init() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                //不改,登陆就没有数据,因为接口改了,所以,不好控制了
//                if (!AppContext.getAppContext().isLogin()) {
//                    AppContext.getAppContext().login((Activity) getContext(), new TransmitDataInterface() {
//                        @Override
//                        public void transmit(Object o) {
//                            if (o instanceof LoginUserInfo) {
//                                LoginUserInfo info = (LoginUserInfo) o;
//                                EventBus.getDefault().post(info);
//                            }
//                        }
//                    });
//                    return;
//                }

                skipDetailsActivity();
            }
        });
        giftChangeBtn = (TextView)findViewById(R.id.ch_view_gift_btn);

        giftChangeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                String package_name = mEntry.getPackage_name();
                if (TextUtils.isEmpty(package_name)) {
                    CHToast.show(getContext(),"无效包名");
                    return;
                }

                if(!isAppInstalled(package_name)){
                    skipDetailsActivity();
                    return;
                }

                if (!AppContext.getAppContext().isLogin()) {
                    AppContext.getAppContext().login((Activity) getContext(), new TransmitDataInterface() {
                        @Override
                        public void transmit(Object o) {
                            if (o instanceof LoginUserInfo) {
                            }
                        }
                    });
                    return;
                }

                if (popWindow != null && popWindow.isShowing()) {
                    popWindow.dismiss();
                    return;
                } else {
                    if (mEntry.getIs_get()==1) {
                        CHToast.show(getContext(),"已经领取了礼包");
                        return;
                    }
                    showGiftPopWindow();
                }

            }
        });

        imgIcon = (ImageView) findViewById(R.id.ch_view_gift_icon);
        tvTitle = (TextView) findViewById(R.id.ch_view_gift_title);
        tvDes = (TextView) findViewById(R.id.ch_view_gift_des);
    }

    private void skipDetailsActivity() {
        if (mEntry != null && !TextUtils.isEmpty(mEntry.getGift_id())) {
            GiftDetailActivity.start(getContext(), mEntry.getGift_id(), mEntry.getGift_name(), GiftDetailActivity.TYPE_NORMAL);
            AnalyticsHome.umOnEvent(AnalyticsHome.HOME_HOT_GIFT_CLICK_ANALYTICS,mEntry.getGift_name());
//            Intent intent = new Intent(getContext(), GetGiftDetailsActivity.class);
//        intent.putExtra("gift_id",mEntry.getGift_id());
//        intent.putExtra("gift_icon",mEntry.getGame_icon());
//        intent.putExtra("gift_desc",mEntry.getGift_desc());
//        intent.putExtra("gift_name",mEntry.getGame_name());
//        intent.putExtra("isGet",mEntry.getIs_get());
//            intent.putExtra("entry", mEntry);
//            getContext().startActivity(intent);
//        } else {
//            CHToast.show(getContext(), "请求参数错误");
        }
    }

    private boolean isAppInstalled(String pkg) {
        // String packageName = ApkUtil.getUnInstallApkPackageName(context, info.getFilePath());
        return ApkUtil.checkAppInstalled(AppContext.getAppContext(), pkg);
    }

    public void setData(Object o) {
        if (o == null)
            return;
        if (o instanceof Integer) {
            giftChangeBtn.setText("已领取");
            return;
        }
        mEntry = (GiftEntry) o;
        if (this.entry == null || !this.entry.sameIcon(mEntry)) {
            PicUtil.displayImg(getContext(), imgIcon, mEntry.getGame_icon(), R.drawable.ch_default_apk_icon);
        }

        this.entry = mEntry;
//        tvDes.setText(Html.fromHtml("<B>"+ mEntry.getGift_name() + "</B>: " + mEntry.getGift_desc() + ""));
        tvDes.setText(""+ mEntry.getGift_name() + ": " + mEntry.getGift_desc() + "");
        tvTitle.setText(mEntry.getGame_name());
        if (mEntry.getIs_get() == 1) {
            giftChangeBtn.setText("已领取");
        }else {
            giftChangeBtn.setText("领取");
        }
    }

    private void showGiftPopWindow() {
        final LoadingDialog loadingDialog = new LoadingDialog(getContext(), "");
        loadingDialog.show();
        ShowCardnoModel showCardnoModel = new ShowCardnoModel(mEntry);
        new ShowCardnoLogic().getShowCardno(0 ,"" , showCardnoModel, new BaseLogic.AppLogicListner() {
            @Override
            public void failed(String errorMsg) {
                loadingDialog.dismiss();
                CHToast.show(getContext(),"领取失败：" + errorMsg);
                AnalyticsHome.umOnEvent(AnalyticsHome.GIFT_CLICK_ANALYTICS,
                        SdkSession.getInstance().getUserName() + "--领取失败：" + errorMsg);
            }

            @Override
            public void success(Object entryResult) {
                LogUtil.debugLog(""+entryResult);
                if (entryResult != null) {
                    String result = (String) entryResult;
                    popWindow = new GiftPopupWindow(getContext(),result);
                    giftChangeBtn.setText("已领取");
                    mEntry.setIs_get(1);
                    AnalyticsHome.umOnEvent(AnalyticsHome.GIFT_CLICK_ANALYTICS,
                            SdkSession.getInstance().getUserName() + "--已领取");
                }
                loadingDialog.dismiss();
            }
        });
    }

    @Subscribe
    public void refreshButton(GiftEntry entry){
        LogUtil.errorLog(entry+"");
        if (entry.getGift_id().equals(mEntry.getGift_id())) {
            giftChangeBtn.setText("已领取");
        }
    }


}
