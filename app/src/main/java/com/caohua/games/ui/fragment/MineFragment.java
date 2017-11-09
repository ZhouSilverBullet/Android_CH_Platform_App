package com.caohua.games.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.TransmitDataInterface;
import com.caohua.games.biz.account.AccountPicHelper;
import com.caohua.games.biz.account.LevelInfoEntry;
import com.caohua.games.biz.account.LevelInfoLogic;
import com.caohua.games.biz.account.PayCheckLogic;
import com.caohua.games.biz.minegame.DealTipsLogic;
import com.caohua.games.biz.minegame.LogoutEntry;
import com.caohua.games.biz.minegame.MineDotEntry;
import com.caohua.games.biz.minegame.TipsEntry;
import com.caohua.games.biz.minegame.TipsLogic;
import com.caohua.games.biz.task.TaskFilter;
import com.caohua.games.biz.task.TaskNotifyDotEntry;
import com.caohua.games.ui.account.AccountHeadView;
import com.caohua.games.ui.account.AccountItemView;
import com.caohua.games.ui.account.AccountSettingActivity;
import com.caohua.games.ui.account.MineSendContentActivity;
import com.caohua.games.ui.account.MineWalletActivity;
import com.caohua.games.ui.account.MyFavoriteActivity;
import com.caohua.games.ui.account.PayActionActivity;
import com.caohua.games.ui.account.SystemSetupActivity;
import com.caohua.games.ui.giftcenter.GiftCenterActivity;
import com.caohua.games.ui.minegame.MineGameActivity;
import com.caohua.games.ui.mymsg.MyMsgActivity;
import com.caohua.games.ui.vip.CHVipActivity;
import com.caohua.games.ui.widget.KnowHeightScrollView;
import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.WebParamEntry;
import com.chsdk.biz.app.AnalyticsHome;
import com.chsdk.biz.pay.WalletEntry;
import com.chsdk.biz.pay.WalletInfoLogic;
import com.chsdk.configure.DataStorage;
import com.chsdk.configure.SdkSession;
import com.chsdk.configure.UserDBHelper;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.CHAlertDialog;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.LoadingDialog;
import com.chsdk.utils.LogUtil;
import com.chsdk.utils.ViewUtil;
import com.xiaomi.mipush.sdk.MiPushClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhouzhou on 2017/2/20.
 */

public class MineFragment extends NormalFragment implements View.OnClickListener, KnowHeightScrollView.ScrollViewListener {

    private AccountHeadView accountHeadView;
    private TextView nickName, accountNumber;
    private long lastLoadTime;
    private Animation animation;
    private AccountPicHelper accountPicHelper;
    private static boolean hasShown;
    private AccountItemView giftItemView;
    private AccountItemView msgItemView;
    private AccountItemView commentItemView, favoriteItemView, myMsgItemView;
    private TipsEntry tipsEntry;
    private TextView offText;
    private List<Integer> dotList = new ArrayList<>();
    private View mySendItemView;
    private TextView forumNameText;
    private View userCenterPopBtn;
    private KnowHeightScrollView scrollView;
    private View bgLayout;
    private TextView titleBg;
    //    private ImageView accountBgImage1;
//    private ImageView accountBgImage2;
    private TextView levelNameText;
    private View levelLayout;
    private TextView levelValue;
    private TextView payShowChb;
    private TextView payShowChd;
    private TextView payShowSilver;
    private TextView payShowCoupon;
    private WalletEntry walletEntry;
    private View payShowPay;
    private View payShowCouponLayout;
    private View titleLayoutBg;
    private TextView number;
    private TextView vipName;
    private View vipLayout;
    private TextView vipLevel;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (getActivity() == null) {
            return;
        }
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("uri")) {
                accountPicHelper = new AccountPicHelper(getActivity(), this);
                accountPicHelper.setUri(Uri.parse(savedInstanceState.getString("uri")));
            }
            if (savedInstanceState.containsKey("file")) {
                accountPicHelper = new AccountPicHelper(getActivity(), this);
                accountPicHelper.setCropSaveFile(new File(savedInstanceState.getString("file")));
            }
        }
        getLevelLogic(false);
    }

    private void getLevelLogic(boolean isRefresh) {
        if (!AppContext.getAppContext().isLogin()) {
            return;
        }
        LevelInfoLogic levelInfoLogic = new LevelInfoLogic(AppContext.getAppContext(), isRefresh);
        levelInfoLogic.getLevelInfo(new BaseLogic.AppLogicListner() {
            @Override
            public void failed(String errorMsg) {
//                accountBgImage1.setVisibility(View.VISIBLE);
//                accountBgImage2.setVisibility(View.GONE);
                if (errorMsg.contains("网络连接失败，请重试")) {
                    CHToast.show(AppContext.getAppContext(), errorMsg);
                    return;
                }
//                accountHeadView.setAccountHeadBg("0");
//                levelValue.setText("");
//                levelNameText.setText("");
//                levelLayout.setVisibility(View.GONE);
            }

            @Override
            public void success(Object entryResult) {
                if (entryResult instanceof LevelInfoEntry) {
                    levelLayout.setVisibility(View.VISIBLE);
                    LevelInfoEntry entry = (LevelInfoEntry) entryResult;
                    String grow_name = entry.getGrow_name();
                    levelNameText.setText(grow_name);
                    String grow_level = entry.getGrow_level();
                    levelValue.setText(grow_level);
                    String img_mask = entry.getImg_mask();
                    accountHeadView.setAccountHeadBg(img_mask);
                    if (!TextUtils.isEmpty(grow_name)) {
                        levelLayout.setVisibility(View.VISIBLE);
                    } else {
                        levelLayout.setVisibility(View.GONE);
                    }
                    updateGrowToDb(grow_name, grow_level, img_mask);
                }
            }
        });
    }

    private void updateGrowToDb(String grow_name, String grow_level, String img_mask) {
        String userName = SdkSession.getInstance().getUserName();
        LoginUserInfo info = UserDBHelper.getUser(AppContext.getAppContext(), userName);
        if (info == null) {
            return;
        }
        boolean update = false;
        if (!TextUtils.isEmpty(grow_level)) {
            if (!grow_level.equals(info.grow_level)) {
                info.grow_level = grow_level;
                update = true;
            }
        } else {
            info.grow_level = "";
            update = true;
        }
        if (!TextUtils.isEmpty(img_mask)) {
            if (!img_mask.equals(info.img_mask)) {
                update = true;
                info.img_mask = img_mask;
            }
        } else {
            update = true;
            info.img_mask = "";
        }

        if (!TextUtils.isEmpty(grow_name)) {
            if (!grow_name.equals(info.grow_name)) {
                update = true;
                info.grow_name = grow_name;
            }
        } else {
            update = true;
            info.grow_name = "";
        }
        if (update) {
            UserDBHelper.updateUser(AppContext.getAppContext(), info);
            SdkSession.getInstance().setUserInfo(info);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ch_activity_usercenter;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        tipsLogic(false);
        if (AccountSettingActivity.showLoginDialog) {
            AccountSettingActivity.showLoginDialog = false;
            logout();
            doLogin();
        }

        if (AppContext.getAppContext().isLogin()) {
            setLoginInfo(true, AppContext.getAppContext().getUser());
        } else {
            setLoginInfo(false, null);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getLevelLogic(false);
            loadData(false);
            tipsLogic(false);
        }
    }

    public void tipsLogic(boolean isRefresh) {
        if (getActivity() == null) {
            return;
        }
        new TipsLogic(getActivity(), isRefresh).getTips(new BaseLogic.AppLogicListner() {
            @Override
            public void failed(String errorMsg) {

            }

            @Override
            public void success(Object entryResult) {
                dotList.clear();
                notifyDot(View.INVISIBLE);
                if (entryResult instanceof TipsEntry) {
                    tipsEntry = (TipsEntry) entryResult;
                    int get_gift = tipsEntry.getGet_gift();
                    if (get_gift == 1) {
                        dotList.add(1);
                    }
                    giftItemView.setText(get_gift);
                    if (tipsEntry.getMsg() == 1 || tipsEntry.getConsume() == 1
                            || tipsEntry.getRecharge() == 1) {
                        msgItemView.setText(1);
                        dotList.add(1);
                    } else {
                        msgItemView.setText(0);
                    }
                    int comment = tipsEntry.getComment();
                    if (comment == 1) {
                        dotList.add(1);
                    }
                    commentItemView.setText(comment);
                }
                if (dotList.size() > 0) {
                    //通知显示小红点
                    notifyDot(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void doLogin() {
        if (getActivity() == null) {
            return;
        }
        AppContext.getAppContext().login(getActivity(), new TransmitDataInterface() {
            @Override
            public void transmit(Object o) {
                if (o instanceof LoginUserInfo) {
                    LoginUserInfo info = (LoginUserInfo) o;
                    setLoginInfo(true, info);
                    loadData(true);
                    tipsLogic(true);
                }
            }
        });
    }

    private void setLoginInfo(boolean login, LoginUserInfo info) {
        if (login && info != null && info.userName != null) {
            getLevelLogic(true);
            if (getActivity() != null && !TextUtils.isEmpty(info.imgUrl)) {
                accountHeadView.setAccountImage(info.imgUrl, false);
//                PicUtil.displayImg(getActivity(), userIcon, info.imgUrl, R.drawable.ch_account);
            }
            if (!TextUtils.isEmpty(info.nickName)) {
                number.setVisibility(View.VISIBLE);
                number.setText(info.nickName);
            } else {
                number.setVisibility(View.VISIBLE);
                number.setText(info.userName);
            }
            accountNumber.setVisibility(View.GONE);
            offText.setVisibility(View.VISIBLE);
            if (info.userFlag == 1) {
                offText.setText("官方");
            } else {
                offText.setText("非官方");
            }
            if (!TextUtils.isEmpty(info.forum_name)) {
                forumNameText.setVisibility(View.VISIBLE);
                forumNameText.setText(info.forum_name);
            } else {
                forumNameText.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(info.grow_name)) {
                levelLayout.setVisibility(View.VISIBLE);
                levelLayout.setVisibility(View.VISIBLE);
                levelNameText.setText(info.grow_name);
                levelValue.setText(info.grow_level);
                accountHeadView.setAccountHeadBg(info.img_mask);
            } else {
                levelLayout.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(info.vip_level) && !TextUtils.isEmpty(info.vip_name)) {
                vipLayout.setVisibility(View.VISIBLE);
                vipLevel.setText(info.vip_level);
                vipName.setText(info.vip_name);
            } else {
                vipLayout.setVisibility(View.GONE);
            }

            AnalyticsHome.umOnProfileSignIn(info.userName);

        } else {
//            userIcon.setImageResource(R.drawable.ch_account);
            accountNumber.setText("未登录");
            number.setVisibility(View.GONE);
            accountNumber.setVisibility(View.VISIBLE);
//            iconCover.setVisibility(View.GONE);
            offText.setVisibility(View.GONE);
            forumNameText.setVisibility(View.GONE);
            payShowChb.setText(0 + "");
            payShowChd.setText(0 + "");
            payShowSilver.setText(0 + "");
            payShowCoupon.setText(0 + "");
//            accountBgImage1.setVisibility(View.VISIBLE);
//            accountBgImage2.setVisibility(View.GONE);
            levelLayout.setVisibility(View.GONE);
            accountHeadView.setAccountImage("", true);
            accountHeadView.setAccountHeadBg("0");
            levelValue.setText("");
            levelNameText.setText("");
            vipLayout.setVisibility(View.GONE);
        }
    }

    private void logout() {
        List<String> list = MiPushClient.getAllUserAccount(AppContext.getAppContext());
        if (list != null) {
            for (String s : list) {
                MiPushClient.unsetUserAccount(AppContext.getAppContext(), s, s);
            }
        }
        msgItemView.setText(0);
        commentItemView.setText(0);
        giftItemView.setText(0);
        dotList.clear();
        notifyDot(View.INVISIBLE);
        setLoginInfo(false, null);
        LoginUserInfo info = new LoginUserInfo();
        info.userId = "0";
        SdkSession.getInstance().setUserInfo(info);
        EventBus.getDefault().post(info);
        TaskNotifyDotEntry event = new TaskNotifyDotEntry();
        event.setStatus(View.INVISIBLE);
        EventBus.getDefault().post(event);
        AnalyticsHome.umProfileSignOff();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setLogout(LogoutEntry entry) {
        logout();
    }

    private int height;

    @Override
    protected void initChildView() {
        accountHeadView = findView(R.id.ch_view_usercenter_icon);
        accountHeadView.getAccountImage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppContext.getAppContext().isLogin()) {
                    doLogin();
                    return;
                }
                if (accountPicHelper == null) {
                    accountPicHelper = new AccountPicHelper(getActivity(), MineFragment.this);
                }
                accountPicHelper.showSelectPicDialog();
            }
        });
        offText = findView(R.id.ch_view_usercenter_off_text);
        forumNameText = findView(R.id.ch_view_usercenter_forum_name);
        accountNumber = findView(R.id.ch_view_usercenter_accountnumber);
        number = findView(R.id.ch_view_usercenter_number);
        userCenterPopBtn = findView(R.id.ch_activity_usercenter_skin);
        bgLayout = findView(R.id.ch_usercenter_bg_layout);
        scrollView = findView(R.id.ch_usercenter_scroll_view);
        bgLayout.post(new Runnable() {
            @Override
            public void run() {
                height = bgLayout.getHeight() - titleLayoutBg.getHeight();
            }
        });
        scrollView.setScrollViewListener(MineFragment.this);
        userCenterPopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopWindow(v);
            }
        });
        titleBg = findView(R.id.ch_activity_usercenter_title_bg);
        titleLayoutBg = findView(R.id.ch_activity_usercenter_title_fl);
        titleLayoutBg.setAlpha(0);
        if (isVersionMoreKitkat()) {
            titleLayoutBg.setPadding(0, ViewUtil.getStatusHeight(AppContext.getAppContext()), 0, 0);
            findView(R.id.ch_activity_usercenter_rl).setPadding(0, ViewUtil.getStatusHeight(AppContext.getAppContext())
                    , 0, 0);
        }

        msgItemView = findView(R.id.ch_activity_usercenter_msg);
        msgItemView.setOnClickListener(this);
        findView(R.id.ch_activity_usercenter_mall).setOnClickListener(this);
        findView(R.id.ch_activity_usercenter_h5).setOnClickListener(this);
        findView(R.id.ch_activity_usercenter_mine_game).setOnClickListener(this);
        giftItemView = findView(R.id.ch_activity_usercenter_gift);
        giftItemView.setOnClickListener(this);
        findView(R.id.ch_activity_usercenter_service).setOnClickListener(this);
//        findView(R.id.ch_activity_usercenter_faq).setOnClickListener(this);
        findView(R.id.ch_activity_usercenter_grow).setOnClickListener(this);
        commentItemView = findView(R.id.ch_activity_usercenter_comment);
        commentItemView.setOnClickListener(this);

        favoriteItemView = findView(R.id.ch_activity_usercenter_favorite);
        favoriteItemView.setOnClickListener(this);

        myMsgItemView = findView(R.id.ch_activity_usercenter_mymsg);
        myMsgItemView.setOnClickListener(this);

        mySendItemView = findView(R.id.ch_activity_usercenter_my_send);
        mySendItemView.setOnClickListener(this);

        findView(R.id.ch_activity_usercenter_vip).setOnClickListener(this);

        accountNumber.setOnClickListener(this);
        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.ch_anim_mine_rotate_cycle);

        levelNameText = ((TextView) findView(R.id.ch_account_level_name));
        levelValue = ((TextView) findView(R.id.ch_account_level_value));
        levelLayout = findView(R.id.ch_view_usercenter_level_layout);
        if (AppContext.getAppContext().isLogin()) {
            loadData(true);
        }
        vipName = findView(R.id.ch_account_vip_name);
        vipLayout = findView(R.id.ch_view_usercenter_vip_layout);
        vipLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppContext.getAppContext().isLogin()) {
                    LoginUserInfo user = SdkSession.getInstance().getUserInfo();
                    if (user != null && user.userFlag == 1) {
                        CHVipActivity.start(v.getContext());
                    } else {
                        CHToast.show(v.getContext(), "暂不具备会员认证资格");
                    }
                }
            }
        });
        vipLevel = findView(R.id.ch_account_vip_value);
        payShowInit();
    }

    private void payShowInit() {
        findView(R.id.ch_mine_pay_show_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppContext.getAppContext().isLogin()) {
                    doLogin();
                    return;
                }
                Intent intent = new Intent(getActivity(), MineWalletActivity.class);
                intent.putExtra("walletEntry", walletEntry);
                getActivity().startActivity(intent);
                AnalyticsHome.umOnEvent(AnalyticsHome.MINE_WALLET, "点击我的钱包次数");
            }
        });
        payShowChb = findView(R.id.ch_mine_pay_show_chb);
        payShowChd = findView(R.id.ch_mine_pay_show_chd);
        payShowSilver = findView(R.id.ch_mine_pay_show_by);
        payShowCoupon = findView(R.id.ch_mine_pay_show_yhj);
        payShowCouponLayout = findView(R.id.ch_mine_pay_show_yhj_layout);
        String couponActive = DataStorage.getCouponActive(getActivity());
        if (TextUtils.isEmpty(couponActive) || "0".equals(couponActive)) {
            payShowCouponLayout.setVisibility(View.GONE);
        } else {
            payShowCouponLayout.setVisibility(View.VISIBLE);
        }
        payShowPay = findView(R.id.ch_mine_pay_show_pay);
        payShowPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppContext.getAppContext().isLogin()) {
                    doLogin();
                    return;
                }
                String userId = AppContext.getAppContext().getUser().userId;
                boolean payCheck = DataStorage.getPayCheck(AppContext.getAppContext(), userId);
                if (payCheck) {
                    enterPayActivity();
                } else {
                    payCheckDialog();
                }
            }
        });
    }


    @Override
    public void onScrollChanged(KnowHeightScrollView scrollView, int x, int y, int oldx, int oldy) {
        float percent = 0;
        if (height != 0) {
            percent = (y * 1f) / (height * 1f);
        }
        if (percent >= 0 && percent <= 1) {
            titleLayoutBg.setAlpha(percent);
        } else if (percent > 1) {
            titleLayoutBg.setAlpha(1);
        }
    }

    private void showPopWindow(View anchor) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.ch_view_app_menu, null);
        TextView accountSetting = (TextView) view.findViewById(R.id.ch_pop_usercenter_account_setting);
        TextView appSetting = (TextView) view.findViewById(R.id.ch_pop_usercenter_app_setting);
        final TextView logout = (TextView) view.findViewById(R.id.ch_pop_usercenter_loginout);
        if (AppContext.getAppContext().isLogin()) {
            logout.setVisibility(View.VISIBLE);
            accountSetting.setVisibility(View.VISIBLE);
        }
        final PopupWindow popWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popWindow.setOutsideTouchable(true);
        LinearLayout popMenu = (LinearLayout) view.findViewById(R.id.ch_view_pop_menu);
        popMenu.setFocusable(true);
        popMenu.setFocusableInTouchMode(true);
        popWindow.setBackgroundDrawable(new ColorDrawable());
        int dimensionY = (int) getResources().getDimension(R.dimen.ch_pop_y);
        int dimensionX = (int) getResources().getDimension(R.dimen.ch_pop_x);
        setBackgroundAlpha(0.5f, getActivity());
        popWindow.showAsDropDown(anchor, dimensionX, dimensionY);
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1f, getActivity());
            }
        });
        popWindow.showAsDropDown(anchor, dimensionX, dimensionY);
        popMenu.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                    popWindow.dismiss();
                }
                return false;
            }
        });

        accountSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AccountSettingActivity.class));
                popWindow.dismiss();
            }
        });
        appSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SystemSetupActivity.class));
                popWindow.dismiss();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataStorage.setAppLogin(getContext(), false);
                popWindow.dismiss();
                logout();
            }
        });
    }

    public void setBackgroundAlpha(float bgAlpha, Activity activity) {
        WindowManager.LayoutParams lp = activity.getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        activity.getWindow().setAttributes(lp);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnIntent);
        if (resultCode != Activity.RESULT_OK)
            return;

        if (accountPicHelper == null && getActivity() != null) {
            accountPicHelper = new AccountPicHelper(getActivity(), this);
        }
        accountPicHelper.handleResult(requestCode, accountHeadView.getAccountImage(), imageReturnIntent);
    }

    @Override
    public void onClick(View v) {

        if (ViewUtil.setClickLimit(v, 1000, null)) {
            return;
        }

        switch (v.getId()) {
            case R.id.ch_activity_usercenter_mymsg: //专区消息
                MyMsgActivity.start(getActivity());
                AnalyticsHome.umOnEvent(AnalyticsHome.MINE_FORUM_MESSAGE, "专区消息二级界面");
                break;
            case R.id.ch_activity_usercenter_my_send:
                MineSendContentActivity.start(getActivity());
                AnalyticsHome.umOnEvent(AnalyticsHome.MINE_PUBLISH, "我的发布二级界面");
                break;
            case R.id.ch_activity_usercenter_vip:
                CHVipActivity.start(v.getContext());
                break;
            case R.id.ch_activity_usercenter_favorite:
                MyFavoriteActivity.start(getActivity());
                AnalyticsHome.umOnEvent(AnalyticsHome.MINE_COLLECT, "我的收藏二级界面");
                break;
            case R.id.ch_activity_usercenter_gift: //我的礼包
                if (giftItemView.getRedIconVisible()) {
                    changeDotList();
                    giftItemView.setText(0);
                    msgLogic(2);
                }
                GiftCenterActivity.start(getActivity(), 2);
                AnalyticsHome.umOnEvent(AnalyticsHome.ACCOUNT_USER_CENTER_GIFT, "我的礼包二级界面");
                break;
            case R.id.ch_activity_usercenter_mine_game: //我的游戏
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), MineGameActivity.class);
                    getActivity().startActivity(intent);
                    AnalyticsHome.umOnEvent(AnalyticsHome.ACCOUNT_USER_CENTER_MIME_GAME, "我的游戏二级界面");
                }
                break;
            case R.id.ch_activity_usercenter_msg:
                if (msgItemView.getRedIconVisible()) {
                    changeDotList();
                    msgItemView.setText(0);
                    msgLogic(3);
                    startWebPage("messageCenter", tipsEntry);
                } else {
                    startWebPage("messageCenter");
                }
                AnalyticsHome.umOnEvent(AnalyticsHome.ACCOUNT_USER_CENTER_MSG, "消息中心二级界面");
                break;
            case R.id.ch_activity_usercenter_comment:
                if (commentItemView.getRedIconVisible()) {
                    changeDotList();
                    commentItemView.setText(0);
                    msgLogic(1);
                }
                WebActivity.startMyComment(getActivity(), "https://app-sdk.caohua.com/ucenter/myComment");
                AnalyticsHome.umOnEvent(AnalyticsHome.ACCOUNT_USER_CENTER_COMMENT, "我的评论二级界面");
                break;
            case R.id.ch_activity_usercenter_grow:
                WebActivity.startWebPageGetParam(getActivity(), "https://passport-sdk.caohua.com/grow/index", null);
                AnalyticsHome.umOnEvent(AnalyticsHome.MINE_GROW, "我的成长二级界面");
                break;
            case R.id.ch_activity_usercenter_service:
                startWebPage("contactKefu");
                AnalyticsHome.umOnEvent(AnalyticsHome.ACCOUNT_USER_CENTER_SERVICE, "联系客服二级界面");
                break;
        }
    }

    private void enterPayActivity() {
        Intent intent = new Intent(getActivity(), PayActionActivity.class);
        startActivity(intent);
        AnalyticsHome.umOnEvent(AnalyticsHome.ACCOUNT_USER_CENTER_PAY, "充值草花币二级界面");
    }

    private void payCheckDialog() {
        if (getActivity() == null) {
            return;
        }
        PayCheckLogic checkLogic = new PayCheckLogic();
        final LoadingDialog loadingDialog = new LoadingDialog(getActivity(), true);
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
        if (getActivity() == null) {
            return;
        }
        final CHAlertDialog chAlertDialog = new CHAlertDialog(getActivity());
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

    private void changeDotList() {
        if (dotList.size() > 0) {
            dotList.remove(dotList.size() - 1);
        }
        if (dotList.size() == 0) {
            //没有小红点
            notifyDot(View.INVISIBLE);
        }
    }

    private void notifyDot(int invisible) {
        MineDotEntry mineDotEntry = new MineDotEntry();
        mineDotEntry.setStatus(invisible);
        EventBus.getDefault().post(mineDotEntry);
    }

    private void msgLogic(int step) {
        if (tipsEntry == null) {
            return;
        }
        new DealTipsLogic(tipsEntry, step).getDealTips(null);
    }

    private void startWebPage(String pathName) {
        WebActivity.startAppLink(getActivity(), "https://app-sdk.caohua.com/ucenter/" + pathName);
    }

    private void startWebPage(String pathName, TipsEntry entry) {
        WebActivity.startMessageCenter(getActivity(), "https://app-sdk.caohua.com/ucenter/" + pathName, entry);
    }


    private void loadData(boolean isRefresh) {
        WalletInfoLogic logic = new WalletInfoLogic(WalletInfoLogic.TYPE_ALL, isRefresh, new BaseLogic.AppLogicListner() {
            @Override
            public void success(Object entryResult) {
                if (entryResult instanceof WalletEntry) {
                    if (AppContext.getAppContext().isLogin()) {
                        walletEntry = (WalletEntry) entryResult;
                        payShowChb.setText(walletEntry.chb);
                        payShowChd.setText(walletEntry.chd);
                        payShowSilver.setText(walletEntry.silver);
                        payShowCoupon.setText(walletEntry.coupon);
                    }
                }
            }

            @Override
            public void failed(String errorMsg) {
            }
        });
        logic.getBalance();
    }

    /**
     * 登陆的时候刷新这个界面
     *
     * @param info
     */
    @Subscribe
    public void refreshLogin(LoginUserInfo info) {
        if (info != null) {
            setLoginInfo(true, info);
            loadData(true);
        }
    }

    /**
     * 刷新token失败时
     *
     * @param action
     */
    @Subscribe
    public void refreshLogout(String action) {
//        LogUtil.errorLog("refreshLogout:" + action + "," + getClass().getName());
//        if (!hasShown && getActivity() != null) {
//            hasShown = true;
//            CHToast.show(getActivity(), "账号信息验证出错, 请重新登录", Toast.LENGTH_LONG);
//        }
//        logout();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (accountPicHelper != null) {
            if (accountPicHelper.getUri() != null) {
                LogUtil.errorLog("MineFragment onSaveInstanceState:" + accountPicHelper.getUri().toString());
                outState.putString("uri", accountPicHelper.getUri().toString());
            }
            if (accountPicHelper.getCropSaveFile() != null) {
                LogUtil.errorLog("MineFragment onSaveInstanceState:" + accountPicHelper.getCropSaveFile().toString());
                outState.putString("file", accountPicHelper.getCropSaveFile().toString());
            }
        }
    }
}
