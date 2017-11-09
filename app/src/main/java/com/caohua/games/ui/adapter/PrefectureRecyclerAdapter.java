package com.caohua.games.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.prefecture.PrefectureEntry;
import com.caohua.games.biz.task.TaskFilter;
import com.caohua.games.ui.bbs.BBSActivity;
import com.caohua.games.ui.prefecture.PrefectureListActivity;
import com.chsdk.biz.app.AnalyticsHome;
import com.chsdk.ui.WebActivity;

import java.util.Collection;
import java.util.List;

/**
 * Created by zhouzhou on 2017/5/8.
 */

public class PrefectureRecyclerAdapter extends RecyclerView.Adapter<PrefectureRecyclerAdapter.PrefectureViewHolder> {
    private LayoutInflater inflater;
    private List<PrefectureEntry.DataBean.MainBannerBean> list;
    private Context context;

    public PrefectureRecyclerAdapter(Context context, List<PrefectureEntry.DataBean.MainBannerBean> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public PrefectureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PrefectureViewHolder(inflater.inflate(R.layout.ch_prefecture_recycler_item, parent, false));
    }

    @Override
    public void onBindViewHolder(PrefectureViewHolder holder, int position) {
        final PrefectureEntry.DataBean.MainBannerBean bean = list.get(position);
        doItemBg(holder.imageView, bean.getBanner_type());
        holder.textView.setText(bean.getName());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryTarget(bean);
            }
        });
    }

    public void categoryTarget(PrefectureEntry.DataBean.MainBannerBean categoryTarget) {
        switch (categoryTarget.getType()) {
            case TaskFilter.METHOD_URL:
                WebActivity.startWebPage(context, categoryTarget.getUrl());
                umOnEvent(TaskFilter.METHOD_URL);
                break;
            case TaskFilter.METHOD_RESOURCE:
                if (TaskFilter.VALUE_RESOURCE_BBS_ACTIVITY.equals(categoryTarget.getTarget())) {
                    Intent intent = new Intent(context, BBSActivity.class);
                    intent.putExtra("forumId", categoryTarget.getId());
                    context.startActivity(intent);
                } else if (TaskFilter.VALUE_RESOURCE_LIST_ACTIVITY.equals(categoryTarget.getTarget())) {
                    Intent intent = new Intent(context, PrefectureListActivity.class);
                    intent.putExtra("listId", categoryTarget.getId());
                    intent.putExtra("titleName", categoryTarget.getName());
                    context.startActivity(intent);
                }
                umOnEvent(TaskFilter.METHOD_URL);
                break;
        }
    }

    private void umOnEvent(String methodUrl) {
        AnalyticsHome.umOnEvent(AnalyticsHome.SUBJECT_MAIN_TAB, methodUrl + "：7个item的二级界面");
    }

    private void doItemBg(ImageView imageView, int itemBg) {
        switch (itemBg) {
            case 1: //资讯
                imageView.setImageResource(R.drawable.ch_prefecture_icon_news);
                break;
            case 2: //活动
                imageView.setImageResource(R.drawable.ch_prefecture_icon_act);
                break;
            case 3: //攻略
                imageView.setImageResource(R.drawable.ch_prefecture_icon_strategy);
                break;
            case 4: //公告
                imageView.setImageResource(R.drawable.ch_prefecture_icon_notice);
                break;
            case 6: //专题
                imageView.setImageResource(R.drawable.ch_prefecture_icon_project);
                break;
            case 96: //链接
                imageView.setImageResource(R.drawable.ch_prefecture_icon_link);
                break;
            case 97: //礼包
                imageView.setImageResource(R.drawable.ch_prefecture_icon_gift);
                break;
            case 98: //游戏论坛
                imageView.setImageResource(R.drawable.ch_prefecture_icon_bbs);
                break;
            case 99: //综合论坛
                imageView.setImageResource(R.drawable.ch_prefecture_icon_all_bbs);
                break;
            default: //其他
                imageView.setImageResource(R.drawable.ch_prefecture_icon_other);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void addAll(Collection<PrefectureEntry.DataBean.MainBannerBean> data) {
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
            layout = itemView.findViewById(R.id.ch_prefecture_recycler_item_layout);
            textView = ((TextView) itemView.findViewById(R.id.ch_prefecture_recycler_item_text));
            imageView = ((ImageView) itemView.findViewById(R.id.ch_prefecture_recycler_item_image));
        }
    }
}
