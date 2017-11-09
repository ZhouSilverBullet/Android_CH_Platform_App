package com.caohua.games.ui.hot;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.TransmitDataInterface;
import com.caohua.games.biz.hot.HotEntry;
import com.caohua.games.biz.hot.RewardEntry;
import com.caohua.games.biz.hot.RewardLogic;
import com.caohua.games.biz.hot.SubHotLogic;
import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.app.AnalyticsHome;
import com.chsdk.configure.DataStorage;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.LoadingDialog;
import com.chsdk.ui.widget.RiffEffectImageButton;
import com.chsdk.utils.LogUtil;
import com.chsdk.utils.PicUtil;
import com.chsdk.utils.ViewUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by ZengLei on 2016/10/31.
 */

public class HomeHotItemView extends LinearLayout {

    public static final int TYPE_IS_LEFT = 10;
    public static final int TYPE_ONE_PAGER = 11;
    public static final int TYPE_IS_RIGHT = 12;

    private RiffEffectImageButton imgIcon;
    private TextView tvTitle;
    private HotEntry entry;
    public static final String HOTE_GEFT_CODE = "256";
    private int hotType;

    public HomeHotItemView(Context context, int hotType) {
        super(context);
        this.hotType = hotType;
        loadXml();
    }

    public HomeHotItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadXml();
    }

    private void loadXml() {
        LayoutInflater.from(getContext()).inflate(R.layout.ch_view_home_hot_item, this, true);
        setOrientation(VERTICAL);
        setClickable(true);
        setGravity(Gravity.CENTER);
        imgIcon = (RiffEffectImageButton) findViewById(R.id.ch_hot_item_icon);
        switch (hotType) {
            case TYPE_IS_LEFT:
                setPadding(ViewUtil.dp2px(getContext(), 10), ViewUtil.dp2px(getContext(), 5),
                        ViewUtil.dp2px(getContext(), 5), ViewUtil.dp2px(getContext(), 2));
                setLayoutParams(imgIcon, false);
                break;
            case TYPE_IS_RIGHT:
                setPadding(ViewUtil.dp2px(getContext(), 5), ViewUtil.dp2px(getContext(), 5),
                        ViewUtil.dp2px(getContext(), 10), ViewUtil.dp2px(getContext(), 2));
                setLayoutParams(imgIcon, false);
                break;
            case TYPE_ONE_PAGER:
                setPadding(ViewUtil.dp2px(getContext(), 10), ViewUtil.dp2px(getContext(), 5),
                        ViewUtil.dp2px(getContext(), 10), ViewUtil.dp2px(getContext(), 2));
                setLayoutParams(imgIcon, true);
                break;

        }
        imgIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (entry != null) {
                    AnalyticsHome.umOnEvent(AnalyticsHome.HOME_HOT_ACTIVITY_CLICK_ANALYTICS, "热门活动二级页面网页");
                    if (!entry.isSubHot()) {
                        WebActivity.startWebPage(getContext(), entry.getActivity_url());
                    } else {
                        //领取特殊礼包
                        AppContext appContext = AppContext.getAppContext();
                        if (!appContext.isLogin()) {
                            appContext.login((Activity) getContext(), new TransmitDataInterface() {
                                @Override
                                public void transmit(Object o) {
                                    if (o instanceof LoginUserInfo) {
                                        LogUtil.errorLog("登陆成功!");
                                    }
                                }
                            });
                            return;
                        }

                        final LoadingDialog loadingDialog = new LoadingDialog(getContext(), "");
                        loadingDialog.show();

                        new RewardLogic(getContext()).getRewardLogic(new BaseLogic.AppLogicListner() {
                            @Override
                            public void failed(String errorMsg) {
                                String tip = null;
                                if (TextUtils.isEmpty(errorMsg)) {
                                    tip = "获取活动信息失败";
                                } else {
                                    tip = errorMsg;
                                }
                                CHToast.show(getContext(), tip);
                                loadingDialog.dismiss();
                            }

                            @Override
                            public void success(Object entryResult) {
                                loadingDialog.dismiss();
                                if (entryResult instanceof RewardEntry) {
                                    showPopWindow((RewardEntry) entryResult);
                                }
                            }
                        });
                    }
                }
            }
        });
        tvTitle = (TextView) findViewById(R.id.ch_hot_item_title);
    }

    public void notifyHomeHotItemView() {
        EventBus.getDefault().post(new NotifyHotEntry());
    }


    /**
     * 新人领取界面的 PopWindow
     */
    private void showPopWindow(RewardEntry entry) {
        View popView = LayoutInflater.from(getContext()).inflate(R.layout.hot_new_pop_item, null);
        TextView minuteImageBtn = (TextView) popView.findViewById(R.id.iv_minute_btn);
        TextView laterImageBtn = (TextView) popView.findViewById(R.id.iv_later_btn);
        TextView rewardText = (TextView) popView.findViewById(R.id.hot_new_text_view);
        TextView caoHuaBi = (TextView) popView.findViewById(R.id.hot_new_tv_cao_hua_bi);

        String reward = entry.getReward();
        if (RewardLogic.REWARD_CAOHUA_B.equals(reward)) {
            caoHuaBi.setText("草花币");
        } else if (RewardLogic.REWARD_CAOHUA_D.equals(reward)) {
            caoHuaBi.setText("草花豆"); //草花豆
        }
        rewardText.setText(entry.getNum());
        final PopupWindow popupWindow = new PopupWindow(popView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, false);
        popupWindow.showAtLocation(this, Gravity.CENTER, 0, 0);

        isShowing(popupWindow);

        popupWindow.setOutsideTouchable(true);
        minuteImageBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final LoadingDialog dialog = new LoadingDialog(getContext(), "");
                dialog.show();
                new SubHotLogic().getSubHotLogic(new BaseLogic.LogicListener() {
                    @Override
                    public void failed(String errorMsg) {
                        if (!TextUtils.isEmpty(errorMsg) && HOTE_GEFT_CODE.equals(errorMsg)) {
                            DataStorage.setHotGiftOnce(getContext(), true);
                            CHToast.show(getContext(), "您已经领过了");
                        } else {
                            CHToast.show(getContext(), "领取失败,稍后重试");
                        }
                        dialog.dismiss();
                        popupWindow.dismiss();
                        notifyHomeHotItemView();
                        isShowing(popupWindow);
                    }

                    @Override
                    public void success(String... entryResult) {
                        CHToast.show(getContext(), "领取成功");
                        dialog.dismiss();
                        popupWindow.dismiss();
                        DataStorage.setHotGiftOnce(getContext(), true);
                        notifyHomeHotItemView();
                        isShowing(popupWindow);
                    }
                });
            }
        });

        laterImageBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                isShowing(popupWindow);
            }
        });

    }

    public static boolean isShowing;

    private void isShowing(PopupWindow popupWindow) {
        if (popupWindow.isShowing()) {
            isShowing = true;
        } else {
            isShowing = false;
        }
    }

    public void setData(HotEntry entry) {
        if (entry == null)
            return;

        tvTitle.setText(entry.getActivity_name());
        if (this.entry == null || !entry.sameIcon(this.entry)) {
            PicUtil.displayImg(getContext(), imgIcon, entry.getActivity_img(), R.drawable.ch_default_pic);
        }
        this.entry = entry;
    }

    public RiffEffectImageButton getImgIcon() {
        return imgIcon;
    }

    private void setLayoutParams(View view, boolean one) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.height = ViewUtil.getRealHeight(getContext(), 310, 130, 25, !one);
        view.setLayoutParams(params);
    }
}
