package com.caohua.games.ui.giftcenter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.gift.GiftCenterHotEntry;
import com.caohua.games.biz.gift.GiftCenterHotLogic;
import com.caohua.games.ui.adapter.ItemDivider;
import com.caohua.games.ui.adapter.ViewHolder;
import com.caohua.games.ui.fragment.NormalFragment;
import com.caohua.games.ui.giftcenter.widget.GiftCenterHScrollView;
import com.caohua.games.ui.widget.EmptyView;
import com.caohua.games.ui.widget.NoNetworkView;
import com.chsdk.biz.BaseLogic;
import com.chsdk.http.HttpConsts;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.utils.PicUtil;
import com.chsdk.utils.ViewUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.util.List;

/**
 * Created by admin on 2017/10/26.
 */

public class GiftCenterHotFragment extends NormalFragment {

    private boolean isFragmentVisible;
    private boolean isFirst = true;
    private RecyclerView recyclerView;
    private EmptyView emptyView;
    private LinearLayoutManager layoutManager;
    private SmartRefreshLayout refreshLayout;
    private MyAdapter adapter;
    private NoNetworkView noNetworkView;

    @Override
    protected void initChildView() {
        refreshLayout = findView(R.id.ch_fragment_gift_center_hot_refresh);
        recyclerView = findView(R.id.ch_fragment_gift_center_hot_recycler);
        noNetworkView = findView(R.id.ch_fragment_gift_center_hot_network);
        recyclerView.addItemDecoration(new ItemDivider().setDividerColor(0xfff4f4f4).setDividerWidth(ViewUtil.dp2px(activity, 1)));
        emptyView = findView(R.id.ch_fragment_gift_center_hot_empty);
        layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
//        if (isFragmentVisible && isFirst) {
//            onFragmentVisibleChange(true);
//        }
        refreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                if (adapter != null) {
                    onLogic(adapter.getItemCount() - 1, false);
                }
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                onLogic(0, true);
            }
        });

        if (getActivity() instanceof GiftCenterActivity) {
            if (((GiftCenterActivity) getActivity()).getTabValue() != 2) {
                refreshLayout.autoRefresh(400);
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isFragmentVisible = true;
        }

        if (mRoot == null) {
            return;
        }
        //可见，并且没有加载过
        if (isFirst && isFragmentVisible) {
            onLogic(0, true);
            return;
        }
        //由可见——>不可见 已经加载过
        if (isFragmentVisible) {  //不用调用加载网络，但是是有可见变成不可见
            isFragmentVisible = false;
        }
    }

    protected void showNoNetworkView(boolean showNoNetwork) {
        if (showNoNetwork) {
            noNetworkView.setVisibility(View.VISIBLE);
            noNetworkView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noNetworkView.setVisibility(View.GONE);
                    onLogic(0, true);
                }
            });
        } else {
            noNetworkView.setVisibility(View.GONE);
        }
    }

    protected void showEmptyView(boolean showEmpty) {
        if (showEmpty) {
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    emptyView.setVisibility(View.GONE);
                    onLogic(0, true);
                }
            });
        } else {
            emptyView.setVisibility(View.GONE);
        }
    }

    private void onLogic(final int pageSize, final boolean isRefresh) {
        isFirst = false;
        new GiftCenterHotLogic().centerHot(pageSize, new BaseLogic.DataLogicListner<GiftCenterHotEntry>() {
            @Override
            public void failed(String errorMsg, int errorCode) {
                if (pageSize > 0) {
                    refreshLayout.finishLoadmore();
                } else {
                    refreshLayout.finishRefresh();
                }
                if (!HttpConsts.ERROR_CODE_PARAMS_VALID.equals(errorMsg)) {
                    CHToast.show(activity, errorMsg);
                }
                if (adapter != null && adapter.getItemCount() > 0) {
                    showEmptyView(false);
                    showNoNetworkView(false);
                    refreshLayout.setLoadmoreFinished(true);
                    return;
                }
                if (HttpConsts.ERROR_NO_NETWORK.equals(errorMsg)) {
                    showNoNetworkView(true);
                    return;
                }
                showEmptyView(true);
            }

            @Override
            public void success(GiftCenterHotEntry entryResult) {
                if (pageSize > 0) {
                    refreshLayout.finishLoadmore();
                } else {
                    refreshLayout.finishRefresh();
                }
                if (entryResult == null) {
                    showEmptyView(true);
                    return;
                }
                List<GiftCenterHotEntry.ListBean> list = entryResult.getList();
                List<GiftCenterHotEntry.MyListBean> my_list = entryResult.getMy_list();
                if (adapter == null) {
                    adapter = new MyAdapter(list, my_list);
                    recyclerView.setAdapter(adapter);
                } else {
                    if (isRefresh) {
                        adapter.clearAll();
                        adapter.addAll(list, my_list);
                    } else {
                        adapter.addAll(list);
                    }
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ch_fragment_gift_center_hot;
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.HotHolder> {

        private LayoutInflater inflater;
        private List<GiftCenterHotEntry.MyListBean> my_list;
        private List<GiftCenterHotEntry.ListBean> list;

        public MyAdapter(List<GiftCenterHotEntry.ListBean> list, List<GiftCenterHotEntry.MyListBean> my_list) {
            this.list = list;
            this.my_list = my_list;
            inflater = LayoutInflater.from(activity);
        }

        @Override
        public MyAdapter.HotHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View inflate = inflater.inflate(viewType, parent, false);
            HotHolder viewHolder = new HotHolder(inflate);
            return viewHolder;
        }

        public void clearAll() {
            if (my_list != null) {
                my_list.clear();
            }

            if (list != null) {
                list.clear();
            }
            notifyDataSetChanged();
        }

        public void addAll(List<GiftCenterHotEntry.ListBean> lists, List<GiftCenterHotEntry.MyListBean> my_lists) {
            if (my_list != null) {
                my_list.addAll(my_lists);
            }
            if (list != null) {
                list.addAll(lists);
            }
            notifyDataSetChanged();
        }

        public void addAll(List<GiftCenterHotEntry.ListBean> lists) {
            if (list != null) {
                list.addAll(lists);
            }
            notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(MyAdapter.HotHolder holder, int position) {
//            list.get(position - 2); //这是最大值不要越界
            if (position == 0) {
                if (my_list == null || my_list.size() == 0) {
                    holder.hTitle.setVisibility(View.GONE);
                } else {
                    holder.hTitle.setVisibility(View.VISIBLE);
                }
                holder.hScroll.setData(my_list);
            } else if (position == 1) {
                if (adapter.getItemCount() - 2 > 0) {
                    holder.contentTitle.setVisibility(View.VISIBLE);
                } else {
                    holder.contentTitle.setVisibility(View.GONE);
                }
            } else {
                GiftCenterHotEntry.ListBean listBean = list.get(position - 2);
                PicUtil.displayImg(activity, holder.icon, listBean.getGame_icon(), R.drawable.ch_default_apk_icon);
                int rest = listBean.getRest();
                if (rest < 0 || rest > 100) {
                    rest = 0;
                }
                holder.contentProgress.setProgress(rest);
//                holder.value.setText(rest + "%");
                holder.content.setText(listBean.getGame_name());
                final String gift_name = listBean.getGift_name();
                holder.title.setText(gift_name);
                final String giftId = listBean.getGift_id();
                if (listBean.getIs_get() == 1) {
                    holder.btn.setText("已领取");
                } else {
                    holder.btn.setText("领取");
                }
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(giftId)) {
                            GiftDetailActivity.start(activity, giftId, gift_name, GiftDetailActivity.TYPE_NORMAL);
                        }
                    }
                });

                holder.btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(giftId)) {
                            GiftDetailActivity.start(activity, giftId, gift_name, GiftDetailActivity.TYPE_NORMAL);
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size() + 2;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return R.layout.ch_gift_center_hot_recycler_item_h_srcoll;
            } else if (position == 1) {
                return R.layout.ch_gift_center_hot_recycler_item_content_title;
            }
            return R.layout.ch_gift_center_hot_recycler_item_content;
        }

        class HotHolder extends ViewHolder {

            Button btn;
            ProgressBar contentProgress;
            View progressLayout;
            TextView title;
            TextView content;
            ImageView icon;

            TextView contentTitle;

            TextView hTitle;
            GiftCenterHScrollView hScroll;

            public HotHolder(View itemView) {
                super(itemView);

                //我的礼包
                hTitle = getView(R.id.ch_gift_center_hot_recycler_item_h_title);
                hScroll = getView(R.id.ch_gift_center_hot_recycler_item_h_scroll);

                //内容title
                contentTitle = getView(R.id.ch_gift_center_hot_recycler_item_content_title);
                //内容
                icon = getView(R.id.ch_gift_center_hot_item_icon);
                title = getView(R.id.ch_gift_center_hot_item_title);
                content = getView(R.id.ch_gift_center_hot_item_content);
                progressLayout = getView(R.id.ch_gift_center_hot_view_progress_layout);
                contentProgress = getView(R.id.ch_gift_center_hot_view_progress);
                btn = getView(R.id.ch_gift_center_hot_item_btn);
            }
        }
    }
}