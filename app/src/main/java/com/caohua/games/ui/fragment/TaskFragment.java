package com.caohua.games.ui.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.task.TaskForHomeNotify;
import com.caohua.games.biz.task.TaskLogic;
import com.caohua.games.ui.adapter.ItemDivider;
import com.caohua.games.ui.adapter.TaskRecyclerAdapter;
import com.caohua.games.ui.find.FindContentActivity;
import com.caohua.games.ui.widget.BlankLoginView;
import com.caohua.games.ui.widget.EmptyView;
import com.caohua.games.ui.widget.SubActivityTitleView;
import com.chsdk.biz.BaseLogic;
import com.chsdk.model.login.LoginUserInfo;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhou on 2017/2/20.
 */

public class TaskFragment extends NormalFragment {
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private SmartRefreshLayout refreshLayout;
    private static final long UPDATE_INTERVAL = 10 * 1000;
    private TaskRecyclerAdapter taskAdapter;
    private boolean notLogin;
    private BlankLoginView blankLogin;
    private EmptyView empty;

    @Override
    protected int getLayoutId() {
        return R.layout.ch_fragment_task;
    }

    @Override
    protected void initChildView() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        refreshLayout = findView(R.id.ch_task_swipe_refresh);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                taskLogic(notLogin);

            }
        });
        recyclerView = findView(R.id.ch_task_recycler_view);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        ArrayList<Object> list = new ArrayList<>();
        taskAdapter = new TaskRecyclerAdapter(getActivity(), list);
        recyclerView.setAdapter(taskAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new ItemDivider().setDividerWidth(2).setDividerColor(0xffeeeeee));
//        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                int topRowVerticalPosition =
//                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
//                refreshLayout.setEnabled(topRowVerticalPosition >= 0);
//            }
//
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//        });
        blankLogin = findView(R.id.ch_task_blank_login);
        if (!blankLogin.isLogin()) {
            blankLogin.show(new BlankLoginView.BlankLoginListener() {
                @Override
                public void onBlankLogin(LoginUserInfo info) {
                    taskLogic(true);
                }
            });
        } else {
            taskLogic(true);
        }

        ((SubActivityTitleView) findView(R.id.ch_task_sub_title_view)).getBackImage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    ((FindContentActivity) getActivity()).openWelcomeActivity(getActivity(), 2);
                    getActivity().finish();
                }
            }
        });

        empty = findView(R.id.ch_task_empty);
    }

    protected void showEmptyView(boolean showEmpty) {
        if (showEmpty) {
            empty.setVisibility(View.VISIBLE);
            empty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    empty.setVisibility(View.GONE);
                    taskLogic(true);
                }
            });
        } else {
            empty.setVisibility(View.GONE);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!appContext().isLogin()) {

        } else {
            taskLogic(notLogin);
        }
    }

    private void taskLogic(boolean refresh) {
        if (!getUserVisibleHint()) {
            return;
        }

        new TaskLogic(refresh).getTaskEntry(new BaseLogic.AppLogicListner() {
            @Override
            public void failed(String errorMsg) {
                refreshClose(false);
                notLogin = false;
                if ("delay".equals(errorMsg)) {
                    return;
                }

                if (taskAdapter == null || taskAdapter.getItemCount() == 0) {
                    if (!appContext().isNetworkConnected()) {
                        showNoNetworkView(true);
                        return;
                    }
                }

                if (taskAdapter == null || taskAdapter.getItemCount() == 0) {
                    showEmptyView(true);
                }
            }

            @Override
            public void success(Object entryResult) {
                showNoNetworkView(false);
                showEmptyView(false);
                refreshClose(true);
                notLogin = false;
                if (entryResult instanceof List) {
                    List<Object> objectList = (List<Object>) entryResult;
                    taskAdapter.clearAll();
                    taskAdapter.addAll(objectList);
                }
                if (taskAdapter == null || taskAdapter.getItemCount() == 0) {
                    showEmptyView(true);
                }
            }
        });
    }

    @Subscribe
    public void notifyData(TaskForHomeNotify entry) {
        if (appContext().isLogin() && entry != null) {
            new TaskLogic(entry.isRefresh() || notLogin).getTaskEntry(new BaseLogic.AppLogicListner() {
                @Override
                public void failed(String errorMsg) {
                    notLogin = false;
                    refreshClose(false);
                    if ("delay".equals(errorMsg)) {
                        return;
                    }

                    if (taskAdapter == null || taskAdapter.getItemCount() == 0) {
                        if (!appContext().isNetworkConnected()) {
                            showNoNetworkView(true);
                            return;
                        }
                    }

                    if (taskAdapter == null || taskAdapter.getItemCount() == 0) {
                        showEmptyView(true);
                    }
                }

                @Override
                public void success(Object entryResult) {
                    showNoNetworkView(false);
                    showEmptyView(false);
                    notLogin = false;
                    refreshClose(true);
                    if (entryResult instanceof List) {
                        List<Object> objectList = (List<Object>) entryResult;
                        taskAdapter.clearAll();
                        taskAdapter.addAll(objectList);
                    }

                    if (taskAdapter == null || taskAdapter.getItemCount() == 0) {
                        showEmptyView(true);
                    }
                }
            });
        }
    }

    private void refreshClose(boolean success) {
        refreshLayout.finishRefresh(success);
    }

    public AppContext appContext() {
        return AppContext.getAppContext();
    }
}
