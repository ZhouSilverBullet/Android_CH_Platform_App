package com.caohua.games.ui.vip.more;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.vip.VipRechargeAwardEntry;
import com.caohua.games.ui.adapter.CommonRecyclerViewAdapter;
import com.caohua.games.ui.adapter.ItemDivider;
import com.caohua.games.ui.adapter.ViewHolder;
import com.caohua.games.ui.coupon.CouponCenterActivity;
import com.caohua.games.ui.vip.VipCertificationActivity;
import com.caohua.games.ui.vip.widget.VipTitleView;
import com.chsdk.biz.BaseLogic;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.CHAlertDialog;
import com.chsdk.utils.ViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/8/25.
 */

public class VipCouponListView extends LinearLayout {
    private Context context;
    private VipTitleView titleView;
    private RecyclerView recyclerView;
    private CouponAdapter adapter;
    private boolean isVip;
    private boolean isAuth;

    public VipCouponListView(Context context) {
        this(context, null);
    }

    public VipCouponListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VipCouponListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.ch_vip_coupon_list_view, this, true);
        setVisibility(GONE);
        titleView = (VipTitleView) findViewById(R.id.ch_vip_coupon_title_view);
        titleView.setIconAndMoreTextGone();
        recyclerView = (RecyclerView) findViewById(R.id.ch_vip_coupon_recycler_view);
        recyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new ItemDivider().setDividerWidth(ViewUtil.dp2px(context, 1)).setDividerColor(getResources().getColor(R.color.ch_gray)));
        adapter = new CouponAdapter(context, new ArrayList<VipRechargeAwardEntry.DataBean.CouponBean>(), R.layout.ch_vip_coupon_list_item_view);
        recyclerView.setAdapter(adapter);
    }

    public void setData(List<VipRechargeAwardEntry.DataBean.CouponBean> list, boolean isAuth, boolean isVip) {
        if (list == null || list.size() == 0) {
            setVisibility(GONE);
            return;
        }
        this.isVip = isVip;
        this.isAuth = isAuth;
        setVisibility(VISIBLE);
        adapter.addAll(list);
    }

    private class CouponAdapter extends CommonRecyclerViewAdapter<VipRechargeAwardEntry.DataBean.CouponBean> {

        public CouponAdapter(Context context, List<VipRechargeAwardEntry.DataBean.CouponBean> list, int layoutId) {
            super(context, list, layoutId);
        }

        @Override
        protected void covert(ViewHolder holder, VipRechargeAwardEntry.DataBean.CouponBean entry) {
            TextView number = (TextView) holder.getView(R.id.ch_vip_coupon_list_item_number);
            TextView limit = holder.getView(R.id.ch_vip_coupon_list_item_limit);
            TextView content = holder.getView(R.id.ch_vip_coupon_list_item_content);
            View btn = holder.getView(R.id.ch_vip_coupon_list_item_btn);
            String name = entry.getName();
            content.setText(name);
            limit.setText(entry.getMin_amt());
            if ("1".equals(entry.getUse_type())) {
                number.setText(entry.getUse_amt() + "￥");
            } else {
                number.setText(entry.getUse_amt());
            }

            btn.setOnClickListener(new OnClickListener() {
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

}
