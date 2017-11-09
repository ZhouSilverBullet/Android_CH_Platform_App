package com.caohua.games.ui.vip;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.caohua.games.R;
import com.caohua.games.ui.BaseActivity;
import com.caohua.games.ui.widget.BlankLoginView;
import com.caohua.games.ui.widget.NoNetworkView;
import com.caohua.games.ui.widget.SubActivityTitleView;
import com.chsdk.model.login.LoginUserInfo;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

/**
 * Created by admin on 2017/8/29.
 */

public abstract class CommonActivity extends BaseActivity {

    protected SmartRefreshLayout refreshLayout;
    private View empty;
    private View loadingProgress;
    protected CommonActivity activity;
    protected SubActivityTitleView titleView;
    protected final static String IS_VIP = "is_vip";
    protected final static String IS_AUTH = "is_auth";
    private BlankLoginView blankLogin;
    private NoNetworkView noNetworkView;
    private FrameLayout fLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch_activity_common);
        initVariables();
        baseInit();
        initView();
        if (hasLogin()) {
            if (!blankLogin.isLogin()) {
                blankLogin.show(new BlankLoginView.BlankLoginListener() {
                    @Override
                    public void onBlankLogin(LoginUserInfo info) {
                        loadData();
                    }
                });
                return;
            }
        }
        loadData();
    }

    protected void initVariables() {
    }

    private void baseInit() {
        activity = this;
        refreshLayout = getView(R.id.ch_activity_common_rl);
        fLayout = getView(R.id.ch_activity_common_fl);
        fLayout.addView(LayoutInflater.from(this).inflate(childViewId(), null),
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        refreshLayout.setEnabled(childIsRefresh());
        refreshLayout.setEnableLoadmore(childIsLoadMore());
        loadingProgress = getView(R.id.ch_activity_common_progress);
        empty = getView(R.id.ch_activity_common_empty);
        titleView = (SubActivityTitleView) findViewById(R.id.ch_activity_common_title_view);
        titleView.setTitle(subTitle());
        titleView.getBackImage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWelcomeActivity(v.getContext(), 1);
                finish();
            }
        });

        blankLogin = getView(R.id.ch_activity_common_blank_login);
        noNetworkView = getView(R.id.ch_activity_common_no_network);
    }

    private boolean childIsLoadMore() {
        return false;
    }

    protected void showNoNetworkView(boolean showNoNetwork) {
        if (showNoNetwork) {
            noNetworkView.setVisibility(View.VISIBLE);
            noNetworkView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noNetworkView.setVisibility(View.GONE);
                    loadData();
                }
            });
        } else {
            noNetworkView.setVisibility(View.GONE);
        }
    }

    protected void showEmptyView(boolean showEmpty) {
        if (showEmpty) {
            empty.setVisibility(View.VISIBLE);
            empty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    empty.setVisibility(View.GONE);
                    loadData();
                }
            });
        } else {
            empty.setVisibility(View.GONE);
        }
    }

    protected void showLoadProgress(boolean showAnim) {
        loadingProgress.setVisibility(showAnim ? View.VISIBLE : View.GONE);
    }

    protected boolean hasLogin() {
        return false;
    }

    protected abstract String subTitle();

    protected abstract int childViewId();

    protected abstract void initView();

    protected abstract void loadData();

    public static void start(Context context, Class clazz) {
        Intent intent = new Intent(context, clazz);
        context.startActivity(intent);
    }

    public boolean childIsRefresh() {
        return false;
    }

    public void handleAllHaveData() {
        showLoadProgress(false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        openWelcomeActivity(this, 1);
    }
}
