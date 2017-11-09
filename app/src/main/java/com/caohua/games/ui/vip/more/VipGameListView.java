package com.caohua.games.ui.vip.more;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.vip.VipRewardGameEntry;
import com.caohua.games.ui.adapter.ViewHolder;
import com.caohua.games.ui.giftcenter.GiftDetailActivity;
import com.chsdk.utils.PicUtil;

/**
 * Created by admin on 2017/8/29.
 */

public class VipGameListView extends VipCommonListView<VipRewardGameEntry.DataBean.GameBean> {
    public VipGameListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int recyclerItemLayoutId() {
        return R.layout.ch_vip_reward_item_view;
    }

    @Override
    protected void onCommonCovert(ViewHolder holder, VipRewardGameEntry.DataBean.GameBean gameBean) {
        ImageView icon = (ImageView) holder.getView(R.id.ch_vip_reward_item_icon);
        TextView title = (TextView) holder.getView(R.id.ch_vip_reward_item_title);
        TextView content = (TextView) holder.getView(R.id.ch_vip_reward_item_content);
        holder.getView(R.id.ch_vip_reward_view_progress_layout).setVisibility(VISIBLE);
        ProgressBar progressBar = holder.getView(R.id.ch_vip_reward_view_progress);
        TextView progressValue = holder.getView(R.id.ch_vip_reward_view_progress_value);
        content.setVisibility(GONE);
        Button btn = holder.getView(R.id.ch_vip_reward_item_btn);
        final String gift_id = gameBean.getGift_id();
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                GiftDetailActivity.start(context, gift_id, "", GiftDetailActivity.TYPE_NORMAL);
//                Intent intent = new Intent(context, GetGiftDetailsActivity.class);
//                intent.putExtra("path", gift_id);
//                intent.putExtra("type", 100);
//                List<ResolveInfo> resolveInfo = context.getPackageManager().queryIntentActivities(intent,
//                        PackageManager.MATCH_DEFAULT_ONLY);
//                if (resolveInfo != null && resolveInfo.size() > 0) {
//                    context.startActivity(intent);
//                } else {
//                    CHToast.show(context, "打开礼包详情页面出错");
//                }
            }
        });
        String gameIcon = gameBean.getGame_icon();
        String name = gameBean.getName();
        float rate = gameBean.getRate();
        String tag = gameBean.getTag();
        if (!TextUtils.isEmpty(gameIcon)) {
            PicUtil.displayImg(context, icon, gameIcon, R.drawable.ch_default_apk_icon);
        }
        title.setText(name);
        if (rate >= 0) {
            int progress = (int) rate;
            progressBar.setProgress(progress);
            progressValue.setText(progress + "%");
        }

    }
}
