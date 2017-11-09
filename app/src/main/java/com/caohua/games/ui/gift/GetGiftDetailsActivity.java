package com.caohua.games.ui.gift;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.TransmitDataInterface;
import com.caohua.games.biz.download.DownloadIsInsert;
import com.caohua.games.biz.download.ViewDownloadMgr;
import com.caohua.games.biz.gift.GetGiftDetailsEntry;
import com.caohua.games.biz.gift.GetGiftDetailsLogic;
import com.caohua.games.biz.gift.GetGiftDetailsModel;
import com.caohua.games.biz.gift.GiftEntry;
import com.caohua.games.biz.gift.ShowCardnoLogic;
import com.caohua.games.biz.gift.ShowCardnoModel;
import com.caohua.games.ui.BaseActivity;
import com.caohua.games.ui.download.DownloadProgressButton;
import com.caohua.games.ui.widget.SubActivityTitleView;
import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.app.AnalyticsHome;
import com.chsdk.biz.download.DownloadEntry;
import com.chsdk.configure.SdkSession;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.ui.widget.CHAlertDialog;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.LoadingDialog;
import com.chsdk.ui.widget.RippleEffectButton;
import com.chsdk.utils.ApkUtil;
import com.chsdk.utils.LogUtil;
import com.chsdk.utils.PicUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by CXK on 2016/11/5.
 */

public class GetGiftDetailsActivity extends BaseActivity implements View.OnClickListener {
    private TextView giftDes;
    private RippleEffectButton giftButton;
    private TextView giftRuntime;
    private SubActivityTitleView giftTitle;
    private TextView userMethod;
    private ImageView giftIcon;
    private GetGiftDetailsEntry detailsEntry;
    private int isGet;
    private DownloadProgressButton progressButton;
    private GiftEntry mEntry;
    private LoadingDialog loadingDialog;
    private ViewDownloadMgr viewDownloadMgr;
    private TextView takeLimit;
    private int type;
    private String data;
    private String uId = "0";
    private String giftId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch_activity_get_gift_details);
        initView();
        loadingDialog = new LoadingDialog(this, true);
        loadingDialog.show();
        initEvent();
    }

    private void initEvent() {
        giftButton.setOnClickListener(this);
        giftIcon.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        openWelcomeActivity(this, 1);
    }

    private int getInt(String value) {
        int intValue = 0;
        if (!TextUtils.isEmpty(value)) {
            try {
                intValue = Integer.parseInt(value);
            } catch (Exception e) {
                intValue = 0;
            }
        }
        return intValue;
    }

    private void initVariables() {
        if (getIntent() != null) {
            Uri uri = getIntent().getData();
            if (uri != null) {
                type = 99;
                giftId = uri.getQueryParameter("gift_id");
                uId = uri.getQueryParameter("user_id");
                data = uri.getQueryParameter("data");
            }
        }
    }

    private void initView() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        giftDes = getView(R.id.ch_activity_get_gift_details_des);
        giftButton = getView(R.id.ch_activity_get_gift_details_gift_btn);
        giftRuntime = getView(R.id.ch_activity_get_gift_details_runtime);
        giftTitle = getView(R.id.ch_activity_get_gift_details_sub_title);
        giftTitle.getBackImage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWelcomeActivity(v.getContext(), 1);
                finish();
            }
        });
        userMethod = getView(R.id.ch_activity_get_gift_details_user_method);
        this.giftIcon = getView(R.id.ch_activity_get_gift_details_icon);
        progressButton = getView(R.id.ch_progress_button);
        takeLimit = getView(R.id.ch_activity_get_gift_details_take_limit);

        Intent intent = getIntent();
        mEntry = (GiftEntry) intent.getSerializableExtra("entry");
        String userId = intent.getStringExtra("userId");
        type = intent.getIntExtra("type", 0);
        data = intent.getStringExtra("data");

        if (mEntry != null) {
            giftId = mEntry.getGift_id();
            isGet = mEntry.getIs_get();
            final String giftIcons = mEntry.getGame_icon();
            String giftDescs = mEntry.getGift_desc();
            String giftNames = mEntry.getGift_name();

            if (!TextUtils.isEmpty(giftNames)) {
                giftTitle.setTitle(giftNames);
            }

            if (!TextUtils.isEmpty(giftDescs)) {
                giftDes.setText(giftDescs);
            }

//            PicUtil.displayImg(GetGiftDetailsActivity.this, GetGiftDetailsActivity.this.giftIcon, giftIcons, R.drawable.ch_default_apk_icon);

            if (isGet == 1) {
                giftButton.setText("已\t\t\t领\t\t\t取");
            } else if (isGet == 0) {
                giftButton.setText("领\t\t\t取");
            }

        } else if (type == 100) {
            String path = intent.getStringExtra("path");
            LogUtil.errorLog(path);
            giftId = path;
        } else {
            giftId = "";
            failButtonBehavior();
        }

        initVariables();

        if (TextUtils.isEmpty(userId)) {
            LoginUserInfo user = AppContext.getAppContext().getUser();
            if (user != null) {
                uId = user.userId;
            }
        }

        GetGiftDetailsModel model = new GetGiftDetailsModel(giftId, userId == null ? uId : userId);
        new GetGiftDetailsLogic().getGetGiftDetails(type, data, model, new BaseLogic.AppLogicListner() {

            @Override
            public void failed(String errorMsg) {
                CHToast.show(getApplicationContext(), errorMsg);
                if (isFinishing()) {
                    return;
                }
                try {
                    loadingDialog.dismiss();
                } catch (IllegalArgumentException e) {
                    LogUtil.errorLog("GetGiftDetailsActivity loadingDialog.dismiss() error:" + e.getMessage());
                }
                giftRuntime.setText("有效期:\t" + "获取失败" + "");
                failButtonBehavior();
            }

            @Override
            public void success(Object entryResult) {
                if (isFinishing()) {
                    return;
                }
                if (entryResult != null && (type == 100 || type == 99)) {
//                downloadEntry.downloadUrl="http://dl3.caohua.com/APK/dgzfz_caohua/639/576/dgzfz_caohua.apk";
//                downloadEntry.title="帝国征服";
//                downloadEntry.pkg ="11";

                    mEntry = new GiftEntry();
                    detailsEntry = ((GetGiftDetailsEntry) entryResult);
                    mEntry.setItem(detailsEntry.getItem() + "");
                    String gift_id = detailsEntry.getGift_id();
                    mEntry.setGift_id(gift_id);

                    String use_state = detailsEntry.getUse_state();
                    if (!TextUtils.isEmpty(use_state)) {
                        userMethod.setText("使用方法：" + use_state + "");
                    } else {
                        userMethod.setText("使用方法： 在游戏上方【奖励-福利】图标点开后，礼包领取栏输入激活码领取");
                    }

                    int is_get = detailsEntry.getIs_get();
                    mEntry.setIs_get(is_get);
                    isGet = is_get;
                    if (is_get == 1) {
                        giftButton.setText("已\t\t\t领\t\t\t取");
                    } else if (is_get == 0) {
                        giftButton.setText("领\t\t\t取");
                    }

                    takeLimit();

                    mEntry.setGame_icon(detailsEntry.getGame_icon());
                    mEntry.setGift_desc(detailsEntry.getGift_desc());
                    mEntry.setGift_name(detailsEntry.getGift_name());
                    String package_name = detailsEntry.getPackage_name();

                    downloadView(package_name);

                    checkAppInstalled(package_name);

                    if (detailsEntry != null) {
                        if (!TextUtils.isEmpty(detailsEntry.getGame_icon())) {
                            PicUtil.displayImg(GetGiftDetailsActivity.this, GetGiftDetailsActivity.this.giftIcon, detailsEntry.getGame_icon(), R.drawable.ch_default_apk_icon);
                        }
                        String gift_name = detailsEntry.getGift_name();
                        if (!TextUtils.isEmpty(gift_name)) {
                            giftTitle.setTitle(gift_name);
                        } else {
                            giftTitle.setTitle("获取失败");
                        }

                        String gift_time = detailsEntry.getGift_time();
                        if (!TextUtils.isEmpty(gift_time)) {
                            giftRuntime.setText("有效期:\t" + gift_time + "");
                        } else {
                            giftRuntime.setText("有效期:\t" + "获取失败" + "");
                        }

                        String gift_desc = detailsEntry.getGift_desc();
                        if (!TextUtils.isEmpty(gift_desc)) {
                            giftDes.setText(gift_desc);
                        } else {
                            giftDes.setText("获取失败...");
                        }

                        LogUtil.errorLog(giftId + "----" + detailsEntry.getGift_name() + "--" + detailsEntry.getGift_time() + "--" + detailsEntry.getGift_desc());
                    }
                } else if (mEntry != null && entryResult != null) {
                    detailsEntry = ((GetGiftDetailsEntry) entryResult);
                    String package_name = detailsEntry.getPackage_name();
                    downloadView(package_name);
                    checkAppInstalled(package_name);

                    String gift_time = detailsEntry.getGift_time();
                    if (!TextUtils.isEmpty(gift_time)) {
                        giftRuntime.setText("有效期:\t" + gift_time + "");
                    } else {
                        giftRuntime.setText("有效期:\t" + "获取失败" + "");
                    }

                    String use_state = detailsEntry.getUse_state();
                    if (!TextUtils.isEmpty(use_state)) {
                        userMethod.setText("使用方法：" + use_state + "");
                    } else {
                        userMethod.setText("使用方法：该游戏使用方法获取失败，请稍后重试");
                    }
                    if (!TextUtils.isEmpty(detailsEntry.getGame_icon())) {
                        PicUtil.displayImg(GetGiftDetailsActivity.this, GetGiftDetailsActivity.this.giftIcon, detailsEntry.getGame_icon(), R.drawable.ch_default_apk_icon);
                    } else {
                        giftIcon.setImageResource(R.drawable.ch_default_apk_icon);
                    }
                    takeLimit();
                    LogUtil.errorLog(giftId + "----" + detailsEntry.getGift_name() + "--" + detailsEntry.getGift_time() + "--" + detailsEntry.getGift_desc());
                } else {
                    giftRuntime.setText("有效期:\t" + "获取失败" + "");

                    failButtonBehavior();

                }
                try {
                    loadingDialog.dismiss();
                } catch (IllegalArgumentException e) {
                    LogUtil.errorLog("GetGiftDetailsActivity loadingDialog.dismiss() error:" + e.getMessage());
                }
            }
        });
    }

    private void takeLimit() {
        if (!TextUtils.isEmpty(detailsEntry.getTake_limit())) {
            takeLimit.setText("领取条件：" + detailsEntry.getTake_limit());
        } else {
            takeLimit.setText("领取条件：免费领取");
        }
    }

    @Subscribe
    public void changeButtonStatus(DownloadIsInsert isInsert) {
        if (mEntry != null && !TextUtils.isEmpty(mEntry.getPackage_name())
                && mEntry.getPackage_name().equals(isInsert.getPkg())) {
            giftButton.setVisibility(View.VISIBLE);
            progressButton.setVisibility(View.GONE);
        }
    }

    /**
     * 调下载button的逻辑
     *
     * @param package_name
     */
    private void downloadView(String package_name) {
        viewDownloadMgr = new ViewDownloadMgr(progressButton);
        DownloadEntry downloadEntry = new DownloadEntry();
        downloadEntry.title = detailsEntry.getGame_name();
        downloadEntry.downloadUrl = detailsEntry.getGame_url();
        downloadEntry.pkg = package_name;
        downloadEntry.iconUrl = detailsEntry.getGame_icon();
        viewDownloadMgr.setData(downloadEntry);
    }

    /**
     * 是否安装逻辑
     *
     * @param package_name
     */
    private void checkAppInstalled(String package_name) {
        if (!isAppInstalled(package_name)) {
            giftButton.setVisibility(View.GONE);
            progressButton.setVisibility(View.VISIBLE);
        } else {
            giftButton.setVisibility(View.VISIBLE);
            progressButton.setVisibility(View.GONE);
        }
    }

    /**
     * 错误时的行为
     */
    private void failButtonBehavior() {
        progressButton.setVisibility(View.GONE);
        giftButton.setVisibility(View.VISIBLE);
        giftButton.setText("访问地址有误");
        giftButton.setBackgroundColor(getResources().getColor(R.color.ch_gray_black));
        giftButton.setClickable(false);
        giftIcon.setImageResource(R.drawable.ch_default_apk_icon);
        giftDes.setText("获取失败...");
        giftDes.setTextColor(getResources().getColor(R.color.ch_color_white));
        giftDes.setGravity(Gravity.CENTER);
        giftTitle.setTitle("获取失败");
        takeLimit.setText("领取条件：\t获取失败");
        userMethod.setText("使用方法： 该游戏使用方法获取失败，请稍后重试");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ch_activity_get_gift_details_gift_btn:
                if (type == 99) {
                    getGiftPop();
                } else {
                    if (!AppContext.getAppContext().isLogin()) {
                        AppContext.getAppContext().login(GetGiftDetailsActivity.this, new TransmitDataInterface() {
                            @Override
                            public void transmit(Object o) {
                                if (o instanceof LoginUserInfo) {
                                    LoginUserInfo info = (LoginUserInfo) o;
                                    LogUtil.errorLog(info.nickName + "---" + info.userName + "-------" + info.userId);
                                }
                            }
                        });
                        return;
                    }

                    getGiftPop();
                }

                break;
            case R.id.ch_activity_get_gift_details_icon:

                break;
        }
    }

    private void getGiftPop() {
        if (isGet == 1) {
            CHToast.show(this, "已经领取");
            return;
        }
        if (!TextUtils.isEmpty(detailsEntry.getTake_limit()) && !detailsEntry.getTake_limit().contains("免费领取")) {
            String text = takeLimit.getText().toString();
            showDeleteDialog(text);
        } else {
            showGiftPopWindow();
        }
    }

    private void showDeleteDialog(String content) {
        final CHAlertDialog chAlertDialog = new CHAlertDialog(this);
        chAlertDialog.show();
        chAlertDialog.setContent(content);
        chAlertDialog.setTitle("提示！");
        Dialog dialog = chAlertDialog.getDialog();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        chAlertDialog.setOkButton("领取", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chAlertDialog.dismiss();
                showGiftPopWindow();
            }
        });
        chAlertDialog.setCancelButton("返回", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chAlertDialog.dismiss();

            }
        });
    }

    private boolean isAppInstalled(String pkg) {
        // String packageName = ApkUtil.getUnInstallApkPackageName(context, info.getFilePath());
        return ApkUtil.checkAppInstalled(AppContext.getAppContext(), pkg);
    }

    private void showGiftPopWindow() {
        if (isFinishing()) {
            return;
        }
        final LoadingDialog loadingDialog = new LoadingDialog(this, true);
        loadingDialog.show();
        if (mEntry == null && !isFinishing()) {
            loadingDialog.dismiss();
            return;
        }
        ShowCardnoModel showCardnoModel = new ShowCardnoModel(mEntry);
        new ShowCardnoLogic().getShowCardno(type, data, showCardnoModel, new BaseLogic.AppLogicListner() {
            @Override
            public void failed(String errorMsg) {
                if (!isFinishing()) {
                    loadingDialog.dismiss();
                    CHToast.show(getApplicationContext(), "领取失败：" + errorMsg);
                    AnalyticsHome.umOnEvent(AnalyticsHome.GIFT_CLICK_ANALYTICS,
                            SdkSession.getInstance().getUserName() + "--领取失败：" + errorMsg);
                }
            }

            @Override
            public void success(Object entryResult) {
                if (!isFinishing()) {
                    loadingDialog.dismiss();
                    if (entryResult != null) {
                        AnalyticsHome.umOnEvent(AnalyticsHome.GIFT_CLICK_ANALYTICS,
                                SdkSession.getInstance().getUserName() + "--已领取");
                        LogUtil.debugLog("" + entryResult);
                        String result = (String) entryResult;
                        GiftPopupWindow popWindow = new GiftPopupWindow(GetGiftDetailsActivity.this, result);
                        mEntry.setIs_get(1);
                        giftButton.setText("已\t\t\t领\t\t\t取");
                        EventBus.getDefault().post(mEntry);
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (viewDownloadMgr != null) {
            viewDownloadMgr.release();
        }
    }
}
