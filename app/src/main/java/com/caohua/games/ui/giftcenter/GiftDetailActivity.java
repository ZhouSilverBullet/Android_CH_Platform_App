package com.caohua.games.ui.giftcenter;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.download.ViewDownloadMgr;
import com.caohua.games.biz.gift.GetGiftLogic;
import com.caohua.games.biz.gift.GiftDetailEntry;
import com.caohua.games.biz.gift.GiftDetailLogic;
import com.caohua.games.ui.HomePagerActivity;
import com.caohua.games.ui.download.DownloadButton;
import com.caohua.games.ui.gift.GiftPopupWindow;
import com.caohua.games.ui.giftcenter.widget.GiftDetailLimitView;
import com.caohua.games.ui.vip.CommonActivity;
import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.app.AnalyticsHome;
import com.chsdk.biz.download.DownloadEntry;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.HttpConsts;
import com.chsdk.ui.widget.CHAlertDialog;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.LoadingDialog;
import com.chsdk.utils.ApkUtil;
import com.chsdk.utils.PicUtil;

import org.wlf.filedownloader.FileDownloader;
import org.wlf.filedownloader.base.DownloadParams;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by admin on 2017/10/31.
 */

public class GiftDetailActivity extends CommonActivity {
    public static final String GIFT_ID = "gift_id";
    public static final String GIFT_NAME = "gift_name";

    public static final String GIFT_TYPE = "gift_type"; //普通情况
    public static final int TYPE_SPECIAL = 99; //特殊的情况
    public static final int TYPE_NORMAL = 100; //普通情况


    private String giftId;
    private String giftName;
    private ImageView icon;
    private TextView title;
    private TextView count;
    private TextView des;
    private TextView content;
    private TextView time;
    private TextView duiHuanDes;
    private GiftDetailLimitView limitXiaoHao;
    private GiftDetailLimitView limitShiming;
    private GiftDetailLimitView limitDengji;
    private GiftDetailLimitView limitVip;
    private GiftDetailLimitView limitChdengji;
    private View layout;
    private Button btn;
    private GiftDetailEntry detailEntry;

    private int type;
    private String dataToH5;
    private DownloadButton downloadBtn;
    private View submitLayout;

    @Override
    protected void initVariables() {
        Intent intent = getIntent();
        if (intent != null) {
            giftId = intent.getStringExtra(GIFT_ID);
            giftName = intent.getStringExtra(GIFT_NAME);
            type = intent.getIntExtra(GIFT_TYPE, -1);
            dataToH5 = intent.getStringExtra("data");  //welcomeActivity来的
            //H5来的数据
            Uri uri = getIntent().getData();
            if (uri != null) {
                type = 99;
                giftId = uri.getQueryParameter("gift_id");
                dataToH5 = uri.getQueryParameter("data");
            }
        }
    }

    @Override
    protected boolean hasLogin() {
        return true;
    }

    @Override
    protected String subTitle() {
        return giftName;
    }

    @Override
    protected int childViewId() {
        return R.layout.ch_activity_gift_detail;
    }

    @Override
    protected void initView() {
        layout = findViewById(R.id.ch_activity_gift_detail_layout);
        icon = getView(R.id.ch_activity_gift_detail_icon);
        title = ((TextView) getView(R.id.ch_activity_gift_detail_title));
        count = ((TextView) getView(R.id.ch_activity_gift_detail_count));
        des = ((TextView) getView(R.id.ch_activity_gift_detail_des));
        content = ((TextView) getView(R.id.ch_activity_gift_detail_content));
        time = ((TextView) getView(R.id.ch_activity_gift_detail_time));
        duiHuanDes = ((TextView) getView(R.id.ch_activity_gift_detail_duihuan_des));
        limitXiaoHao = getView(R.id.ch_activity_gift_detail_limit_xiaohao);
        limitShiming = getView(R.id.ch_activity_gift_detail_limit_shiming);
        limitDengji = getView(R.id.ch_activity_gift_detail_limit_juesedengji);
        limitVip = getView(R.id.ch_activity_gift_detail_limit_vip);
        limitChdengji = getView(R.id.ch_activity_gift_detail_limit_chdengji);
        btn = getView(R.id.ch_activity_gift_detail_submit);
        submitLayout = getView(R.id.ch_activity_gift_detail_submit_layout);

        downloadBtn = getView(R.id.ch_activity_gift_detail_download);
        btnEvent();
    }

    private void handleData(GiftDetailEntry entry) {
        detailEntry = entry;
        PicUtil.displayImg(activity, icon, entry.game_icon, R.drawable.ch_default_apk_icon);
        titleView.setTitle(entry.getGift_name());
        title.setText(entry.getGame_name());
        des.setText(entry.getGame_introduct());
        content.setText(entry.getGift_desc());
        time.setText(entry.getGift_time());
        duiHuanDes.setText(entry.getUse_state());
        String takeHb = entry.getTake_hb();
        limitXiaoHao.setTextValue(takeHb);
        if (entry.getTake_certify() == 1) {
            limitShiming.setTextValue("需要");
        } else {
            limitShiming.setTextValue("不需要");
        }
        String take_level = entry.getTake_level();
        if (!TextUtils.isEmpty(take_level)) {
            limitDengji.setTextValue(">=" + take_level);
        }
        String take_vip = entry.getTake_vip();
        if (!TextUtils.isEmpty(take_vip)) {
            limitVip.setTextValue(">=" + take_vip);
        }
        String take_grow = entry.getTake_grow();
        if (!TextUtils.isEmpty(take_grow)) {
            limitChdengji.setTextValue(">=" + take_grow);
        }

        int is_get = entry.getIs_get();
        if (is_get == 1) {
            btn.setText("已\t\t\t领\t\t\t取");
        } else {
            btn.setText("领\t\t\t取");
        }

    }

    @Override
    protected void loadData() {
        showLoadProgress(true);
        new GiftDetailLogic().giftDetail(type, giftId, dataToH5, new BaseLogic.DataLogicListner<GiftDetailEntry>() {
            @Override
            public void failed(String errorMsg, int errorCode) {
                layout.setVisibility(View.INVISIBLE);
                submitLayout.setVisibility(View.GONE);
                showLoadProgress(false);
                CHToast.show(activity, errorMsg);
                if (HttpConsts.ERROR_NO_NETWORK.equals(errorMsg)) {
                    showNoNetworkView(true);
                    return;
                }
                showEmptyView(true);
            }

            @Override
            public void success(GiftDetailEntry entryResult) {
                showLoadProgress(false);
                showNoNetworkView(false);
                if (entryResult == null) {
                    showEmptyView(true);
                    return;
                }
                layout.setVisibility(View.VISIBLE);
                submitLayout.setVisibility(View.VISIBLE);
                showEmptyView(false);
                handleData(entryResult);
            }
        });
    }

    private void btnEvent() {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (detailEntry != null) {
                    String jumpPackName = detailEntry.getPackage_name();
                    if (!TextUtils.isEmpty(jumpPackName)) {
                        if (!ApkUtil.checkAppInstalled(activity, jumpPackName)) {
                            showDownloadDialog(detailEntry);
                            return;
                        }
                    } else {
                        CHToast.show(AppContext.getAppContext(), "未发现游戏！");
                        return;
                    }
                    final LoadingDialog loadingDialog = new LoadingDialog(activity, true);
                    loadingDialog.show();
                    new GetGiftLogic().getGift(type, dataToH5, giftId, detailEntry.item + "", new BaseLogic.DataLogicListner<String>() {
                        @Override
                        public void failed(String errorMsg, int errorCode) {
                            if (isFinishing()) {
                                return;
                            }
                            loadingDialog.dismiss();
                            CHToast.show(activity, errorMsg);
                        }

                        @Override
                        public void success(String entryResult) {
                            if (isFinishing()) {
                                return;
                            }
                            loadingDialog.dismiss();
                            if (!TextUtils.isEmpty(entryResult)) {
                                GiftPopupWindow popWindow = new GiftPopupWindow(activity, entryResult);
                                btn.setText("已领取");
                                if (detailEntry != null) {
                                    detailEntry.setIs_get(1);
                                }
                                AnalyticsHome.umOnEvent(AnalyticsHome.GIFT_CLICK_ANALYTICS,
                                        SdkSession.getInstance().getUserName() + "--已领取");
                            }
                        }
                    });
                }
            }
        });
    }

    private void showDownloadDialog(final GiftDetailEntry detailEntry) {
        final CHAlertDialog chAlertDialog = new CHAlertDialog(activity);
        final ViewDownloadMgr downloadMgr = new ViewDownloadMgr(downloadBtn);
        chAlertDialog.show();
        chAlertDialog.setContent("检测到您未安装该游戏，请下载安装后打开游戏领取");
        chAlertDialog.setTitle("提示！");
        Dialog dialog = chAlertDialog.getDialog();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        chAlertDialog.setOkButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if (!EasyPermissions.hasPermissions(activity, perms)) {
                    EasyPermissions.requestPermissions(activity, "请授予手机读写权限", HomePagerActivity.PERMISSION_FOR_WRITE_STORAGE, perms);
                    return;
                }
                CHToast.show(AppContext.getAppContext(), "正在下载中，可在广场右上角下载管理中查看进度");
                DownloadParams params = new DownloadParams();
                String jumpGameUrl = detailEntry.getGame_url();
                if (TextUtils.isEmpty(jumpGameUrl)) {
                    CHToast.show(AppContext.getAppContext(), "下载的地址为空");
                    return;
                }
                params.url = jumpGameUrl;
                String jumpGameName = detailEntry.getGame_name();
                params.title = jumpGameName;
                params.pkg = detailEntry.getPackage_name();
                params.iconUrl = detailEntry.getGame_icon();

                DownloadEntry entry = new DownloadEntry();  //解决可以在通知栏加入进度条
                entry.detail_url = "";
                entry.title = jumpGameName;
                entry.downloadUrl = jumpGameUrl;
                entry.pkg = detailEntry.getPackage_name();
                entry.iconUrl = detailEntry.getGame_icon();
                downloadMgr.setData(entry);

                FileDownloader.start(params);
                chAlertDialog.dismiss();
                AnalyticsHome.umOnEvent(AnalyticsHome.COUPON_CENTER_AWARD_DOWNLOAD, "点击弹窗的下载游戏次数");
            }
        });
        chAlertDialog.setCancelButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chAlertDialog.dismiss();
            }
        });
    }

    public static void start(Context context, String gift_id, String giftName, int type) {
        Intent intent = new Intent(context, GiftDetailActivity.class);
        intent.putExtra(GIFT_ID, gift_id);
        intent.putExtra(GIFT_NAME, giftName);
        intent.putExtra(GIFT_TYPE, type);
        context.startActivity(intent);
    }
}
