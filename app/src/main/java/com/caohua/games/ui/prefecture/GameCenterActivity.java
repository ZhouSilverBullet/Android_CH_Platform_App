package com.caohua.games.ui.prefecture;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.prefecture.GameCenterEntry;
import com.caohua.games.biz.prefecture.GameCenterLogic;
import com.caohua.games.ui.BaseActivity;
import com.caohua.games.ui.HomePagerActivity;
import com.caohua.games.ui.adapter.ItemDivider;
import com.caohua.games.ui.adapter.MultiRecyclerViewAdapter;
import com.caohua.games.ui.adapter.ViewHolder;
import com.caohua.games.ui.widget.EmptyView;
import com.caohua.games.ui.widget.NoNetworkView;
import com.caohua.games.ui.widget.SubActivityTitleView;
import com.chsdk.api.CacheManager;
import com.chsdk.biz.app.AnalyticsHome;
import com.chsdk.biz.download.DownloadEntry;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.utils.LogUtil;
import com.chsdk.utils.PicUtil;

import java.util.List;

/**
 * Created by zhouzhou on 2017/5/13.
 */

public class GameCenterActivity extends BaseActivity {
    private RecyclerView centerRootRecyclerView;
    private GameCenterEntry.DataBean dataBean;
    private CenterRecyclerViewAdapter adapter;
    private EmptyView emptyView;
    private View loadView;
    private NoNetworkView noNetworkView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch_activity_game_center);
        initView();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        openWelcomeActivity(this, 0);
    }

    private void initView() {
        loadView = getView(R.id.ch_activity_game_center_download_progress_layout);
        centerRootRecyclerView = getView(R.id.ch_game_center_recycler_view);
        centerRootRecyclerView.addItemDecoration(new ItemDivider().setDividerWidth(2).setDividerColor(getResources().getColor(R.color.ch_gray)));
        LinearLayoutManager layoutManager = new LinearLayoutManager(GameCenterActivity.this);
        centerRootRecyclerView.setLayoutManager(layoutManager);
        dataBean = new GameCenterEntry.DataBean();
        adapter = new CenterRecyclerViewAdapter(GameCenterActivity.this, dataBean);
        centerRootRecyclerView.setAdapter(adapter);
        centerRootRecyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                loadData(adapter.getItemCount() - 4, 20, true);
            }
        });

        emptyView = getView(R.id.ch_activity_game_empty);
        noNetworkView = getView(R.id.ch_activity_game_no_network);

        loadData(0, 20, false);

        ((SubActivityTitleView) getView(R.id.ch_activity_game_center_title)).getBackImage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWelcomeActivity(v.getContext(), 0);
                finish();
            }
        });
    }

    protected void showNoNetworkView(boolean showNoNetwork) {
        if (showNoNetwork) {
            noNetworkView.setVisibility(View.VISIBLE);
            noNetworkView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noNetworkView.setVisibility(View.GONE);
                    loadData(0, 20, false);
                }
            });
        } else {
            noNetworkView.setVisibility(View.GONE);
        }
    }

    private void loadData(int page, int pageSize, boolean load) {
        if (!load) {
            loadView.setVisibility(View.VISIBLE);
        }
        if (page == 0) {
            GameCenterEntry gameCenterEntry = (GameCenterEntry) CacheManager.readObject(this, "gameCenterEntry");
            if (gameCenterEntry != null) {
                dataBean = gameCenterEntry.getData();
                adapter.setDataBean(dataBean);
            }
        }
        GameCenterLogic logic = new GameCenterLogic(this, null, page, pageSize);
        logic.getGameCenter(new GameCenterLogic.AppGameCenterListener() {
            @Override
            public void failed(String errorMsg) {
                if (isFinishing()) {
                    return;
                }
                loadView.setVisibility(View.GONE);
                CHToast.show(GameCenterActivity.this, errorMsg);
                if (adapter != null && adapter.getItemCount() > 0) {
                    return;
                }
                if (!AppContext.getAppContext().isNetworkConnected()) {
                    showNoNetworkView(true);
                    return;
                }
                showNoNetworkView(false);
                emptyView.setVisibility(View.VISIBLE);
                emptyView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loadData(0, 20, false);
                        emptyView.setVisibility(View.GONE);
                        loadView.setVisibility(View.VISIBLE);
                        centerRootRecyclerView.setVisibility(View.VISIBLE);
                    }
                });
                centerRootRecyclerView.setVisibility(View.GONE);
            }

            @Override
            public void success(Object entryResult, int currentPage) {
                showNoNetworkView(false);
                if (isFinishing()) {
                    return;
                }
                loadView.setVisibility(View.GONE);
                if (entryResult instanceof GameCenterEntry) {
                    GameCenterEntry.DataBean data = ((GameCenterEntry) entryResult).getData();
                    if (currentPage == 0) {
                        adapter.setDataBean(data);
                    } else if (currentPage > 0) {
                        if (data.getAllgame().size() > 0) {
                            adapter.addAllNotClear(data.getAllgame());
                            adapter.loadComplete(false);
                        } else {
                            adapter.loadComplete(true);
                        }
                    }
                }
            }
        });
    }

    class CenterRecyclerViewAdapter extends MultiRecyclerViewAdapter<GameCenterEntry.DataBean.AllgameBean> {

        private GameCenterEntry.DataBean dataBean;
        private ProgressBar progressBar;
        private TextView textView;

        public CenterRecyclerViewAdapter(Context context, GameCenterEntry.DataBean dataBean) {
            super(context, dataBean.getAllgame());
            this.dataBean = dataBean;
        }

        public void setDataBean(GameCenterEntry.DataBean dataBean) {
            this.dataBean = dataBean;
            list = dataBean.getAllgame();
            notifyDataSetChanged();
        }

        @Override
        protected void covert(ViewHolder holder, int position) {
            if (position == 0 || position == 1) {
                List<GameCenterEntry.DataBean.MygameBean> mygame = dataBean.getMygame();
                List<GameCenterEntry.DataBean.MultipleBean> multiple = dataBean.getMultiple();
                if (position == 0) {
                    ((TextView) holder.getView(R.id.ch_game_center_des)).setText("我的游戏");
                    if (mygame != null && mygame.size() > 0) {
                        holder.getView(R.id.ch_game_center_scroll_view_1_layout).setVisibility(View.VISIBLE);
                        ((GameCenterHorizontalScrollView) holder.getView(R.id.ch_game_center_scroll_view_1)).setData(mygame, GameCenterHorizontalScrollView.TYPE_MY_GAME);
                        if (multiple != null && multiple.size() > 0) {
                            holder.getView(R.id.ch_game_center_scroll_view_divider).setVisibility(View.GONE);
                        } else {
                            holder.getView(R.id.ch_game_center_scroll_view_divider).setVisibility(View.VISIBLE);
                        }
                    } else {
                        holder.getView(R.id.ch_game_center_scroll_view_1_layout).setVisibility(View.GONE);
                    }
                } else {
                    ((TextView) holder.getView(R.id.ch_game_center_des)).setText("推荐游戏");
                    if (multiple != null && multiple.size() > 0) {
                        holder.getView(R.id.ch_game_center_scroll_view_1_layout).setVisibility(View.VISIBLE);
                        ((GameCenterHorizontalScrollView) holder.getView(R.id.ch_game_center_scroll_view_1)).setData(multiple, GameCenterHorizontalScrollView.TYPE_MY_MULTIPLE);
                        holder.getView(R.id.ch_game_center_scroll_view_divider).setVisibility(View.VISIBLE);
                    } else {
                        holder.getView(R.id.ch_game_center_scroll_view_1_layout).setVisibility(View.GONE);
                    }
                }
            } else if (position == list.size() + 2) {
                progressBar = holder.getView(R.id.ch_game_center_item_load_progress);
                textView = holder.getView(R.id.ch_game_center_item_load_text);

                return;
            } else {
                final GameCenterEntry.DataBean.AllgameBean allgameBean = list.get(position - 2);
                final String game_icon = allgameBean.getGame_icon();
                ImageView item2image = holder.getView(R.id.ch_game_center_item2_image);
                if (!TextUtils.isEmpty(game_icon)) {
                    PicUtil.displayImg(context, item2image, game_icon, R.drawable.ch_default_apk_icon);
                } else {
                    item2image.setImageResource(R.drawable.ch_default_apk_icon);
                }
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AnalyticsHome.umOnEvent(AnalyticsHome.GAME_CENTER_ITEM, "游戏中心item被点击");
                        String detail_url = allgameBean.getDetail_url();
                        if (!TextUtils.isEmpty(detail_url)) {
                            DownloadEntry downloadEntry = new DownloadEntry();
                            downloadEntry.setTitle(allgameBean.getGame_name());
                            downloadEntry.setIconUrl(game_icon);
                            downloadEntry.setDetail_url(detail_url);
                            downloadEntry.setDownloadUrl(allgameBean.getGame_url());
                            downloadEntry.setPkg(allgameBean.getPackage_name());
                            WebActivity.startGameDetail(GameCenterActivity.this, downloadEntry);
                        }
                    }
                });
                ((TextView) holder.getView(R.id.ch_game_center_item2_name)).setText(allgameBean.getGame_name());
                ((TextView) holder.getView(R.id.ch_game_center_item2_content)).setText(allgameBean.getShort_desc());
                final int subject_id = allgameBean.getSubject_id();
                if (subject_id == 0) {
                    ((TextView) holder.getView(R.id.ch_game_center_item2_enter)).setVisibility(View.GONE);
                    return;
                } else {
                    ((TextView) holder.getView(R.id.ch_game_center_item2_enter)).setVisibility(View.VISIBLE);
                }
                ((TextView) holder.getView(R.id.ch_game_center_item2_enter)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AnalyticsHome.umOnEvent(AnalyticsHome.GAME_CENTER_ENTER_SUBJECT, "游戏中心进入专区界面");
                        HomePagerActivity.start(GameCenterActivity.this, subject_id);
                    }
                });
            }
        }

        public void loadComplete(boolean noMore) {
            if (noMore) {
                progressBar.setVisibility(View.GONE);
                textView.setText("没有更多的数据");
            }
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size() + 2 + 1;
        }

        @Override
        protected int getMultiItemViewType(int position) {
            if (position == 0 || position == 1) {
                return R.layout.ch_game_center_recycler_item_h;
            } else if (position == list.size() + 2) {
                return R.layout.ch_game_center_recycler_item_loading;
            }
            return R.layout.ch_game_center_recycler_item_v;
        }
    }

    abstract class EndlessRecyclerOnScrollListener extends
            RecyclerView.OnScrollListener {

        private int previousTotal = 0;
        private boolean loading = true;
        int firstVisibleItem, visibleItemCount, totalItemCount;

        private int currentPage = 0;

        private LinearLayoutManager mLinearLayoutManager;

        public EndlessRecyclerOnScrollListener(
                LinearLayoutManager linearLayoutManager) {
            this.mLinearLayoutManager = linearLayoutManager;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            visibleItemCount = recyclerView.getChildCount();
            totalItemCount = mLinearLayoutManager.getItemCount();
            firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }
            if (!loading
                    && (totalItemCount - visibleItemCount) <= firstVisibleItem) {
                currentPage++;
                onLoadMore(currentPage);
                loading = true;
            }
            LogUtil.errorLog("visibleItemCount = " + visibleItemCount + " totalItemCount = " +
                    totalItemCount + " firstVisibleItem = " + firstVisibleItem);
        }

        public abstract void onLoadMore(int currentPage);
    }

}
