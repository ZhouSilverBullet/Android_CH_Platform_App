package com.caohua.games.ui.ranking;

import android.graphics.Rect;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.caohua.games.R;
import com.caohua.games.biz.ranking.RankingEntry;
import com.caohua.games.ui.BasePageFragment;
import com.chsdk.utils.ViewUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ZengLei on 2016/11/3.
 */

public class RankingDetailFragment extends BasePageFragment {
    private int index;
    private List<RankingEntry> data;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private Set<RankingTopItemView> mTopItemViews;
    private Set<RankingRecycleItemView> mRecycleItemViews;

    @Override
    protected int getLayoutId() {
        return R.layout.ch_fragment_ranking_detail;
    }

    @Override
    protected List<LoadParams> getDataType() {
        List<LoadParams> types = new ArrayList<>();
        LoadPageParams params = new LoadPageParams();
        params.requestType = index;
        params.loadedCount = loadedCount;
        params.requestCount = 10;
        types.add(params);
        return types;
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
    }

    @Override
    protected void handleData(int type, boolean loadMore, Object o) {
        if (type != index) {
            return;
        }

        List<RankingEntry> list = (List<RankingEntry>) o;

        if (list == null || list.size() == 0)
            return;

        if (recyclerAdapter == null) {
            data = list;
            recyclerAdapter = new RecyclerAdapter();
            recyclerView.setAdapter(recyclerAdapter);
        } else {
            if (!loadMore) {
                data.clear();
            }
            data.addAll(list);
            recyclerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void initChildView() {
        recyclerView = findView(R.id.ch_rank_recycle_view);

        /*final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return recyclerAdapter.isHeader(position) ? layoutManager.getSpanCount() : 1;
            }
        });*/
        refreshLayout.setEnableLoadmore(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        int spacingInPixels = ViewUtil.dp2px(getActivity(), 1);
        recyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
        refreshLayout.setEnableLoadmore(true);
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                loadedCount = layoutManager.getItemCount();
                loadData(false);
            }
        });
//        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
//                    if (!isLoading && lastVisiblePosition >= layoutManager.getItemCount() - 1) {
//                        loadedCount = layoutManager.getItemCount();
//                        loadData(false);
//                    }
//                }
//            }
//        });
        RecyclerView.ItemAnimator animator = new DefaultItemAnimator();
        animator.setChangeDuration(0);
        recyclerView.setItemAnimator(animator);

        index = (int) getIntentData();
    }

    private class RecyclerAdapter extends RecyclerView.Adapter {
        private static final int ITEM_VIEW_TYPE_HEADER = 0;
        private static final int ITEM_VIEW_TYPE_ITEM = 1;

        public boolean isHeader(int position) {
            return position == 0;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == ITEM_VIEW_TYPE_HEADER) {
                RankingTopItemView convertView = new RankingTopItemView(getActivity());
                if (mTopItemViews == null) {
                    mTopItemViews = new HashSet<>();
                }
                mTopItemViews.add(convertView);
                return new HeaderViewHolder(convertView);
            } else {
                RankingRecycleItemView convertView = new RankingRecycleItemView(getActivity());
                if (mRecycleItemViews == null) {
                    mRecycleItemViews = new HashSet<>();
                }
                mRecycleItemViews.add(convertView);
                return new BodyViewHolder(convertView);
            }
        }

        @Override
        public int getItemViewType(int position) {
//            return isHeader(position) ? ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_ITEM;
            return ITEM_VIEW_TYPE_HEADER;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof HeaderViewHolder) {
                HeaderViewHolder bodyViewHolder = (HeaderViewHolder) holder;
                RankingTopItemView item = (RankingTopItemView) bodyViewHolder.itemView;
                item.setData(data.get(position), position);

            } else if (holder instanceof BodyViewHolder) {
                BodyViewHolder bodyViewHolder = (BodyViewHolder) holder;
                RankingRecycleItemView item = (RankingRecycleItemView) bodyViewHolder.itemView;
                item.setData(data.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class HeaderViewHolder extends RecyclerView.ViewHolder {
            public HeaderViewHolder(View itemView) {
                super(itemView);
            }
        }

        public class BodyViewHolder extends RecyclerView.ViewHolder {
            public BodyViewHolder(RankingRecycleItemView itemView) {
                super(itemView);
            }
        }
    }

    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildPosition(view);
            if (position > 0) {
                outRect.top = space;
//                if (position % 2 == 1) {
//                    outRect.right = space / 2;
//                } else {
//                    outRect.left = space / 2;
//                }
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mRecycleItemViews != null) {
            for (RankingRecycleItemView recycleItemView : mRecycleItemViews) {
                if (recycleItemView != null) {
                    recycleItemView.onDestroy();
                }
            }
        }
        if (mTopItemViews != null) {
            for (RankingTopItemView topItemView : mTopItemViews) {
                if (topItemView != null) {
                    topItemView.onDestroy();
                }
            }
        }
        super.onDestroy();
    }
}
