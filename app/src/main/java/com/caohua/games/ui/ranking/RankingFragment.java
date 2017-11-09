package com.caohua.games.ui.ranking;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.caohua.games.R;
import com.caohua.games.biz.home.DataMgr;
import com.caohua.games.biz.ranking.RankingTotalSubEntry;
import com.caohua.games.ui.BaseFragment;
import com.caohua.games.ui.LazyLoadFragment;
import com.chsdk.utils.LogUtil;
import com.chsdk.utils.ViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/14.
 */

public class RankingFragment extends LazyLoadFragment {
    private ViewGroup container;
    private SparseArray<RankingItemView> cacheViews;

    @Override
    protected int getLayoutId() {
        return R.layout.ch_fragment_ranking;
    }

    @Override
    protected void initChildView() {
        container = findView(R.id.ch_fragment_ranking_container);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected List<LoadParams> getDataType() {
        List<LoadParams> types = new ArrayList<>();
        LoadParams params = new LoadParams();
        params.requestType = DataMgr.DATA_TYPE_RANKING_TOTAL;
        types.add(params);
        return types;
    }

    @Override
    protected void handleData(LoadParams param, Object o) {
        if (param.requestType != DataMgr.DATA_TYPE_RANKING_TOTAL) {
            return;
        }

        if (o instanceof List) {
            List<List<RankingTotalSubEntry>> data = (List<List<RankingTotalSubEntry>>) o;
            if (cacheViews == null) {
                cacheViews = new SparseArray<>();
            }

            for (int i = 0; i < data.size(); i++) {
                List<RankingTotalSubEntry> entry = data.get(i);
                RankingItemView view = cacheViews.get(i);
                if (view == null) {
                    view = new RankingItemView(getActivity());
                    cacheViews.append(i, view);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    if (i != 0) {
                        params.setMargins(0, ViewUtil.dp2px(getActivity(), 10), 0, 0);
                    }
                    container.addView(view, params);
                }

                if (entry != null && entry.size() > 0) {
                    view.setVisibility(View.VISIBLE);
                    view.initData(entry, i);
                } else {
                    view.setVisibility(View.GONE);
                }
            }

            if (cacheViews.size() > data.size()) {
                for (int i = data.size(); i < cacheViews.size(); i++) {
                    View view = cacheViews.get(i);
                    view.setVisibility(View.GONE);
                }
            }
        }
    }
}
