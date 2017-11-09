package com.caohua.games.ui.coupon;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.coupon.ColorEggSwitchLogic;
import com.caohua.games.biz.coupon.CouponAwardLogic;
import com.caohua.games.biz.coupon.CouponEntry;
import com.caohua.games.biz.coupon.CouponLogic;
import com.caohua.games.biz.download.ViewDownloadMgr;
import com.caohua.games.ui.BaseActivity;
import com.caohua.games.ui.HomePagerActivity;
import com.caohua.games.ui.download.DownloadButton;
import com.caohua.games.ui.widget.BlankLoginView;
import com.caohua.games.ui.widget.BottomLoadListView;
import com.caohua.games.ui.widget.CHTwoBallView;
import com.caohua.games.ui.widget.RoundProgressBar;
import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.app.AnalyticsHome;
import com.chsdk.biz.download.DownloadEntry;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.CHAlertDialog;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.utils.ApkUtil;
import com.chsdk.utils.LogUtil;
import com.chsdk.utils.PicUtil;
import com.chsdk.utils.ViewUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.wlf.filedownloader.FileDownloader;
import org.wlf.filedownloader.base.DownloadParams;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by zhouzhou on 2017/7/27.
 */

public class CouponCenterActivity extends BaseActivity {

    private ListView bottomListView;
    private LayoutInflater inflater;
    private SmartRefreshLayout smartRefreshLayout;
    private ListAdapter adapter;
    private View emptyView;
    private ViewDownloadMgr downloadMgr;
    private View searchBtn;
    private BlankLoginView blankLogin;
    private View noNetworkView;
    private CHTwoBallView towBallView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch_activity_coupon_center);
        initView();
        if (!blankLogin.isLogin()) {
            blankLogin.show(new BlankLoginView.BlankLoginListener() {
                @Override
                public void onBlankLogin(LoginUserInfo info) {
                    initData();
                }
            });
            return;
        }
        initData();
    }

    private void initData() {
        loadColorEggSwitch();
        showProgress(true);
        loadData(0, false);
    }

    private void loadColorEggSwitch() {
        new ColorEggSwitchLogic().eggSwitch(new BaseLogic.AppLogicListner() {
            @Override
            public void failed(String errorMsg) {
                searchBtn.setVisibility(View.GONE);
            }

            @Override
            public void success(Object entryResult) {
                searchBtn.setVisibility(View.VISIBLE);
            }
        });
    }

    protected void showNoNetworkView(boolean showNoNetwork) {
        if (showNoNetwork) {
            noNetworkView.setVisibility(View.VISIBLE);
            noNetworkView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noNetworkView.setVisibility(View.GONE);
                    showProgress(true);
                    loadData(0, false);
                }
            });
        } else {
            noNetworkView.setVisibility(View.GONE);
        }
    }

    private void loadData(int count, final boolean swipeRefresh) {
        CouponLogic logic = new CouponLogic();
        logic.getCouponEntry(count, new BaseLogic.AppLogicListner() {
            @Override
            public void failed(String errorMsg) {
                showProgress(false);
                if (isFinishing()) {
                    return;
                }
                loadMoreFinish(false);
                CHToast.show(AppContext.getAppContext(), errorMsg);
                smartRefreshLayout.finishRefresh(500, false);
                if (!hasData() && !AppContext.getAppContext().isNetworkConnected()) {
                    showNoNetworkView(true);
                    return;
                }
                showNoNetworkView(false);
                emptyView.setVisibility(hasData() ? View.GONE : View.VISIBLE);
                emptyView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        emptyView.setVisibility(View.GONE);
                        showProgress(true);
                        loadData(0, false);
                    }
                });
            }

            @Override
            public void success(Object entryResult) {
                showNoNetworkView(false);
                if (isFinishing()) {
                    return;
                }
                smartRefreshLayout.finishRefresh();
                if (entryResult instanceof CouponEntry) {
                    CouponEntry couponEntry = (CouponEntry) entryResult;
                    List<CouponEntry.DataBean> data = couponEntry.getData();
                    if (swipeRefresh) {
                        if (adapter != null) {
                            adapter.clear();
                            adapter.addAll(data);
                            loadMoreFinish(true);
                        }
                    } else {
                        if (adapter == null) {
                            adapter = new ListAdapter(data);
                            bottomListView.setAdapter(adapter);
                        } else {
                            if (data != null) {
                                if (data.size() == 0) {
                                    loadMoreFinish(false);
                                } else {
                                    adapter.addAll(data);
                                    loadMoreFinish(true);
                                }
                            }
                        }
                    }
                }
                showProgress(false);
                emptyView.setVisibility(hasData() ? View.GONE : View.VISIBLE);
            }
        });
    }

    protected void loadMoreFinish(boolean success) {
        if (!success) {
            smartRefreshLayout.setLoadmoreFinished(true);
        } else {
            smartRefreshLayout.finishLoadmore();
        }
    }

    private boolean hasData() {
        return adapter != null && adapter.getCount() > 0;
    }

    private void initView() {
        smartRefreshLayout = getView(R.id.ch_coupon_center_swipe_layout);
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                loadData(0, true);
            }
        });

        bottomListView = getView(R.id.ch_coupon_center_bottom_list_view);
        smartRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                if (adapter != null) {
                    loadData(adapter.getCount(), false);
                }
            }
        });
        downloadMgr = new ViewDownloadMgr((DownloadButton) findViewById(R.id.ch_coupon_center_gone_button));
        emptyView = getView(R.id.ch_coupon_center_bottom_empty);
        noNetworkView = getView(R.id.ch_coupon_center_bottom_no_network);
        searchBtn = getView(R.id.ch_activity_mine_search_layout);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppContext.getAppContext().isNetworkConnected()) {
                    CHToast.show(v.getContext(), "请打开网络重试！");
                    return;
                }
                //跳入到彩蛋界面
                FlyingCardActivity.start(CouponCenterActivity.this);
            }
        });

        blankLogin = getView(R.id.ch_coupon_center_blank_login);
        towBallView = getView(R.id.ch_coupon_center_two_ball);
        showProgress(true);
    }

    private void showProgress(boolean isShow) {
        towBallView.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    class ListAdapter extends BaseAdapter {
        private List<CouponEntry.DataBean> list;

        ListAdapter(List<CouponEntry.DataBean> list) {
            inflater = LayoutInflater.from(CouponCenterActivity.this);
            this.list = list;
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public CouponEntry getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void addAll(List<CouponEntry.DataBean> collect) {
            if (list != null) {
                list.addAll(collect);
                notifyDataSetChanged();
            }
        }

        public void clear() {
            if (list != null) {
                list.clear();
                notifyDataSetChanged();
            }
        }

        public void replace(CouponEntry.DataBean entry, int position) {
            if (list != null) {
                LogUtil.errorLog("CouponEntry replace: " + position);
                list.remove(position);
                list.add(position, entry);
                notifyDataSetChanged();
            }
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.ch_coupon_center_list_item, null);
                viewHolder.pb = (RoundProgressBar) convertView.findViewById(R.id.ch_coupon_center_list_item_progress);
                viewHolder.couponIcon = ((ImageView) convertView.findViewById(R.id.ch_coupon_center_list_item_coupon_icon));
                viewHolder.itemTitle = (TextView) convertView.findViewById(R.id.ch_coupon_center_list_item_title);
                viewHolder.itemTitle2 = (TextView) convertView.findViewById(R.id.ch_coupon_center_list_item_title_2);
                viewHolder.itemNumber = (TextView) convertView.findViewById(R.id.ch_coupon_center_list_item_number);
                viewHolder.itemLimit = (TextView) convertView.findViewById(R.id.ch_coupon_center_list_item_limit);
                viewHolder.itemProgressValue = (TextView) convertView.findViewById(R.id.ch_coupon_center_list_item_p_value);
                viewHolder.itemGet = (TextView) convertView.findViewById(R.id.ch_coupon_center_list_item_get);
                viewHolder.obliqueText = (TextView) convertView.findViewById(R.id.ch_coupon_center_list_item_oblique_text);
                viewHolder.itemCostText = (TextView) convertView.findViewById(R.id.ch_coupon_center_list_item_cost);
                viewHolder.descriptionText = (TextView) convertView.findViewById(R.id.ch_coupon_center_list_item_coupon_description);
                viewHolder.contentLayout = convertView.findViewById(R.id.ch_coupon_center_list_item_layout);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final CouponEntry.DataBean dataBean = list.get(position);
            int gameType = dataBean.getGame_type();
            String gameIcon = dataBean.getGame_icon();
            switch (gameType) {  //1.全平台 2.多游戏 3.单游戏
                case 1:
                    viewHolder.contentLayout.setClickable(false);
                    viewHolder.couponIcon.setImageResource(R.drawable.ch_coupon_item_genaral_icon);
                    break;
                case 2:
                    final List<String> gameNames = dataBean.getGame_names();
                    String gameString = "";
                    for (int i = 0; i < gameNames.size(); i++) {
                        String s = gameNames.get(i);
                        if (i == 0) {
                            gameString = s;
                        } else {
                            gameString = gameString + "," + s;
                        }
                    }
                    final String popContent = gameString;
                    viewHolder.contentLayout.setClickable(true);
                    viewHolder.contentLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showContentPop(v, gameNames);
                        }
                    });
                    viewHolder.couponIcon.setImageResource(R.drawable.ch_coupon_item_more_game_icon);
                    break;
                default:
                    viewHolder.contentLayout.setClickable(false);
                    PicUtil.displayImg(CouponCenterActivity.this, viewHolder.couponIcon, gameIcon, R.drawable.ch_default_apk_icon);
                    break;
            }

            String tag = dataBean.getTag();
            if (!TextUtils.isEmpty(tag)) {
                viewHolder.obliqueText.setVisibility(View.VISIBLE);
                viewHolder.obliqueText.setText(tag);
            } else {
                viewHolder.obliqueText.setVisibility(View.GONE);
            }
            final int hasRecive = dataBean.getHas_recive(); //是否已经领取 0.未领取 1.已经领取
            final int useRate = dataBean.getUse_rate();
            viewHolder.pb.setProgress(useRate);
            viewHolder.itemProgressValue.setText(useRate + "%");
            final String takeType = dataBean.getTake_type();
            if (hasRecive == 1) {
                if (useRate == 100) {
                    viewHolder.itemGet.setText("已领完");
                    viewHolder.itemGet.setBackgroundResource(R.drawable.ch_coupon_get_text_selector);
                } else {
                    viewHolder.itemGet.setText("已领取");
                    viewHolder.itemGet.setBackgroundResource(R.drawable.ch_coupon_dis_get_text_selector);
                }
            } else {
                switch (takeType) {
                    case "1":
                        viewHolder.itemGet.setText("立即领取");
                        break;
                    default:
                        viewHolder.itemGet.setText("去领取");
                        break;
                }
                viewHolder.itemGet.setBackgroundResource(R.drawable.ch_coupon_get_text_selector);
                if (useRate == 100) {
                    viewHolder.itemGet.setText("已领完");
                }
            }

            final int jumpType = dataBean.getJump_type();
            viewHolder.itemGet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AnalyticsHome.umOnEvent(AnalyticsHome.COUPON_CENTER_AWARD, "点击领取按钮次数");
                    if (hasRecive == 1) {
                        if (useRate == 100) {
                            viewHolder.itemGet.setText("已领完");
                            viewHolder.itemGet.setBackgroundResource(R.drawable.ch_coupon_get_text_selector);
                        } else {
                            viewHolder.itemGet.setText("已领取");
                            viewHolder.itemGet.setBackgroundResource(R.drawable.ch_coupon_dis_get_text_selector);
                        }
                        CHToast.show(v.getContext(), "不要贪心哦，已经领取过了");
                    } else {
                        if ("1".equals(takeType)) { //直接领取
                            new CouponAwardLogic().centerGet(dataBean.getCenter_id(), new BaseLogic.DataLogicListner<Integer>() {
                                @Override
                                public void failed(String errorMsg, int errorCode) {
                                    if (isFinishing()) {
                                        return;
                                    }
                                    CHToast.show(AppContext.getAppContext(), errorMsg);
                                }

                                @Override
                                public void success(Integer value) {
                                    if (isFinishing()) {
                                        return;
                                    }
                                    CHToast.show(CouponCenterActivity.this, "领取成功！");
                                    CouponEntry.DataBean bean = list.get(position);
                                    bean.setHas_recive(1);
                                    if (value != 0) {
                                        bean.setUse_rate(value);
                                    }
                                    replace(bean, position);
                                    AnalyticsHome.umOnEvent(AnalyticsHome.COUPON_CENTER_AWARD_SUCCESS, "领取成功次数");
                                }
                            });
                        } else {
                            switch (jumpType) {
                                case 1: //链接跳转
                                    String jumpUrl = dataBean.getJump_url();
                                    if (!TextUtils.isEmpty(jumpUrl)) {
                                        WebActivity.startWebPage(CouponCenterActivity.this, jumpUrl);
                                    } else {
                                        if (isFinishing()) {
                                            return;
                                        }
                                        CHToast.show(CouponCenterActivity.this, "跳转链接不存在!");
                                    }
                                    break;
                                case 2: //跳转游戏apk
                                    String jumpPackName = dataBean.getJump_pack_name();
                                    if (!TextUtils.isEmpty(jumpPackName)) {
                                        if (!ApkUtil.checkAppInstalled(CouponCenterActivity.this, jumpPackName)) {
                                            showDownloadDialog(dataBean);
                                        } else {
                                            ApkUtil.startAPP(CouponCenterActivity.this, jumpPackName);
                                        }
                                    } else {
                                        CHToast.show(AppContext.getAppContext(), "未发现有效包名！");
                                    }
                                    break;
                            }
                        }
                    }
                }
            });

            String useType = dataBean.getUse_type();
            if (useType != null) {
                switch (useType) {
                    case "2":
                        viewHolder.itemTitle.setText("折扣券");
                        viewHolder.itemCostText.setText("折");
                        viewHolder.itemCostText.setVisibility(View.VISIBLE);
                        break;
                    default:
                        viewHolder.itemTitle.setText("代金券");
                        viewHolder.itemCostText.setVisibility(View.GONE);
                        break;
                }
            } else {
                viewHolder.itemTitle.setText("代金券");
            }
            int pixels = CouponCenterActivity.this.getResources().getDisplayMetrics().widthPixels;
            if (pixels >= 1080) {
                viewHolder.itemTitle2.setText("\t\t\t\t\t " + dataBean.getName());
            } else {
                viewHolder.itemTitle2.setText("\t\t\t\t" + dataBean.getName());
            }
            String minAmt = dataBean.getMin_amt();
            if (!TextUtils.isEmpty(minAmt)) {
                viewHolder.itemLimit.setVisibility(View.VISIBLE);
                viewHolder.itemLimit.setText(minAmt);
            } else {
                viewHolder.itemLimit.setVisibility(View.GONE);
            }
            viewHolder.itemNumber.setText(dataBean.getUse_amt());

            String description = dataBean.getDescription();
            if (!TextUtils.isEmpty(description)) {
                viewHolder.descriptionText.setText(description);
                viewHolder.descriptionText.setVisibility(View.VISIBLE);
            } else {
                viewHolder.descriptionText.setVisibility(View.GONE);
            }
            return convertView;
        }

        private void showDownloadDialog(final CouponEntry.DataBean dataBean) {
            final CHAlertDialog chAlertDialog = new CHAlertDialog(CouponCenterActivity.this);
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
                    if (!EasyPermissions.hasPermissions(CouponCenterActivity.this, perms)) {
                        EasyPermissions.requestPermissions(CouponCenterActivity.this, "请授予手机读写权限", HomePagerActivity.PERMISSION_FOR_WRITE_STORAGE, perms);
                        return;
                    }
                    CHToast.show(AppContext.getAppContext(), "正在下载中，可在广场右上角下载管理中查看进度");
                    DownloadParams params = new DownloadParams();
                    String jumpGameUrl = dataBean.getJump_game_url();
                    params.url = jumpGameUrl;
                    String jumpGameName = dataBean.getJump_game_name();
                    params.title = jumpGameName;
                    params.pkg = dataBean.getJump_pack_name();
                    params.iconUrl = dataBean.getJump_game_icon();

                    DownloadEntry entry = new DownloadEntry();  //解决可以在通知栏加入进度条
                    entry.detail_url = "";
                    entry.title = jumpGameName;
                    entry.downloadUrl = jumpGameUrl;
                    entry.pkg = dataBean.getJump_pack_name();
                    entry.iconUrl = dataBean.getJump_game_icon();
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

        private void showContentPop(View v, final List<String> popContent) {
            CouponCenterActivity context = CouponCenterActivity.this;
            View view = View.inflate(context, R.layout.ch_coupon_dialog_list, null);
            ListView listView = (ListView) view.findViewById(R.id.ch_coupon_dialog_list_view);
            View back = view.findViewById(R.id.ch_coupon_dialog_list_back);
            listView.setAdapter(new BaseAdapter() {
                @Override
                public int getCount() {
                    return popContent == null ? 0 : popContent.size();
                }

                @Override
                public String getItem(int position) {
                    return popContent.get(position);
                }

                @Override
                public long getItemId(int position) {
                    return position;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    Context ctt = parent.getContext();
                    if (convertView == null) {
                        convertView = View.inflate(ctt, R.layout.ch_coupon_dialog_list_item, null);
                    }
                    TextView textView = (TextView) convertView;
                    textView.setText(popContent.get(position));
                    return convertView;
                }
            });
            final Dialog dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
            dialog.setContentView(view);
            Window dialogWindow = dialog.getWindow();
            dialogWindow.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = ViewUtil.dp2px(context, 240);
            dialogWindow.setAttributes(lp);
            dialog.show();//显示对话框
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });
        }

        class ViewHolder {
            RoundProgressBar pb;
            ImageView couponIcon;
            TextView itemTitle;
            TextView itemTitle2;
            TextView itemNumber;
            TextView itemLimit;
            TextView itemProgressValue;
            TextView itemGet;
            TextView obliqueText;
            TextView descriptionText;
            TextView itemCostText;
            View contentLayout;
        }

    }
}
