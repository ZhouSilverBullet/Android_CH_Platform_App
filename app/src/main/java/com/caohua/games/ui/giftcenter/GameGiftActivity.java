package com.caohua.games.ui.giftcenter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.gift.GameGiftEntry;
import com.caohua.games.biz.gift.GameGiftLogic;
import com.caohua.games.ui.adapter.ItemDivider;
import com.caohua.games.ui.adapter.MultiRecyclerViewAdapter;
import com.caohua.games.ui.adapter.ViewHolder;
import com.caohua.games.ui.giftcenter.widget.GameGiftTopView;
import com.caohua.games.ui.vip.CommonActivity;
import com.chsdk.biz.BaseLogic;
import com.chsdk.http.HttpConsts;
import com.chsdk.utils.ViewUtil;

import java.util.List;

/**
 * Created by admin on 2017/10/31.
 */

public class GameGiftActivity extends CommonActivity {
    public static final String GAME_GIFT_ID = "game_gift_id";
    private String gameGiftId;
    private RecyclerView recyclerView;
    private MyAdapter adapter;

    @Override
    protected void initVariables() {
        if (getIntent() != null) {
            gameGiftId = getIntent().getStringExtra(GAME_GIFT_ID);
        }
    }

    @Override
    protected String subTitle() {
        return "游戏礼包";
    }

    @Override
    protected int childViewId() {
        return R.layout.ch_activity_game_gift;
    }

    @Override
    protected void initView() {
        recyclerView = getView(R.id.ch_activity_game_gift_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.addItemDecoration(new ItemDivider().setDividerWidth(ViewUtil.dp2px(activity, 1)).setDividerColor(getResources().getColor(R.color.ch_gray)));
    }

    @Override
    protected void loadData() {
        new GameGiftLogic().gameGift(gameGiftId, new BaseLogic.DataLogicListner<GameGiftEntry>() {
            @Override
            public void failed(String errorMsg, int errorCode) {
                if (HttpConsts.ERROR_NO_NETWORK.equals(errorMsg)) {
                    showNoNetworkView(true);
                    return;
                }
                showEmptyView(true);
            }

            @Override
            public void success(GameGiftEntry entryResult) {
                showNoNetworkView(false);
                if (entryResult == null) {
                    showEmptyView(true);
                    return;
                }

                List<GameGiftEntry.ListBean> list = entryResult.getList();
                if (list == null || list.size() == 0) {
                    showEmptyView(true);
                    return;
                }
                adapter = new MyAdapter(activity, list, entryResult.getGame(),
                        new int[]{R.layout.ch_game_gift_recycler_item,
                                R.layout.ch_game_gift_recycler_item_2});
                recyclerView.setAdapter(adapter);

            }
        });
    }

    public static void start(Context context, String gameGiftId) {
        Intent intent = new Intent(context, GameGiftActivity.class);
        intent.putExtra(GAME_GIFT_ID, gameGiftId);
        context.startActivity(intent);
    }

    private class MyAdapter extends MultiRecyclerViewAdapter<GameGiftEntry.ListBean> {

        private GameGiftEntry.GameBean bean;

        public MyAdapter(Context context, List<GameGiftEntry.ListBean> list, GameGiftEntry.GameBean bean, int[] layoutIds) {
            super(context, list, layoutIds);
            this.bean = bean;
        }

        @Override
        protected void covert(ViewHolder holder, int position) {
            if (position == 0) {
                ((GameGiftTopView) holder.getView(R.id.ch_game_gift_recycler_item_top)).setData(bean);
            } else {
                GameGiftEntry.ListBean listBean = list.get(position - 1);
                ProgressBar progressBar = holder.getView(R.id.ch_game_gift_recycler_item_progress);
                TextView title = holder.getView(R.id.ch_game_gift_recycler_item_title);
                Button btn = holder.getView(R.id.ch_game_gift_recycler_item_btn);
                TextView des = holder.getView(R.id.ch_game_gift_recycler_item_des);
                int rest = listBean.getApp_rest();
                if (rest < 0 || rest > 100) {
                    rest = 0;
                }
                progressBar.setProgress(rest);
                final String gift_name = listBean.getGift_name();
                title.setText(gift_name);
                des.setText(listBean.getGift_desc());
                final String gift_id = listBean.getGift_id();
                if (listBean.getIs_get() == 1) {
                    btn.setText("已领取");
                } else {
                    btn.setText("领取");
                }
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(gift_id)) {
                            GiftDetailActivity.start(activity, gift_id, gift_name, GiftDetailActivity.TYPE_NORMAL);
                        }
                    }
                });

                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(gift_id)) {
                            GiftDetailActivity.start(activity, gift_id, gift_name, GiftDetailActivity.TYPE_NORMAL);
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size() + 1;
        }

        @Override
        protected int getMultiItemViewType(int position) {
            if (position == 0) {
                return layoutIds[0];
            }
            return layoutIds[1];
        }
    }
}
