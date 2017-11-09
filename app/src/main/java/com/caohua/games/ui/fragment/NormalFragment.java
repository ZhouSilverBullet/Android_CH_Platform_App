package com.caohua.games.ui.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.caohua.games.R;
import com.caohua.games.ui.widget.NoNetworkView;
import com.chsdk.utils.LogUtil;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by zhouzhou on 2017/2/20.
 */

public abstract class NormalFragment extends Fragment {

    protected RelativeLayout mRoot;
    public boolean position;
    private String pageName;
    protected FragmentActivity activity;
    protected NoNetworkView noNetworkView;
    private FrameLayout innerContainer;

    public NormalFragment() {
        pageName = getClass().getSimpleName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRoot = (RelativeLayout) inflater.inflate(R.layout.ch_fragment_normal, container, false);
        activity = getActivity();
        innerContainer = findView(R.id.ch_fragment_normal_container);
        LayoutInflater.from(activity).inflate(getLayoutId(), innerContainer, true);
        noNetworkView = findView(R.id.ch_fragment_normal_no_network);
        initChildView();
        return mRoot;
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

    protected void loadData() {

    }

    protected abstract void initChildView();

    protected abstract int getLayoutId();

    protected <T extends View> T findView(int viewId) {
        return (T) mRoot.findViewById(viewId);
    }

    @Override
    public void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    protected <T extends Fragment> T getFragment(Bundle savedState, Class cls) {
        return (T) getChildFragmentManager().getFragment(savedState,
                cls.getSimpleName());
    }

    protected void saveFragment(Bundle outState, Fragment fragment) {
        try {
            if (fragment.isAdded()) {
                getChildFragmentManager().putFragment(outState,
                        fragment.getClass().getSimpleName(), fragment);
            }
        } catch (Exception e) {
            LogUtil.errorLog("saveFragment error:" + e.getMessage());
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            onVisibilityChangedToUser(true, false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getUserVisibleHint()) {
            onVisibilityChangedToUser(false, false);
        }
    }

    public void onVisibilityChangedToUser(boolean isVisibleToUser, boolean isHappenedInSetUserVisibleHintMethod) {
        if (isVisibleToUser) {
            if (pageName != null) {
                MobclickAgent.onPageStart(pageName);
                LogUtil.errorLog("UmengPageTrack", pageName + " - display - " + (isHappenedInSetUserVisibleHintMethod ? "setUserVisibleHint" : "onResume"));
            }
        } else {
            if (pageName != null) {
                MobclickAgent.onPageEnd(pageName);
                LogUtil.errorLog("UmengPageTrack", pageName + " - hidden - " + (isHappenedInSetUserVisibleHintMethod ? "setUserVisibleHint" : "onPause"));
            }
        }
    }

    public boolean isVersionMoreKitkat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return true;
        } else {
            return false;
        }
    }
}
