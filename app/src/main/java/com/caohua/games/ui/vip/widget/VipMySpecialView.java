package com.caohua.games.ui.vip.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.app.AnalyticsHome;
import com.chsdk.ui.WebActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by admin on 2017/8/25.
 */

public class VipMySpecialView extends LinearLayout {
    private Context context;
    private RecyclerView recyclerView;
    private SpecialAdapter adapter;
    private TextView titleMore;

    private static final int VIP_GIFT = 1;
    private static final int VIP_KE_FU = 2;
    private static final int VIP_FAN_LI = 3;
    private static final int VIP_SHANG_RI = 4;
    private static final int VIP_ACT = 5;
    private static final int VIP_GIFT_V = 6;
    private static final int VIP_JIA_SU = 7;
    private static final int VIP_BIAO_ZHI = 8;
    private VipTitleView titleView;

    public VipMySpecialView(Context context) {
        this(context, null);
    }

    public VipMySpecialView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VipMySpecialView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.ch_vip_view_my_special, this, true);
        setVisibility(GONE);
        recyclerView = (RecyclerView) findViewById(R.id.ch_vip_my_special_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setFocusable(false);
        List<Integer> list = new ArrayList<>();
        adapter = new SpecialAdapter(context, list);
        recyclerView.setAdapter(adapter);
        titleView = (VipTitleView) findViewById(R.id.ch_vip_my_special_title_view);
        titleView.setMoreTextGone();
    }

    public void setData(List<Integer> list) {
        if (list == null || list.size() == 0) {
            setVisibility(GONE);
            return;
        }
        setVisibility(VISIBLE);
        adapter.addAll(list);
    }

    public class SpecialAdapter extends RecyclerView.Adapter<SpecialAdapter.PrefectureViewHolder> {
        private LayoutInflater inflater;
        private List<Integer> list;
        private Context context;

        public SpecialAdapter(Context context, List<Integer> list) {
            this.context = context;
            this.list = list;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public SpecialAdapter.PrefectureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SpecialAdapter.PrefectureViewHolder(inflater.inflate(R.layout.ch_vip_my_special_view_recycler_item, parent, false));
        }

        @Override
        public void onBindViewHolder(SpecialAdapter.PrefectureViewHolder holder, int position) {
            Integer type = list.get(position);
            doType(holder.layout, holder.imageView, holder.textView, type);
        }

        private void doType(View layout, ImageView imageView, TextView textView, int type) {
            switch (type) {
                case VIP_ACT:
                    imageView.setImageResource(R.drawable.ch_vip_my_special_act);
                    textView.setText("活动");
                    skipWeb(layout, "vip/activeShow");
                    break;
                case VIP_BIAO_ZHI:
                    imageView.setImageResource(R.drawable.ch_vip_my_special_biaozhi);
                    textView.setText("标识");
                    skipWeb(layout, "vip/growShow");
                    break;
                case VIP_FAN_LI:
                    imageView.setImageResource(R.drawable.ch_vip_my_special_fanli);
                    textView.setText("返利");
                    skipWeb(layout, "vip/fanliShow");
                    break;
                case VIP_GIFT:
                    imageView.setImageResource(R.drawable.ch_vip_my_special_gift);
                    textView.setText("礼包");
                    skipWeb(layout, "vip/giftShow");
                    break;
                case VIP_GIFT_V:
                    imageView.setImageResource(R.drawable.ch_vip_my_special_gift_for_v);
                    textView.setText("等级礼包");
                    skipWeb(layout, "vip/giftShow");
                    break;
                case VIP_JIA_SU:
                    imageView.setImageResource(R.drawable.ch_vip_my_special_jiasu);
                    textView.setText("加速");
                    skipWeb(layout, "vip/growShow");
                    break;
                case VIP_KE_FU:
                    imageView.setImageResource(R.drawable.ch_vip_my_special_kefu);
                    textView.setText("客服");
                    skipWeb(layout, "vip/kefuView");
                    break;
                case VIP_SHANG_RI:
                    imageView.setImageResource(R.drawable.ch_vip_my_special_shengri);
                    textView.setText("生日");
                    skipWeb(layout, "vip/giftShow");
                    break;
                default:
                    imageView.setImageResource(R.drawable.ch_vip_my_special_act);
                    textView.setText("活动");
                    skipWeb(layout, "vip/activeShow");
                    break;
            }
        }

        private void skipWeb(View layout, final String value) {
            layout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebActivity.startAppLink(context, BaseLogic.HOST_APP + value);
                }
            });
        }

        private void umOnEvent(String methodUrl) {
            AnalyticsHome.umOnEvent(AnalyticsHome.SUBJECT_MAIN_TAB, methodUrl + "：7个item的二级界面");
        }


        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }

        public void addAll(Collection<Integer> data) {
            list.clear();
            list.addAll(data);
            notifyDataSetChanged();
        }

        class PrefectureViewHolder extends RecyclerView.ViewHolder {

            private View layout;
            private ImageView imageView;
            private TextView textView;

            public PrefectureViewHolder(View itemView) {
                super(itemView);
                layout = itemView.findViewById(R.id.ch_vip_my_special_recycler_item_layout);
                textView = ((TextView) itemView.findViewById(R.id.ch_vip_my_special_recycler_item_text));
                imageView = ((ImageView) itemView.findViewById(R.id.ch_vip_my_special_recycler_item_image));
            }
        }
    }
}
