package com.caohua.games.ui.prefecture;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.prefecture.PrefectureEntry;
import com.caohua.games.biz.task.TaskFilter;
import com.caohua.games.ui.bbs.BBSActivity;
import com.chsdk.biz.app.AnalyticsHome;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.RiffEffectImageButton;
import com.chsdk.ui.widget.RiffEffectLinearLayout;
import com.chsdk.ui.widget.RiffEffectRelativeLayout;

/**
 * Created by zhouzhou on 2017/5/9.
 */

public class PrefectureRecyclerItemView extends RiffEffectLinearLayout {

    private TextView gridText;
    private ImageView imageButton;

    public PrefectureRecyclerItemView(Context context) {
        super(context);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.ch_prefecture_recycler_item, this, true);
        setGravity(Gravity.CENTER);
        setOrientation(VERTICAL);
        gridText = (TextView) findViewById(R.id.ch_prefecture_recycler_item_text);
        imageButton = ((ImageView) findViewById(R.id.ch_prefecture_recycler_item_image));
    }

    public void setData(final PrefectureEntry.DataBean.MainBannerBean bean) {
        gridText.setText(bean.getName());
        doItemBg(bean.getBanner_type());
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryTarget(bean);
            }
        });
    }

    public void categoryTarget(PrefectureEntry.DataBean.MainBannerBean categoryTarget) {
        switch (categoryTarget.getType()) {
            case TaskFilter.METHOD_URL:
                WebActivity.startWebPage(getContext(), categoryTarget.getUrl());
                umOnEvent(TaskFilter.METHOD_URL);
                break;
            case TaskFilter.METHOD_RESOURCE:
                if (TaskFilter.VALUE_RESOURCE_BBS_ACTIVITY.equals(categoryTarget.getTarget())) {
                    Intent intent = new Intent(getContext(), BBSActivity.class);
                    intent.putExtra("forumId", categoryTarget.getId());
                    getContext().startActivity(intent);
                } else if (TaskFilter.VALUE_RESOURCE_LIST_ACTIVITY.equals(categoryTarget.getTarget())) {
                    Intent intent = new Intent(getContext(), PrefectureListActivity.class);
                    intent.putExtra("listId", categoryTarget.getId());
                    intent.putExtra("titleName", categoryTarget.getName());
                    getContext().startActivity(intent);
                }
                umOnEvent(TaskFilter.METHOD_URL);
                break;
        }
    }

    private void umOnEvent(String methodUrl) {
        AnalyticsHome.umOnEvent(AnalyticsHome.SUBJECT_MAIN_TAB, methodUrl + "：7个item的二级界面");
    }

    private void doItemBg(int itemBg) {
        switch (itemBg) {
            case 1: //资讯
                imageButton.setBackgroundResource(R.drawable.ch_prefecture_icon_bbs);
                break;
            case 2: //活动
                imageButton.setBackgroundResource(R.drawable.ch_prefecture_icon_bbs);
                break;
            case 3: //攻略
                imageButton.setBackgroundResource(R.drawable.ch_prefecture_icon_project);
                break;
            case 4: //公告
                imageButton.setBackgroundResource(R.drawable.ch_prefecture_icon_project);
                break;
            case 5: //测评

                break;
            case 6: //专题
                imageButton.setBackgroundResource(R.drawable.ch_prefecture_icon_project);
                break;
            case 96: //链接
                imageButton.setBackgroundResource(R.drawable.ch_prefecture_icon_project);
                break;
            case 97: //礼包
                imageButton.setBackgroundResource(R.drawable.ch_prefecture_icon_strategy);
                break;
            case 98: //游戏论坛
                imageButton.setBackgroundResource(R.drawable.ch_prefecture_icon_strategy);
                break;
            case 99: //综合论坛
                imageButton.setBackgroundResource(R.drawable.ch_prefecture_icon_strategy);
                break;
            default: //其他
                imageButton.setBackgroundResource(R.drawable.ch_prefecture_icon_strategy);
                break;
        }
    }
}
