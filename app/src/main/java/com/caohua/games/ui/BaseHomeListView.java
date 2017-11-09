package com.caohua.games.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.caohua.games.R;
import com.caohua.games.biz.DataInterface;
import com.caohua.games.biz.home.DataMgr;
import com.caohua.games.biz.ranking.RankingEntry;
import com.caohua.games.ui.ranking.HomeRankingItemView;
import com.caohua.games.ui.ranking.RankingDetailActivity;
import com.caohua.games.ui.widget.HomeSubTitleView;
import com.chsdk.model.BaseEntry;

import java.util.List;

/**
 * Created by CXK on 2016/10/18.
 */

public abstract class BaseHomeListView<T extends BaseEntry, A extends DataInterface> extends LinearLayout {
    protected HomeSubTitleView subTitleView;
    private SparseArray<A> cacheViews;
    private List<T> data;
    public BaseHomeListView(Context context) {
        super(context);
        loadXml();
    }

    protected abstract int getMaxCount();
    protected abstract View getItemView();
    protected abstract String getTitle();
    protected void onMoreClick() {

    }

    protected void setTvMore(String tvMore) {
        subTitleView.setTvMore(tvMore);
    }

    public List<T> getDataList() {
        return data;
    }

    public BaseHomeListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadXml();
    }

    private void loadXml() {
        LayoutInflater.from(getContext()).inflate(R.layout.ch_base_view_home_list, this, true);
        setOrientation(VERTICAL);
        setVisibility(GONE);
        init();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    private void init() {
        subTitleView = (HomeSubTitleView) findViewById(R.id.ch_base_view_home_list_subtitle);
        subTitleView.setTopTitle(getTitle());
        subTitleView.setOnMoreClickListener(new HomeSubTitleView.OnMoreClickListener() {
            @Override
            public void moreClick() {
                onMoreClick();
            }
        });
    }

    public synchronized void initData(List<T> list) {
        if (list == null || list.size() == 0) {
            return;
        }

        this.data = list;
        setVisibility(VISIBLE);

        if (cacheViews == null) {
            cacheViews = new SparseArray<>();
        }

        for (int i = 0; i < list.size(); i++) {
            if (i == getMaxCount()) {
                break;
            }
            T entry = list.get(i);
            A view = cacheViews.get(i);
            if (view == null) {
                view = (A) getItemView();
                addView((View) view);
                cacheViews.append(i, view);
            }
            View tmpView = (View) view;
            tmpView.setVisibility(VISIBLE);
            view.setData(entry);
        }

        if (cacheViews.size() > list.size()) {
            for (int i = list.size(); i < cacheViews.size(); i++) {
                A view = cacheViews.get(i);
                View tmpView = (View) view;
                tmpView.setVisibility(GONE);
//                removeView(tmpView);
            }
        }
    }

    protected SparseArray<A> getCacheViews() {
        return cacheViews;
    }
}
