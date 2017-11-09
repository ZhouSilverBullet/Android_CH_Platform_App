package com.caohua.games.ui.vip.more;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.vip.CHVipNotify;
import com.caohua.games.biz.vip.RebateSubmitNotify;
import com.caohua.games.biz.vip.VipGetRewardLogic;
import com.caohua.games.biz.vip.VipRebateDetailSubmitLogic;
import com.caohua.games.biz.vip.VipRewardGameEntry;
import com.caohua.games.biz.vip.VipRewardSecondNotify;
import com.caohua.games.ui.adapter.ViewHolder;
import com.chsdk.biz.BaseLogic;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.LoadingDialog;
import com.chsdk.utils.PicUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by admin on 2017/8/26.
 */

public class VipRebateRewardListView extends VipCommonListView<VipRewardGameEntry.DataBean.RebateBean> {

    public VipRebateRewardListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int recyclerItemLayoutId() {
        return R.layout.ch_vip_reward_item_view;
    }

    @Override
    protected void onCommonCovert(ViewHolder holder, final VipRewardGameEntry.DataBean.RebateBean entry) {
        ImageView icon = holder.getView(R.id.ch_vip_reward_item_icon);
        TextView title = holder.getView(R.id.ch_vip_reward_item_title);
        TextView content = holder.getView(R.id.ch_vip_reward_item_content);
        holder.getView(R.id.ch_vip_reward_view_progress_layout).setVisibility(GONE);
        final Button btn = holder.getView(R.id.ch_vip_reward_item_btn);
        String name = entry.getName();
        String reward = entry.getReward();
        title.setText(name);
        content.setText(reward);
        PicUtil.displayImg(context, icon, entry.getGame_icon(), R.drawable.ch_default_apk_icon);
        int isActive = entry.getIs_active();
        int hasGet = entry.getHas_get();
        if (isAuth) {
            if (isVip) { //是vip
                if (isActive == 1) { //有资格
                    if (hasGet == 0) {
                        btn.setText("领取");
                        btn.setBackgroundResource(R.drawable.ch_vip_draw_button_bg);
                    } else {
                        btn.setText("已领取");
                        btn.setBackgroundResource(R.drawable.ch_vip_draw_button_not_auth_bg);
                    }
                } else {
                    btn.setText("领取");
                    btn.setBackgroundResource(R.drawable.ch_vip_draw_button_not_auth_bg);
                }
            } else {
                btn.setBackgroundResource(R.drawable.ch_vip_draw_button_bg);
            }
        } else {
            btn.setBackgroundResource(R.drawable.ch_vip_draw_button_bg);
        }
        final int rebate_id = entry.getRebate_id();
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isVipOrAuth()) {
                    return;
                }

                final LoadingDialog dialog = new LoadingDialog(context, "");
                dialog.show();
                new VipRebateDetailSubmitLogic().getRebateDetailSubmit(rebate_id + "", new BaseLogic.DataLogicListner<String>() {
                    @Override
                    public void failed(String errorMsg, int errorCode) {
                        CHToast.show(context, errorMsg);
                        dialog.dismiss();
                    }

                    @Override
                    public void success(String entryResult) {
                        CHToast.show(context, "领取成功！");
                        btn.setText("已领取");
                        EventBus.getDefault().post(new CHVipNotify());
                        EventBus.getDefault().post(new RebateSubmitNotify());
                        btn.setBackgroundResource(R.drawable.ch_vip_draw_button_not_auth_bg);
                        dialog.dismiss();
                    }
                });

            }
        });
    }
}
