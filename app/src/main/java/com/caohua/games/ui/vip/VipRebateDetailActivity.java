package com.caohua.games.ui.vip;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.account.PayCheckLogic;
import com.caohua.games.biz.download.ViewDownloadMgr;
import com.caohua.games.biz.task.TaskFilter;
import com.caohua.games.biz.vip.RebateSubmitNotify;
import com.caohua.games.biz.vip.VipRebateEntry;
import com.caohua.games.biz.vip.VipRebateLogic;
import com.caohua.games.ui.HomePagerActivity;
import com.caohua.games.ui.account.PayActionActivity;
import com.caohua.games.ui.adapter.CommonRecyclerViewAdapter;
import com.caohua.games.ui.adapter.ItemDivider;
import com.caohua.games.ui.adapter.ViewHolder;
import com.caohua.games.ui.download.DownloadButton;
import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.WebParamEntry;
import com.chsdk.biz.app.AnalyticsHome;
import com.chsdk.biz.download.DownloadEntry;
import com.chsdk.configure.DataStorage;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.RequestExe;
import com.chsdk.model.app.LinkModel;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.CHAlertDialog;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.LoadingDialog;
import com.chsdk.utils.ApkUtil;
import com.chsdk.utils.PicUtil;
import com.chsdk.utils.ViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.wlf.filedownloader.FileDownloader;
import org.wlf.filedownloader.base.DownloadParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by admin on 2017/9/25.
 */

public class VipRebateDetailActivity extends CommonActivity {
    public static final String REBATE_DETAIL_ID = "rbi";
    private String rebateId;
    private ImageView icon;
    private TextView title;
    private TextView des;
    private RecyclerView recyclerView;
    private TextView time;
    private TextView cafeDes;
    private Button submit;
    private RebateAdapter adapter;
    private boolean isVip;
    private boolean isAuth;
    private boolean isClose;
    private DownloadButton downloadBtn;
    private boolean isOpen;

    @Override
    protected void initVariables() {
        Intent intent = getIntent();
        if (intent != null) {
            rebateId = intent.getStringExtra(REBATE_DETAIL_ID);
            isVip = intent.getBooleanExtra("is_vip", false);
            isAuth = intent.getBooleanExtra("is_auth", false);
            isClose = intent.getBooleanExtra("is_close", false);
            isOpen = intent.getBooleanExtra("is_open", false);
        }
    }

    @Override
    protected String subTitle() {
        return "详情";
    }

    @Override
    protected int childViewId() {
        return R.layout.ch_activity_rebate_detail;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    boolean isNotifySubmit = false;

    @Subscribe
    public void notifySubmit(RebateSubmitNotify entry) {
        submit.setText("已领取");
        submit.setBackgroundResource(R.drawable.ch_vip_rebate_btn_shape_error);
        isNotifySubmit = true;
    }

    @Override
    protected void initView() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        icon = getView(R.id.ch_activity_rebate_detail_icon);
        title = getView(R.id.ch_activity_rebate_detail_title);
        des = getView(R.id.ch_activity_rebate_detail_des);
        recyclerView = getView(R.id.ch_activity_rebate_detail_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.addItemDecoration(new ItemDivider().
                setDividerColor(0xffffffff).setDividerWidth(ViewUtil.dp2px(activity, 0)));
        adapter = new RebateAdapter(activity,
                new ArrayList<VipRebateEntry.DataBean.RebateBean>(), R.layout.ch_vip_rebate_recycler_item_view);
        recyclerView.setAdapter(adapter);
        time = getView(R.id.ch_activity_rebate_detail_time);
        cafeDes = getView(R.id.ch_activity_rebate_detail_cafe_des);
        submit = getView(R.id.ch_activity_rebate_detail_submit);

        downloadBtn = getView(R.id.ch_activity_rebate_detail_download);
    }


    private void setSubmitText(int type) {
        switch (type) {
            case 0:  //活动未结束 不是vip
                submit.setText("先去认证个VIP");
                break;
            case 1: // 活动未结束 是vip且认证
                submit.setText("启动游戏去完成");
                break;
            case 2: // 活动已结束 未认证
                submit.setText("活动已结束");
                submit.setBackgroundResource(R.drawable.ch_vip_rebate_btn_shape_error);
                break;
            case 3: // 活动已结束 已经认证
                submit.setText("领取奖励");
                break;
            case 4:
                submit.setText("已领取");
                submit.setBackgroundResource(R.drawable.ch_vip_rebate_btn_shape_error);
                break;
            case 10:
                submit.setText("去充值");
                break;
        }
    }

    protected boolean isVipOrAuth() {
        if (!isVip) {
            final CHAlertDialog vipDialog = new CHAlertDialog(activity, true, true);
            vipDialog.show();
            vipDialog.setContent("还未达到VIP条件，去看看怎么成为草花VIP吧");
            vipDialog.setCancelButton("取消", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vipDialog.dismiss();
                }
            });
            vipDialog.setOkButton("去查看", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebActivity.startAppLink(activity, BaseLogic.HOST_APP + "vip/expLogView");
                    vipDialog.dismiss();
                }
            });
            return false;
        }

        if (!isAuth) {
            final CHAlertDialog vipDialog = new CHAlertDialog(activity, true, true);
            vipDialog.show();
            vipDialog.setContent("还未进行VIP认证，请先前往认证");
            vipDialog.setCancelButton("取消", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vipDialog.dismiss();
                }
            });
            vipDialog.setOkButton("去认证", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VipCertificationActivity.start(activity);
                    vipDialog.dismiss();
                }
            });
            return false;
        }

        return true;
    }

    @Override
    protected void loadData() {
        new VipRebateLogic().getRebate(rebateId, new BaseLogic.DataLogicListner<VipRebateEntry>() {
            @Override
            public void failed(String errorMsg, int errorCode) {
                CHToast.show(activity, errorMsg);
                if (HttpConsts.ERROR_NO_NETWORK.equals(errorMsg)) {
                    showNoNetworkView(true);
                    return;
                }
            }

            @Override
            public void success(VipRebateEntry entry) {
                if (isFinishing()) {
                    return;
                }
                showNoNetworkView(false);
                if (entry != null && entry.getData() != null) {
                    final VipRebateEntry.DataBean data = entry.getData();
                    String effectTime = data.getEffect_time();
                    String expire_time = data.getExpire_time();
                    time.setText(effectTime + "——" + expire_time);
                    String game_icon = data.getGame_icon();
                    PicUtil.displayImg(activity, icon, game_icon, R.drawable.ch_default_apk_icon);
                    String rebate_desc = data.getRebate_desc();
                    des.setText(rebate_desc);
                    String rebate_title = data.getRebate_title();
                    title.setText(rebate_title);
                    String rebate_intro = data.getRebate_intro();
                    cafeDes.setText(rebate_intro);
                    List<VipRebateEntry.DataBean.RebateBean> rebate = data.getRebate();
                    if (rebate != null && rebate.size() > 0) {
                        adapter.addAll(rebate);
                    }
                    final String game_name = data.getGame_name();
                    final int getStatus = data.getGet_status();
//                    if (getStatus == 0) {
//                        //不可以领取
//                    } else if (getStatus == 1) {
//                        //可以领取
//                    } else if (getStatus == 2) {
//                        //已经领取
//                    }
                    if (!isOpen) {
                        submit.setText("活动将在" + effectTime + "时候开启");
                        return;
                    }
                    if (isClose) {  //是否已经关闭
                        if (!isVip || !isAuth) {
                            setSubmitText(2);
                            return;
                        } else {
                            switch (getStatus) {
                                case 1:
                                    setSubmitText(3); //跳转vip福利
                                    break;
                                case 2:
                                    setSubmitText(4);  //跳转vip福利
                                    break;
                                default:
                                    setSubmitText(2);  //活动已经结束，按钮变灰
                                    return;
                            }
                        }
                    } else {
                        if (!isVip) {
                            setSubmitText(0);
                        } else if (!isAuth) {
                            setSubmitText(0);
                        } else {
                            if ("草花手游".equals(game_name)) {
                                setSubmitText(10);
                            } else {
                                setSubmitText(1);
                            }
                        }
                    }

                    submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isNotifySubmit) {
                                return;
                            }
                            if (isClose) {  //是否已经关闭
                                if (!isVip || !isAuth) {
                                    return;
                                } else {
                                    switch (getStatus) {
                                        case 1: //领取奖励
                                            VipRewardSecondActivity.start(activity, isAuth, isVip);
                                            break;
                                        case 2:
                                            //不做任何跳转
                                            setSubmitText(4);
                                            break;
                                        default:
                                            setSubmitText(2);  //活动已经结束，按钮变灰
                                            return;
                                    }
                                }
                            } else {
                                if (!isVipOrAuth()) {
                                    return;
                                } else {
                                    if ("草花手游".equals(game_name)) {
                                        enterPay();
                                        return;
                                    }
                                    String jumpPackName = data.getPackage_name();
                                    if (!TextUtils.isEmpty(jumpPackName)) {
                                        if (!ApkUtil.checkAppInstalled(activity, jumpPackName)) {
                                            showDownloadDialog(data);
                                        } else {
                                            ApkUtil.startAPP(activity, jumpPackName);
                                        }
                                    } else {
                                        CHToast.show(AppContext.getAppContext(), "未发现游戏！");
                                    }
                                    return;
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void enterPay() {
        String userId = AppContext.getAppContext().getUser().userId;
        boolean payCheck = DataStorage.getPayCheck(AppContext.getAppContext(), userId);
        if (payCheck) {
            enterPayActivity();
        } else {
            payCheckDialog();
        }
    }

    private void payCheckDialog() {
        if (activity == null) {
            return;
        }
        PayCheckLogic checkLogic = new PayCheckLogic();
        final LoadingDialog loadingDialog = new LoadingDialog(activity, true);
        loadingDialog.show();
        checkLogic.getCheck(new BaseLogic.CommentLogicListener() {
            @Override
            public void failed(String errorMsg, int errorCode) {
                loadingDialog.dismiss();
                //如果失败了，就弹出绑定手机号或者验证身份
                showPayCheckDialog();
            }

            @Override
            public void success(String... result) {
                loadingDialog.dismiss();
                AppContext appContext = AppContext.getAppContext();
                DataStorage.setPayCheck(appContext, appContext.getUser().userId, true);
                enterPayActivity();
            }
        });
    }

    private void showPayCheckDialog() {
        final CHAlertDialog chAlertDialog = new CHAlertDialog(activity);
        chAlertDialog.show();
        chAlertDialog.setContent("为了您的账号财产安全，请先绑定手机或实名认证");
        chAlertDialog.setTitle("提示！");
        Dialog dialog = chAlertDialog.getDialog();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        chAlertDialog.setOkButton("去绑定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chAlertDialog.dismiss();
                // 进入手机绑定
                WebParamEntry entry = new WebParamEntry();
                Map<String, String> map = new HashMap<>();
                map.put("fr", "1");
                entry.setMap(map);
                WebActivity.startWebPageGetParam(v.getContext(), TaskFilter.VALUE_URL_REAL_NAME_REGISTRATION_URL, entry);
            }
        });
        chAlertDialog.setCancelButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chAlertDialog.dismiss();
            }
        });
    }

    private void enterPayActivity() {
        Intent intent = new Intent(activity, PayActionActivity.class);
        startActivity(intent);
        AnalyticsHome.umOnEvent(AnalyticsHome.ACCOUNT_USER_CENTER_PAY, "充值草花币二级界面");
    }

    public static void start(Context context, String rbi, boolean open, boolean close, boolean isVip, boolean isAuth) {
        Intent intent = new Intent(context, VipRebateDetailActivity.class);
        intent.putExtra(REBATE_DETAIL_ID, rbi);
        intent.putExtra("is_vip", isVip);
        intent.putExtra("is_auth", isAuth);
        intent.putExtra("is_close", close);
        intent.putExtra("is_open", open);
        context.startActivity(intent);
    }

    private class RebateAdapter extends CommonRecyclerViewAdapter<VipRebateEntry.DataBean.RebateBean> {

        public RebateAdapter(Context context, List<VipRebateEntry.DataBean.RebateBean> list, int layoutId) {
            super(context, list, layoutId);
        }

        @Override
        protected void covert(ViewHolder holder, VipRebateEntry.DataBean.RebateBean rebateBean) {
            TextView title = holder.getView(R.id.ch_vip_rebate_recycler_item_title);
            TextView content = holder.getView(R.id.ch_vip_rebate_recycler_item_content);
            title.setText(rebateBean.getVip_level());
            content.setText(rebateBean.getReward_desc());
        }
    }

    private void showDownloadDialog(final VipRebateEntry.DataBean dataBean) {
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
                String jumpGameUrl = dataBean.getGame_url();
                params.url = jumpGameUrl;
                String jumpGameName = dataBean.getGame_name();
                params.title = jumpGameName;
                params.pkg = dataBean.getPackage_name();
                params.iconUrl = dataBean.getGame_icon();

                DownloadEntry entry = new DownloadEntry();  //解决可以在通知栏加入进度条
                entry.detail_url = "";
                entry.title = jumpGameName;
                entry.downloadUrl = jumpGameUrl;
                entry.pkg = dataBean.getPackage_name();
                entry.iconUrl = dataBean.getGame_icon();
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
}
