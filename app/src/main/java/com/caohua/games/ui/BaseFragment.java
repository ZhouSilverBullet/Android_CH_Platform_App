package com.caohua.games.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.TransmitDataInterface;
import com.caohua.games.biz.home.DataMgr;
import com.caohua.games.ui.widget.BlankLoginView;
import com.caohua.games.ui.widget.EmptyView;
import com.caohua.games.ui.widget.NoNetworkView;
import com.chsdk.api.AppOperator;
import com.chsdk.biz.BaseLogic;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.utils.LogUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/10/16.
 */

public abstract class BaseFragment extends Fragment {
    public static final String KEY_INTENT_DATA = "data";
    private String pageName;
    protected boolean hasLoadData;
    protected boolean isLoading;
    protected View mRoot;
    protected EmptyView emptyView;
    protected View loadingProgress;
    protected SmartRefreshLayout refreshLayout;
    protected FrameLayout containerLayout;
    private BlankLoginView blankLogin;
    private NoNetworkView noNetworkView;

    public BaseFragment() {
        pageName = getClass().getSimpleName();
    }

    protected abstract int getLayoutId();

    protected void initChildView() {
    }

    protected abstract List<LoadParams> getDataType();

    protected void handleData(LoadParams param, Object data) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRoot != null) {
            ViewGroup parent = (ViewGroup) mRoot.getParent();
            if (parent != null)
                parent.removeView(mRoot);
        } else {
            initParentView(inflater, container);
            initChildView();
            initLoad();
        }
        return mRoot;
    }

    private void initParentView(LayoutInflater inflater, ViewGroup container) {
        mRoot = inflater.inflate(R.layout.ch_base_fragment, container, false);
        loadingProgress = findView(R.id.ch_base_fragment_loading_progress);
        emptyView = findView(R.id.ch_base_fragment_empty);
        noNetworkView = findView(R.id.ch_base_fragment_no_network);
        refreshLayout = findView(R.id.ch_base_fragment_refresh);
        containerLayout = findView(R.id.ch_base_fragment_container);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                freshLoad();
            }
        });
        refreshLayout.setEnableLoadmore(false);
        blankLogin = findView(R.id.ch_base_fragment_blank_login);
        if (hasLogin()) {
            if (!blankLogin.isLogin()) {
                blankLogin.show(new BlankLoginView.BlankLoginListener() {
                    @Override
                    public void onBlankLogin(LoginUserInfo info) {
                        refreshLayout.autoRefresh();
                    }
                });
            }
        }
        containerLayout.addView(inflater.inflate(getLayoutId(), container, false));
    }

    protected boolean hasLogin() {
        return false;
    }

    protected void setRefreshEnable(final boolean enable) {
        refreshLayout.setEnabled(enable);
        refreshLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return !enable;
            }
        });
    }

    public void loadData(boolean loadLocal) {
        isLoading = true;
        List<LoadParams> types = getDataType();
        if (types != null && types.size() > 0) {
            for (LoadParams params : types) {
                params.loadLoacl = loadLocal;
                final LoadParams tempParams = params;
                DataLoader dataLoader = new DataLoader(params, new TransmitDataInterface() {
                    @Override
                    public void transmit(Object o) {
                        if (getActivity() != null && !getActivity().isFinishing()) {
                            handleData(tempParams, o);
                        } else {
                            LogUtil.errorLog("getActivity()-->" + getActivity());
                        }
                    }
                });
                if (hasLogin()) {
                    if (!blankLogin.isLogin()) {
                        isLoading = false;
                        return;
                    }
                }
                dataLoader.load();
            }
        }
    }

    protected void initLoad() {
        showLoadProgress(true);
        loadData(true);
    }

    protected void freshLoad() {
        loadData(false);
    }

    public <T> List<T> getIntentListData() {
        if (getActivity() == null || getActivity().getIntent() == null) {
            return null;
        }

        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle == null) {
            return null;
        }

        if (bundle.get(KEY_INTENT_DATA) instanceof List) {
            return (List<T>) bundle.get(KEY_INTENT_DATA);
        }

        return null;
    }

    public Object getIntentData() {
        if (getActivity() == null || getActivity().getIntent() == null) {
            return 0;
        }

        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle == null) {
            return 0;
        }
        return bundle.get(KEY_INTENT_DATA);
    }

    protected void showNoNetworkView(boolean showNoNetwork) {
        if (showNoNetwork && !hasLoadData) {
            noNetworkView.setVisibility(View.VISIBLE);
            noNetworkView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noNetworkView.setVisibility(View.GONE);
                    showLoadProgress(true);
                    loadData(false);
                }
            });
        } else {
            noNetworkView.setVisibility(View.GONE);
        }
    }

    protected void showEmptyView(boolean empty) {
        if (empty && !hasLoadData) {
            refreshLayout.setEnabled(false);
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setOnEmptyListener(new EmptyView.OnEmptyClickListener() {
                @Override
                public void emptyClick() {
                    if (canEmptyViewClick()) {
                        emptyView.setVisibility(View.GONE);
                        showLoadProgress(true);
                        loadData(false);
                    }
                }
            });
        } else {
            emptyView.setVisibility(View.GONE);
            refreshLayout.setEnabled(true);
        }
    }

    protected boolean canEmptyViewClick() {
        return true;
    }

    protected void showLoadProgress(boolean showAnim) {
        loadingProgress.setVisibility(showAnim ? View.VISIBLE : View.GONE);
    }

    private boolean isHaveDataSuccess = false;

    // 这里因为是多次加载所以有一个成功了，我就让它成功
    protected void serverLoadResult(boolean success) {
        if (success) {
            isHaveDataSuccess = true;
        }
        if (success) {
            isLoading = false;
            refreshLayout.finishRefresh();
            hasLoadData = true;
            showLoadProgress(false);
            showEmptyView(false);
            showNoNetworkView(false);
        } else {
            isLoading = false;
            refreshLayout.finishRefresh(400, isHaveDataSuccess);
            showLoadProgress(false);
            if (!AppContext.getAppContext().isNetworkConnected()) {
                showNoNetworkView(true);
                return;
            }
            isHaveDataSuccess = false;
            showEmptyView(true);
        }
    }

    protected <T extends View> T findView(int viewId) {
        return (T) mRoot.findViewById(viewId);
    }

    protected class DataLoader {
        private TransmitDataInterface dataListener;
        private LoadParams loadParams;

        public DataLoader(LoadParams params, TransmitDataInterface dataListener) {
            this.loadParams = params;
            this.dataListener = dataListener;
        }

        public void load() {
            if (loadParams.loadLoacl) {
                loadLocal();
            }
            loadServer();
        }

        protected void loadServer() {
            DataMgr.getPageServerData(getActivity(), new BaseLogic.AppLogicListner() {
                @Override
                public void failed(String errorMsg) {
                    serverLoadResult(false);
                }

                @Override
                public void success(final Object entryResult) {
                    serverLoadResult(true);
                    if (dataListener != null) {
                        dataListener.transmit(entryResult);
                    }
                }
            }, loadParams);
        }

        protected void loadLocal() {
            AppOperator.runOnThread(new Runnable() {
                @Override
                public void run() {
                    final List<? extends Serializable> list = DataMgr.getLocalData(getActivity(), loadParams.requestType);
                    if (list != null && list.size() > 0) {
                        hasLoadData = true;
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showEmptyView(false);
                                    showNoNetworkView(false);
                                    if (dataListener != null) {
                                        dataListener.transmit(list);
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
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

    public static class LoadParams {
        public boolean loadLoacl;
        public int requestType;
        public Object object;
    }
}
