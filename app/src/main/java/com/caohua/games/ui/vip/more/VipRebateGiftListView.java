package com.caohua.games.ui.vip.more;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.vip.VipRechargeAwardEntry;
import com.caohua.games.ui.adapter.ItemDivider;
import com.caohua.games.ui.adapter.ViewHolder;
import com.caohua.games.ui.vip.VipRebateDetailActivity;
import com.caohua.games.ui.vip.widget.VipTitleView;
import com.chsdk.utils.PicUtil;
import com.chsdk.utils.ViewUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by admin on 2017/8/22.
 */

public class VipRebateGiftListView extends LinearLayout {

    private Context context;
    private VipTitleView titleView;
    private RecyclerView recyclerView;
    private VipRewardAdapter adapter;
    private boolean isAuth;
    private boolean isVip;

    public VipRebateGiftListView(Context context) {
        this(context, null);
    }

    public VipRebateGiftListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VipRebateGiftListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.ch_vip_recharge_gift_list_view, this, true);
        setVisibility(GONE);
        titleView = (VipTitleView) findViewById(R.id.ch_vip_reward_title_view);
        titleView.setIconAndMoreTextGone();
        recyclerView = (RecyclerView) findViewById(R.id.ch_vip_reward_recycler_view);
        recyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new ItemDivider().setDividerWidth(ViewUtil.dp2px(context, 1)).setDividerColor(getResources().getColor(R.color.ch_gray)));
        adapter = new VipRewardAdapter(context, new ArrayList<VipRechargeAwardEntry.DataBean.RebateBean>());
        recyclerView.setAdapter(adapter);
    }

    public void setData(List<VipRechargeAwardEntry.DataBean.RebateBean> list, boolean isAuth, boolean isVip) {
        if (list == null || list.size() == 0) {
            setVisibility(GONE);
            return;
        }
        this.isAuth = isAuth;
        this.isVip = isVip;
        setVisibility(VISIBLE);
        adapter.addAll(list);
    }

    private class VipRewardAdapter extends RecyclerView.Adapter<VipRewardAdapter.VipRewardViewHolder> {

        private Context context;
        private LayoutInflater inflater;
        private List<VipRechargeAwardEntry.DataBean.RebateBean> list;

        public VipRewardAdapter(Context context, List<VipRechargeAwardEntry.DataBean.RebateBean> list) {
            this.context = context;
            this.list = list;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public VipRewardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VipRewardViewHolder(inflater.inflate(R.layout.ch_vip_rebate_gift_list_item_view, parent, false));
        }

        @Override
        public void onBindViewHolder(VipRewardViewHolder holder, int position) {
            VipRechargeAwardEntry.DataBean.RebateBean rebateBean = list.get(position);
            PicUtil.displayImg(context, holder.icon, rebateBean.getGame_icon(), R.drawable.ch_default_apk_icon);
            holder.title.setText(rebateBean.getRebate_title());
            holder.content.setText(rebateBean.getRebate_desc());
            final String rebateId = rebateBean.getRebate_id();
            String low_vip = rebateBean.getLow_vip();
            if (!TextUtils.isEmpty(low_vip) && getValue(low_vip) >= 2) {
                holder.level.setVisibility(VISIBLE);
                holder.level.setText("V" + low_vip + "以上");
            } else {
                holder.level.setVisibility(GONE);
            }
            final int is_close = rebateBean.getIs_close();
            final int is_open = rebateBean.getIs_open();
            if (is_close == 1) {
                holder.end.setBackgroundResource(R.drawable.ch_vip_draw_button_not_auth_bg);
                holder.end.setText("已结束");
            } else {
                holder.end.setBackgroundResource(R.drawable.ch_vip_draw_button_bg);
                holder.end.setText("参与");
            }
            holder.end.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    VipRebateDetailActivity.start(context, rebateId, is_open == 1, is_close == 1, isVip, isAuth);
                }
            });
            holder.rebateLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    VipRebateDetailActivity.start(context, rebateId, is_open == 1, is_close == 1, isVip, isAuth);
                }
            });
        }

        private int getValue(String value) {
            int i = 0;
            try {
                i = Integer.parseInt(value);
            } catch (Exception e) {
            }
            return i;
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }

        public void addAll(Collection<VipRechargeAwardEntry.DataBean.RebateBean> collection) {
            if (list != null) {
                list.addAll(collection);
                notifyDataSetChanged();
            }
        }

        class VipRewardViewHolder extends ViewHolder {

            private TextView end;
            private TextView level;
            private View rebateLayout;
            private TextView content;
            private TextView title;
            private ImageView icon;

            public VipRewardViewHolder(View itemView) {
                super(itemView);
                icon = (ImageView) itemView.findViewById(R.id.ch_vip_view_rebate_gift_icon);
                title = (TextView) itemView.findViewById(R.id.ch_vip_view_rebate_gift_title);
                content = (TextView) itemView.findViewById(R.id.ch_vip_view_rebate_gift_content);
                rebateLayout = itemView.findViewById(R.id.ch_vip_view_rebate_gift_top_layout);
                level = (TextView) itemView.findViewById(R.id.ch_vip_view_rebate_gift_level);
                end = (TextView) itemView.findViewById(R.id.ch_vip_view_rebate_gift_end);
            }
        }
    }

}
