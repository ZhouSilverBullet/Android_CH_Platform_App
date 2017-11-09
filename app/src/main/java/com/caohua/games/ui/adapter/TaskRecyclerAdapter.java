package com.caohua.games.ui.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.home.DataMgr;
import com.caohua.games.biz.task.TaskDailyEntry;
import com.caohua.games.biz.task.TaskEntry;
import com.caohua.games.biz.task.TaskGameEntry;
import com.caohua.games.biz.task.TaskGrowthEntry;
import com.caohua.games.biz.task.TaskTitleEntry;
import com.caohua.games.ui.ranking.RankingDetailActivity;
import com.caohua.games.ui.task.TaskItemView;
import com.chsdk.utils.LogUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by zhouzhou on 2017/2/21.
 */

public class TaskRecyclerAdapter extends RecyclerView.Adapter<TaskRecyclerAdapter.TaskHolder> {
    private final LayoutInflater inflater;
    private Context context;
    private List<Object> list;
    public static List<Integer> statusList = new ArrayList<>();
    private static LinearLayoutManager layoutManager;

    public TaskRecyclerAdapter(Context context, List<Object> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public TaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == R.layout.ch_task_recycler_item_content) {
            view = new TaskItemView(context);
        } else {
            view = inflater.inflate(viewType, parent, false);
        }
        return new TaskHolder(view);
    }

    @Override
    public void onBindViewHolder(final TaskHolder holder, int position) {
        if (position == 0) {
            holder.titleImage.setImageResource(R.drawable.ch_task_image_top);
            holder.titleImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            return;
        }

        Object entry = list.get(position - 1);

        if (entry instanceof TaskTitleEntry) {
            TaskTitleEntry titleEntry = (TaskTitleEntry) entry;
            if (titleEntry.getPosition() == 3) {
                holder.titleIcon.setImageResource(R.drawable.ch_task_growth_title_bg);
            } else if (titleEntry.getPosition() == 1) {
                holder.titleIcon.setImageResource(R.drawable.ch_task_daily_title_bg);
            } else if (titleEntry.getPosition() == 2) {
                holder.titleIcon.setImageResource(R.drawable.ch_task_game_title_bg);
            }
        } else {
            if (entry instanceof TaskEntry && holder.itemView instanceof TaskItemView) {
                LogUtil.errorLog("TaskRecyclerAdapter list.get(position): " + entry);
                TaskItemView itemView = (TaskItemView) holder.itemView;
                itemView.setPosition(position);
                itemView.setTaskAdapter(this);
                itemView.setTaskEntry((TaskEntry) entry);
            }
        }
        if (holder.textView != null) {
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RankingDetailActivity.start(v.getContext(), DataMgr.DATA_TYPE_RANKING_SYNTH);
                }
            });
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (layoutManager == null) {
            layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        }

    }

    public static LinearLayoutManager getScrollYDistance() {
//        //获取最后一个可见view的位置
//        int lastItemPosition = 0;
//        if (layoutManager != null) {
//            lastItemPosition = layoutManager.findLastVisibleItemPosition();
//        }
        return layoutManager;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return R.layout.ch_task_item_image_top;
        } else {
            Object o = list.get(position - 1);
            if (o instanceof TaskTitleEntry) {
                return R.layout.ch_task_recycler_item_title;
            } else if (o instanceof TaskDailyEntry) {
                TaskDailyEntry dailyEntry = (TaskDailyEntry) o;
                if (TextUtils.isEmpty(dailyEntry.getTask_name())) {
                    return R.layout.ch_task_recycler_item_error;
                } else {
                    return R.layout.ch_task_recycler_item_content;
                }
            } else if (o instanceof TaskGameEntry) {
                TaskGameEntry gameEntry = (TaskGameEntry) o;
                if (TextUtils.isEmpty(gameEntry.getTask_name())) {
                    return R.layout.ch_task_recycler_item_error;
                } else {
                    return R.layout.ch_task_recycler_item_content;
                }
            } else if (o instanceof TaskGrowthEntry) {
                TaskGrowthEntry growthEntry = (TaskGrowthEntry) o;
                if (TextUtils.isEmpty(growthEntry.getTask_name())) {
                    return R.layout.ch_task_recycler_text;
                } else {
                    return R.layout.ch_task_recycler_item_content;
                }
            }
        }
        return R.layout.ch_task_recycler_item_content;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : (list.size() + 1);
    }

    public void addAll(Collection collection) {
        if (list != null) {
            list.addAll(collection);
            notifyDataSetChanged();
        }
    }

    public void replace(Object entry, int position) {
        if (list != null) {
            LogUtil.errorLog("TaskRecyclerAdapter replace: " + position);
            list.remove(position - 1);
            list.add(position - 1, entry);
            statusList.clear();
            notifyDataSetChanged();
        }
    }

    public static boolean checkSingleValue(int position) {
        int indexOf = statusList.indexOf(position);
        return indexOf == -1;
    }

    public void clearAll() {
        if (list != null) {
            list.clear();
            statusList.clear();
        }
    }

    public List<Object> getList() {
        return list;
    }

    class TaskHolder extends RecyclerView.ViewHolder {
        private TextView completeText;
        private ImageView titleIcon;
        private ImageView titleImage;
        private TextView textView;

        public TaskHolder(View itemView) {
            super(itemView);
            titleImage = (ImageView) itemView.findViewById(R.id.ch_task_item_title_image);
            titleIcon = (ImageView) itemView.findViewById(R.id.ch_task_item_title_icon);
            textView = (TextView) itemView.findViewById(R.id.ch_task_ti_yan_game);
            completeText = ((TextView) itemView.findViewById(R.id.ch_text_task_complete));
        }
    }
}
