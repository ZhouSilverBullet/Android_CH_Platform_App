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
import com.caohua.games.biz.account.HomePageGameEntry;
import com.caohua.games.ui.widget.BottomLoadInnerListView;
import com.caohua.games.ui.widget.EmptyView;
import com.caohua.games.ui.widget.NoNetworkView;
import com.culiu.mhvp.core.InnerScroller;
import com.culiu.mhvp.core.InnerScrollerContainer;
import com.culiu.mhvp.core.OuterScroller;

import java.util.List;

/**
 * Created by ZengLei on 2017/5/23.
 */

public class HomePageGameFragment extends Fragment implements InnerScrollerContainer {

    private BottomLoadInnerListView innerListView;
    private EmptyView emptyView;
    protected OuterScroller mOuterScroller;
    protected int mIndex;
    private List<HomePageGameEntry> gameList;
    private ListAdapter listAdapter;
    private NoNetworkView noNetworkView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ch_fragment_home_page_game, container, false);
        innerListView = (BottomLoadInnerListView) view.findViewById(R.id.ch_fragment_home_page_game_list);
        innerListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        innerListView.setLoadMoreUnable(true);
        emptyView = (EmptyView) view.findViewById(R.id.ch_fragment_home_page_game_empty);
        noNetworkView = (NoNetworkView) view.findViewById(R.id.ch_fragment_home_page_game_no_network);
        innerListView.register2Outer(mOuterScroller, mIndex);
        setView();
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

    public void setData(List<HomePageGameEntry> gameList) {
        this.gameList = gameList;
        setView();
    }

    private void setView() {
        if (emptyView == null) {
            return;
        }
        if (gameList == null || gameList.size() == 0) {
            if (!AppContext.getAppContext().isNetworkConnected()) {
                noNetworkView.setVisibility(View.VISIBLE);
                return;
            }
        }
        noNetworkView.setVisibility(View.GONE);
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
                convertView = new HomePageGameItemView(getActivity());
            }
            HomePageGameItemView view = (HomePageGameItemView) convertView;
            view.setData(getItem(position));
            return convertView;
        }
    }
}
