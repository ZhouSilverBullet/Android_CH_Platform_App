package com.caohua.games.ui.fragment;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.account.HomePageCommentEntry;
import com.caohua.games.biz.account.HomePageCommentLogic;
import com.caohua.games.ui.account.HomePageCommentItemView;
import com.caohua.games.ui.prefecture.InnerLoadListView;
import com.caohua.games.ui.widget.BlankLoginView;
import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.SdkSession;
import com.chsdk.model.login.LoginUserInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by zhouzhou on 2017/5/28.
 */

public class CommentFragment extends NormalFragment {
    private InnerLoadListView innerLoadListView;
    private CommentAdapter<HomePageCommentEntry> adapter;
    private View emptyView;
    private BlankLoginView blankLogin;

    @Override
    protected void initChildView() {
        innerLoadListView = findView(R.id.ch_activity_tiezi_list);
        innerLoadListView.setLoadMoreUnable(true);
        emptyView = findView(R.id.ch_fragment_tie_zi_empty);
        blankLogin = findView(R.id.ch_fragment_tie_zi_blank_login);
        adapter = new CommentAdapter<>(new ArrayList<HomePageCommentEntry>());
        innerLoadListView.setAdapter(adapter);
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tieZiLogic(getActivity(), 0, "");
            }
        });
        if (blankLogin.isLogin()) {
            tieZiLogic(getActivity(), 0, "");
        } else {
            blankLogin.show(new BlankLoginView.BlankLoginListener() {
                @Override
                public void onBlankLogin(LoginUserInfo info) {
                    tieZiLogic(getActivity(), 0, "");
                }
            });
        }
        innerLoadListView.setOnLoadListener(new InnerLoadListView.OnLoadListener() {
            @Override
            public void onLoad(int page) {
                tieZiLogic(getActivity(), adapter.getCount(), "");
            }
        });

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void loadData() {
        tieZiLogic(getActivity(), 0, "");
    }

    @Subscribe
    public void notifyToLoginComment(LoginUserInfo info) {
        if (blankLogin != null && blankLogin.isLogin()) {
            tieZiLogic(getActivity(), 0, "");
            blankLogin.setVisibility(View.GONE);
        }
    }

    private void tieZiLogic(Context context, int page, String toUserId) {
        HomePageCommentLogic logic = new HomePageCommentLogic();

        logic.getData(SdkSession.getInstance().getUserId(), "10", page + "", new BaseLogic.DataLogicListner<List<HomePageCommentEntry>>() {
            @Override
            public void failed(String errorMsg, int errorCode) {
                loadMoreFinish(true);
                if (AppContext.getAppContext().isNetworkConnected()) {
                    if (!hasData()) {
                        showNoNetworkView(true);
                    }
                    return;
                }
                emptyView.setVisibility(hasData() ? View.GONE : View.VISIBLE);
            }

            @Override
            public void success(List<HomePageCommentEntry> entryResult) {
                showNoNetworkView(false);
                if (entryResult != null) {
                    loadMoreFinish(true);
                    if (adapter != null) {
                        adapter.addAll(entryResult);
                    }
                } else {
                    loadMoreFinish(false);
                }
                emptyView.setVisibility(hasData() ? View.GONE : View.VISIBLE);
            }
        });
    }

    protected void loadMoreFinish(boolean success) {
        innerLoadListView.loadComplete(!success);
        innerLoadListView.setLoadMoreUnable(!success);
    }

    private boolean hasData() {
        return adapter != null && adapter.getCount() > 0;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ch_fragment_tie_zi;
    }

    class CommentAdapter<T> extends BaseAdapter {
        List<T> data;

        public CommentAdapter(List<T> data) {
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public T getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void addAll(Collection<T> collection) {
            if (data != null) {
                data.addAll(collection);
                notifyDataSetChanged();
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new HomePageCommentItemView(getActivity());
            }
            HomePageCommentItemView view = (HomePageCommentItemView) convertView;
            view.setData(getItem(position));
            view.setUserId(SdkSession.getInstance().getUserId());
            return convertView;
        }
    }

}
