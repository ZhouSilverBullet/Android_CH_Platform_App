package com.caohua.games.ui.vip.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.vip.VipEntry;
import com.caohua.games.ui.coupon.CouponCenterActivity;
import com.caohua.games.ui.vip.VipCertificationActivity;
import com.caohua.games.ui.vip.VipRebateDetailActivity;
import com.caohua.games.ui.vip.VipRechargeAwardActivity;
import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.app.AnalyticsHome;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.CHAlertDialog;
import com.chsdk.utils.PicUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by admin on 2017/8/24.
 */

public class VipRechargeGiftView extends LinearLayout {

    private Context context;
    private RecyclerView recyclerView;
    private RechargeRecyclerAdapter adapter;
    private VipTitleView titleView;
    private boolean isAuth;
    private boolean isVip;
    private VipEntry.DataBean.RebateBean rebateBean;
    private View rebateLayout;
    private ImageView rebateIcon;
    private TextView rebateTitle;
    private TextView rebateDes;
    private View line;
    private TextView endText;
    private TextView levelText;

    public VipRechargeGiftView(Context context) {
        this(context, null);
    }

    public VipRechargeGiftView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VipRechargeGiftView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    public void setData(List<VipEntry.DataBean.RechargeBean> list, List<VipEntry.DataBean.RebateBean> rebate, boolean isAuth, boolean isVip) {
        if ((list == null || list.size() == 0) && (rebate == null || rebate.size() == 0)) {
            setVisibility(GONE);
            return;
        }
        setVisibility(VISIBLE);
        if (rebate != null && rebate.size() >= 1) {
            rebateBean = rebate.get(0);
            initRebateData();
        }
        this.isAuth = isAuth;
        this.isVip = isVip;
        if (list != null && list.size() >= 1) {
            recyclerView.setVisibility(VISIBLE);
            adapter.addAll(list);
        }
    }

    private void initRebateData() {
        if (rebateBean != null) {
            line.setVisibility(VISIBLE);
            rebateLayout.setVisibility(VISIBLE);
            final int is_close = rebateBean.getIs_close();
            final int is_open = rebateBean.getIs_open();
            rebateLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //请求接口
                    VipRebateDetailActivity.start(context, rebateBean.getRebate_id(), is_open == 1, is_close == 1, isVip, isAuth);
                }
            });
            String low_vip = rebateBean.getLow_vip();
            if (!TextUtils.isEmpty(low_vip) && getValue(low_vip) >= 2) {
                levelText.setVisibility(VISIBLE);
                levelText.setText("V" + low_vip + "以上");
            } else {
                levelText.setVisibility(GONE);
            }
            if (is_close == 1) {
                endText.setBackgroundResource(R.drawable.ch_vip_draw_button_not_auth_bg);
                endText.setText("已结束");
            } else {
                endText.setBackgroundResource(R.drawable.ch_vip_draw_button_bg);
                endText.setText("参与");
            }
            endText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //请求接口
                    VipRebateDetailActivity.start(context, rebateBean.getRebate_id(), is_open == 1, is_close == 1, isVip, isAuth);
                }
            });
            rebateDes.setText(rebateBean.getRebate_desc());
            PicUtil.displayImg(context, rebateIcon, rebateBean.getGame_icon(), R.drawable.ch_default_apk_icon);
            rebateTitle.setText(rebateBean.getRebate_title());
        }
    }

    private int getValue(String value) {
        int i = 0;
        try {
            i = Integer.parseInt(value);
        } catch (Exception e) {
        }
        return i;
    }

    private void init() {
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.ch_vip_view_recharge_gift, this, true);
        setVisibility(GONE);
        recyclerView = (RecyclerView) findViewById(R.id.ch_vip_recharge_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new RechargeRecyclerAdapter(context, new ArrayList<VipEntry.DataBean.RechargeBean>());
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(GONE);
        titleView = (VipTitleView) findViewById(R.id.ch_vip_view_recharge_title_view);
        titleView.getMoreText().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                VipRechargeAwardActivity.start(context, isAuth, isVip);
            }
        });
        initRebateView();
    }

    private void initRebateView() {
        rebateLayout = findViewById(R.id.ch_vip_view_recharge_top_layout);
        rebateLayout.setVisibility(GONE);
        line = findViewById(R.id.ch_vip_view_recharge_center_line);
        line.setVisibility(GONE);
        rebateIcon = (ImageView) findViewById(R.id.ch_vip_view_recharge_icon);
        rebateTitle = (TextView) findViewById(R.id.ch_vip_view_recharge_title);
        rebateDes = (TextView) findViewById(R.id.ch_vip_view_recharge_content);
        endText = (TextView) findViewById(R.id.ch_vip_view_recharge_end);
        levelText = (TextView) findViewById(R.id.ch_vip_view_recharge_level);
    }

    class RechargeRecyclerAdapter extends RecyclerView.Adapter<RechargeRecyclerAdapter.PrefectureViewHolder> {
        private LayoutInflater inflater;
        private List<VipEntry.DataBean.RechargeBean> list;
        private Context context;

        public RechargeRecyclerAdapter(Context context, List<VipEntry.DataBean.RechargeBean> list) {
            this.context = context;
            this.list = list;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public RechargeRecyclerAdapter.PrefectureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RechargeRecyclerAdapter.PrefectureViewHolder(inflater.inflate(R.layout.ch_vip_view_recharge_item_view, parent, false));
        }

        @Override
        public void onBindViewHolder(RechargeRecyclerAdapter.PrefectureViewHolder holder, int position) {
            final VipEntry.DataBean.RechargeBean bean = list.get(position);
            String use_amt = bean.getUse_amt();
            if (TextUtils.isEmpty(use_amt)) {
                holder.textValue.setVisibility(GONE);
            } else {
                holder.textValue.setVisibility(VISIBLE);
                holder.textValue.setText(use_amt);
            }
            String min_amt = bean.getMin_amt();
            if (TextUtils.isEmpty(min_amt)) {
                holder.textDes.setVisibility(GONE);
            } else {
                holder.textDes.setVisibility(VISIBLE);
                holder.textDes.setText(min_amt);
            }
            holder.btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isVipOrAuth()) {
                        return;
                    }
                    Intent intent = new Intent(context, CouponCenterActivity.class);
                    context.startActivity(intent);
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

        public void addAll(Collection<VipEntry.DataBean.RechargeBean> data) {
            if (list != null) {
                list.clear();
                list.addAll(data);
                notifyDataSetChanged();
            }
        }

        class PrefectureViewHolder extends RecyclerView.ViewHolder {

            private Button btn;
            private TextView textDes;
            private TextView textValue;

            public PrefectureViewHolder(View itemView) {
                super(itemView);
                textValue = ((TextView) itemView.findViewById(R.id.ch_vip_recharge_item_value));
                textDes = ((TextView) itemView.findViewById(R.id.ch_vip_recharge_item_des));
                btn = (Button) itemView.findViewById(R.id.ch_vip_recharge_item_btn);
            }
        }
    }

    private boolean isVipOrAuth() {
        if (!isVip) {
            final CHAlertDialog vipDialog = new CHAlertDialog((Activity) context, true, true);
            vipDialog.show();
            vipDialog.setContent("还未达到VIP条件，去看看怎么成为草花VIP吧");
            vipDialog.setCancelButton("取消", new OnClickListener() {
                @Override
                public void onClick(View v) {
                    vipDialog.dismiss();
                }
            });
            vipDialog.setOkButton("去查看", new OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebActivity.startAppLink(context, BaseLogic.HOST_APP + "vip/expLogView");
                    vipDialog.dismiss();
                }
            });
            return false;
        }

        if (!isAuth) {
            final CHAlertDialog vipDialog = new CHAlertDialog((Activity) context, true, true);
            vipDialog.show();
            vipDialog.setContent("还未进行VIP认证，请先前往认证");
            vipDialog.setCancelButton("取消", new OnClickListener() {
                @Override
                public void onClick(View v) {
                    vipDialog.dismiss();
                }
            });
            vipDialog.setOkButton("去认证", new OnClickListener() {
                @Override
                public void onClick(View v) {
                    VipCertificationActivity.start(context);
                    vipDialog.dismiss();
                }
            });
            return false;
        }
        return true;
    }
}
