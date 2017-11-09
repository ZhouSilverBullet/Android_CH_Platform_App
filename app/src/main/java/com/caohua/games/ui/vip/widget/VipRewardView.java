package com.caohua.games.ui.vip.widget;

import android.app.Activity;
import android.content.Context;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.vip.CHVipNotify;
import com.caohua.games.biz.vip.VipEntry;
import com.caohua.games.biz.vip.VipGetRewardLogic;
import com.caohua.games.biz.vip.VipRebateDetailSubmitLogic;
import com.caohua.games.ui.adapter.ItemDivider;
import com.caohua.games.ui.adapter.ViewHolder;
import com.caohua.games.ui.giftcenter.GiftDetailActivity;
import com.caohua.games.ui.vip.VipCertificationActivity;
import com.caohua.games.ui.vip.VipRewardSecondActivity;
import com.chsdk.biz.BaseLogic;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.CHAlertDialog;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.LoadingDialog;
import com.chsdk.utils.PicUtil;
import com.chsdk.utils.ViewUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by admin on 2017/8/22.
 */

public class VipRewardView extends LinearLayout {

    private Context context;
    private VipTitleView titleView;
    private RecyclerView recyclerView;
    private VipRewardAdapter adapter;
    private boolean isVip;
    private boolean isAuth;

    public VipRewardView(Context context) {
        this(context, null);
    }

    public VipRewardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VipRewardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.ch_vip_reward_view, this, true);
        setVisibility(GONE);
        titleView = (VipTitleView) findViewById(R.id.ch_vip_reward_title_view);
        titleView.getMoreText().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //我的奖励
                VipRewardSecondActivity.start(context, isAuth, isVip);
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.ch_vip_reward_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.addItemDecoration(new ItemDivider().setDividerWidth(ViewUtil.dp2px(context, 1)).setDividerColor(getResources().getColor(R.color.ch_gray)));
        adapter = new VipRewardAdapter(context, new ArrayList<VipEntry.DataBean.RewardBean>());
        recyclerView.setAdapter(adapter);
    }

    public void setData(List<VipEntry.DataBean.RewardBean> list, boolean isAuth, boolean isVip) {
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
        private List<VipEntry.DataBean.RewardBean> list;

        public VipRewardAdapter(Context context, List<VipEntry.DataBean.RewardBean> list) {
            this.context = context;
            this.list = list;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public VipRewardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VipRewardViewHolder(inflater.inflate(R.layout.ch_vip_reward_item_view, parent, false));
        }

        @Override
        public void onBindViewHolder(final VipRewardViewHolder holder, int position) {
            VipEntry.DataBean.RewardBean bean = list.get(position);
            if (!TextUtils.isEmpty(bean.getTag())) {
                holder.progressLayout.setVisibility(VISIBLE);
                holder.content.setVisibility(GONE);
                holder.progressValue.setText(bean.getRate() + "%");
                holder.progress.setProgress(bean.getRate());
                final String gift_id = bean.getGift_id();
                holder.btn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GiftDetailActivity.start(context, gift_id, "", GiftDetailActivity.TYPE_NORMAL);
//                        Intent intent = new Intent(context, GetGiftDetailsActivity.class);
//                        intent.putExtra("path", gift_id);
//                        intent.putExtra("type", 100);
//                        List<ResolveInfo> resolveInfo = context.getPackageManager().queryIntentActivities(intent,
//                                PackageManager.MATCH_DEFAULT_ONLY);
//                        if (resolveInfo != null && resolveInfo.size() > 0) {
//                            context.startActivity(intent);
//                        } else {
//                            CHToast.show(context, "打开礼包详情页面出错");
//                        }
                    }
                });
            } else {
                holder.progressLayout.setVisibility(INVISIBLE);
                holder.content.setVisibility(VISIBLE);
                holder.content.setText(bean.getReward());
                final int getType = bean.getGet_type();
                switch (getType) {
                    case 1:
                        holder.icon.setImageResource(R.drawable.ch_vip_reward_vip);
                        break;
                    case 2:
                        holder.icon.setImageResource(R.drawable.ch_vip_reward_gift);
                        break;
                    case 3:
                        PicUtil.displayImg(context, holder.icon, bean.getGame_icon(), R.drawable.ch_default_apk_icon);
                        break;
                }

                int isActive = bean.getIs_active();
                int hasGet = bean.getHas_get();
                if (isAuth) {
                    if (isVip) { //是vip
                        if (isActive == 1) { //有资格
                            if (hasGet == 0) {
                                holder.btn.setText("领取");
                                holder.btn.setBackgroundResource(R.drawable.ch_vip_draw_button_bg);
                            } else {
                                holder.btn.setText("已领取");
                                holder.btn.setBackgroundResource(R.drawable.ch_vip_draw_button_not_auth_bg);
                            }
                        } else {
                            holder.btn.setText("领取");
                            holder.btn.setBackgroundResource(R.drawable.ch_vip_draw_button_not_auth_bg);
                        }
                    } else {
                        holder.btn.setBackgroundResource(R.drawable.ch_vip_draw_button_bg);
                    }
                } else {
                    holder.btn.setBackgroundResource(R.drawable.ch_vip_draw_button_bg);
                }
                final int rebate_id = bean.getRebate_id();
                holder.btn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isVipOrAuth()) {
                            return;
                        }

                        final LoadingDialog dialog = new LoadingDialog(context, "");
                        dialog.show();
                        if (getType == 3) {
                            new VipRebateDetailSubmitLogic().getRebateDetailSubmit(rebate_id + "", new BaseLogic.DataLogicListner<String>() {
                                @Override
                                public void failed(String errorMsg, int errorCode) {
                                    CHToast.show(context, errorMsg);
                                    dialog.dismiss();
                                }

                                @Override
                                public void success(String entryResult) {
                                    CHToast.show(context, "领取成功！");
                                    holder.btn.setText("已领取");
                                    EventBus.getDefault().post(new CHVipNotify());
                                    holder.btn.setBackgroundResource(R.drawable.ch_vip_draw_button_not_auth_bg);
                                    dialog.dismiss();
                                }
                            });
                        } else {
                            VipGetRewardLogic logic = new VipGetRewardLogic();
                            logic.getReward(getType, new BaseLogic.DataLogicListner<String>() {
                                @Override
                                public void failed(String errorMsg, int errorCode) {
                                    CHToast.show(context, errorMsg);
                                    dialog.dismiss();
                                }

                                @Override
                                public void success(String value) {
                                    CHToast.show(context, "领取成功！");
                                    holder.btn.setText("已领取");
                                    EventBus.getDefault().post(new CHVipNotify());
                                    holder.btn.setBackgroundResource(R.drawable.ch_vip_draw_button_not_auth_bg);
                                    dialog.dismiss();
                                }
                            });
                        }
                    }
                });

            }
            holder.title.setText(bean.getName());
            if (!TextUtils.isEmpty(bean.getGame_icon())) {
                PicUtil.displayImg(context, holder.icon, bean.getGame_icon(), R.drawable.ch_default_apk_icon);
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

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }

        public void addAll(Collection<VipEntry.DataBean.RewardBean> collection) {
            if (list != null) {
                list.clear();
                list.addAll(collection);
                notifyDataSetChanged();
            }
        }

        class VipRewardViewHolder extends ViewHolder {

            private ProgressBar progress;
            private TextView progressValue;
            private View progressLayout;
            private Button btn;
            private TextView content;
            private TextView title;
            private ImageView icon;

            public VipRewardViewHolder(View itemView) {
                super(itemView);
                icon = (ImageView) itemView.findViewById(R.id.ch_vip_reward_item_icon);
                title = (TextView) itemView.findViewById(R.id.ch_vip_reward_item_title);
                content = (TextView) itemView.findViewById(R.id.ch_vip_reward_item_content);
                btn = (Button) itemView.findViewById(R.id.ch_vip_reward_item_btn);
                progressValue = (TextView) itemView.findViewById(R.id.ch_vip_reward_view_progress_value);
                progressLayout = itemView.findViewById(R.id.ch_vip_reward_view_progress_layout);
                progress = (ProgressBar) itemView.findViewById(R.id.ch_vip_reward_view_progress);
            }
        }
    }

}
