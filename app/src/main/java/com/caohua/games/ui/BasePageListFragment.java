package com.caohua.games.ui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.caohua.games.R;
import com.caohua.games.biz.DataInterface;
import com.chsdk.model.BaseEntry;
import com.chsdk.utils.LogUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZengLei on 2016/11/3.
 */

public abstract class BasePageListFragment<T extends BaseEntry, A extends DataInterface> extends BasePageFragment {
    protected ListView listView;
    protected List<T> data;
    protected ListAdapter listAdapter;
    private boolean isFirst;

    @Override
    protected int getLayoutId() {
        return R.layout.ch_fragement_hot;
    }

    @Override
    protected void initLoad() {
        List<T> data = getIntentListData();
        setData(data, false);
    }

    @Override
    protected List<LoadParams> getDataType() {
        List<LoadParams> types = new ArrayList<>();
        types.add(getLoadPageParams(getType()));
        return types;
    }

    protected abstract int getType();

    protected abstract int getChildViewId();

    protected abstract View getItemView();

    @Override
    protected void handleData(int type, boolean loadMore, Object data) {
        if (type != getType()) {
            return;
        }
        setData((List<T>) data, loadMore);
    }

    @Override
    protected void initChildView() {
        listView = findView(getChildViewId());
        refreshLayout.setEnableLoadmore(true);
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                loadedCount = listAdapter.getCount();
                moreLoad();
            }
        });
    }

    @Override
    protected void loadMoreFinish(boolean success) {
        refreshLayout.finishLoadmore();
        if (!success) {
            refreshLayout.setLoadmoreFinished(true);
        }
//        if (!success) {
//            refreshLayout.setLoadmoreFinished(true);
//        } else {
//            refreshLayout.finishLoadmore();
//        }
//        listView.loadComplete(!success);
//        listView.setLoadMoreUnable(!success);
    }

    private void setData(List<T> data, boolean loadMore) {
        if (data == null || data.size() == 0) {
            if (refreshLayout != null && !isFirst) {
                refreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        loadData(false);
                    }
                });
            }
            isFirst = true;
            LogUtil.errorLog("BasePageListFragment isFirst" + isFirst);
            return;
        }
        hasLoadData = true;
        if (listAdapter == null) {
            this.data = data;
            handleData(this.data);
            listAdapter = new ListAdapter();
            listView.setAdapter(listAdapter);
        } else {
            if (!loadMore) {
                this.data.clear();
            }
            this.data.addAll(data);
            handleData(this.data);
            listAdapter.notifyDataSetChanged();
        }
    }

    protected void handleData(List<T> data) {

    }

    protected class ListAdapter extends BaseAdapter {

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
                convertView = getItemView();
            }
            A view = (A) convertView;
            view.setData(getItem(position));
            return convertView;
        }
    }
}
