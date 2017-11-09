package com.caohua.games.ui.account;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.account.HomePagePublishEntry;
import com.caohua.games.biz.account.HomePagePublishLogic;
import com.caohua.games.ui.widget.BottomLoadInnerListView;
import com.caohua.games.ui.widget.EmptyView;
import com.caohua.games.ui.widget.NoNetworkView;
import com.chsdk.biz.BaseLogic;
import com.culiu.mhvp.core.InnerScroller;
import com.culiu.mhvp.core.InnerScrollerContainer;
import com.culiu.mhvp.core.OuterScroller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZengLei on 2017/5/23.
 */

public class HomePagePublishFragment extends Fragment implements InnerScrollerContainer {

    private BottomLoadInnerListView innerListView;
    private EmptyView emptyView;
    protected OuterScroller mOuterScroller;
    protected int mIndex;
    private List<HomePagePublishEntry> gameList;
    protected int loadedCount;
    private ListAdapter listAdapter;
    private NoNetworkView noNetworkView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ch_fragment_home_page_game, container, false);
        innerListView = (BottomLoadInnerListView) view.findViewById(R.id.ch_fragment_home_page_game_list);
        emptyView = (EmptyView) view.findViewById(R.id.ch_fragment_home_page_game_empty);
        noNetworkView = (NoNetworkView) view.findViewById(R.id.ch_fragment_home_page_game_no_network);
        innerListView.register2Outer(mOuterScroller, mIndex);
        innerListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        innerListView.setOnLoadListener(new BottomLoadInnerListView.OnLoadListener() {
            @Override
            public void onLoad() {
                loadedCount = hasData() ? gameList.size() : 0;
                getData();
            }
        });
        getData();
        return view;
    }

    @Override
    public void setOuterScroller(OuterScroller outerScroller, int myPosition) {
        if (outerScroller == mOuterScroller && myPosition == mIndex) {
            return;
        }
        mOuterScroller = outerScroller;
        mIndex = myPosition;

        if (getInnerScroller() != null) {
            getInnerScroller().register2Outer(mOuterScroller, mIndex);
        }
    }

    @Override
    public InnerScroller getInnerScroller() {
        return innerListView;
    }

    private void getData() {
        HomePagePublishLogic logic = new HomePagePublishLogic();
        String userid = getActivity().getIntent().getStringExtra("userid");
        logic.getData(userid, "10", String.valueOf(loadedCount), new BaseLogic.DataLogicListner<List<HomePagePublishEntry>>() {
            @Override
            public void failed(String errorMsg, int errorCode) {
                loadMoreFinish(true);
                if (!hasData() && !AppContext.getAppContext().isNetworkConnected()) {
                    noNetworkView.setVisibility(View.VISIBLE);
                    return;
                }
                noNetworkView.setVisibility(View.GONE);
                emptyView.setVisibility(hasData() ? View.GONE : View.VISIBLE);
            }

            @Override
            public void success(List<HomePagePublishEntry> entryResult) {
                noNetworkView.setVisibility(View.GONE);
                if (entryResult != null) {
                    setData(entryResult);
                    loadMoreFinish(true);
                } else {
                    loadMoreFinish(false);
                }
                emptyView.setVisibility(hasData() ? View.GONE : View.VISIBLE);
            }
        });
    }

    protected void loadMoreFinish(boolean success) {
        innerListView.loadComplete(!success);
        innerListView.setLoadMoreUnable(!success);
    }

    private boolean hasData() {
        return gameList != null && gameList.size() > 0;
    }

    public void setData(List<HomePagePublishEntry> data) {
        if (gameList == null) {
            gameList = new ArrayList<>();
        }
        gameList.addAll(data);
        setView();
    }

    private void setView() {
        if (emptyView == null) {
            return;
        }
        if (gameList == null || gameList.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
            setList();
        }
    }

    private void setList() {
        if (listAdapter == null) {
            listAdapter = new ListAdapter(gameList);
            innerListView.setAdapter(listAdapter);
        } else {
            listAdapter.notifyDataSetChanged();
        }
    }

    class ListAdapter<T> extends BaseAdapter {
        List<T> data;
        public ListAdapter(List<T> data) {
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

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new HomePagePublishItemView(getActivity());
            }
            HomePagePublishItemView view = (HomePagePublishItemView) convertView;
            view.setData(getItem(position));
            return convertView;
        }
    }
}
