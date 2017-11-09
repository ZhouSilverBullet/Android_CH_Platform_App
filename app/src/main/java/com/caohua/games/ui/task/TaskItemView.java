package com.caohua.games.ui.task;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.home.DataMgr;
import com.caohua.games.biz.task.DrawAwardLogic;
import com.caohua.games.biz.task.SkipNavigationEntry;
import com.caohua.games.biz.task.TaskDailyEntry;
import com.caohua.games.biz.task.TaskEntry;
import com.caohua.games.biz.task.TaskFilter;
import com.caohua.games.biz.task.TaskForHomeNotify;
import com.caohua.games.biz.task.TaskGameEntry;
import com.caohua.games.biz.task.TaskGrowthEntry;
import com.caohua.games.biz.task.TaskNotifyDotEntry;
import com.caohua.games.ui.account.AccountSettingActivity;
import com.caohua.games.ui.account.PayActionActivity;
import com.caohua.games.ui.adapter.TaskRecyclerAdapter;
import com.caohua.games.ui.find.FindContentActivity;
import com.caohua.games.ui.minegame.MineGameActivity;
import com.caohua.games.ui.ranking.RankingDetailActivity;
import com.caohua.games.ui.search.SearchActivity;
import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.app.AnalyticsHome;
import com.chsdk.configure.DataStorage;
import com.chsdk.model.app.LinkModel;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.LoadingDialog;
import com.chsdk.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by zhouzhou on 2017/4/15.
 */

public class TaskItemView extends LinearLayout {
    private TextView itemTitle;
    private RewardLinearLayout rewardChdou, rewardChbi, rewardby;
    private TaskEntry taskEntry;
    private int position;
    private View itemLayout;
    private SubmitRippleButton submitButton;
    private TaskRecyclerAdapter taskAdapter;
    private RewardLinearLayout rewardXp;

    public TaskItemView(Context context) {
        this(context, null);
    }

    public TaskItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TaskItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        setEvent(context);
    }

    private void setEvent(Context context) {
        setClickable(true);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (taskEntry != null) {
                    TaskDescribeDialog describeDialog = new TaskDescribeDialog(v.getContext(), taskEntry);
                    describeDialog.show();
                }
            }
        });
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.ch_task_recycler_item_content, this, true);
        itemLayout = getView(R.id.ch_task_item_relative_layout);
        itemLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (taskEntry != null) {
                    TaskDescribeDialog describeDialog = new TaskDescribeDialog(v.getContext(), taskEntry);
                    describeDialog.show();
                }
            }
        });
        itemTitle = getView(R.id.ch_task_item_title);
        rewardChdou = getView(R.id.ch_task_reward_chdou);
        rewardChbi = getView(R.id.ch_task_reward_chbi);
        rewardby = getView(R.id.ch_task_reward_chby);
        rewardXp = getView(R.id.ch_task_reward_exp);
        submitButton = getView(R.id.ch_task_item_submit_button);
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int id) {
        return (T) findViewById(id);
    }

    public void setTaskEntry(TaskEntry taskEntry) {
        this.taskEntry = taskEntry;
        itemTitle.setText(taskEntry.getTask_name());
        setReward(taskEntry);
        int statusInt = 0;
        if (!TextUtils.isEmpty(taskEntry.getTask_status())) {
            statusInt = Integer.parseInt(taskEntry.getTask_status());
            setSubmitButton(statusInt);
        }
        setSubmitButtonEvent(statusInt);
    }

    private void setSubmitButtonEvent(final int status) {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (status) {
                    case 1:  //未完成 或者 去完成
                        noComplete(taskEntry.getMethod(), taskEntry.getValue());
                        break;
                    case 2: //领取奖励
                        complete(submitButton);
                        break;
                    case 3:
                        CHToast.show(getContext(), "判断网络重试！");
                        break;
                    case 4:
                        CHToast.show(getContext(), "任务已经完成！");
                        break;
                    case 5:
                        CHToast.show(getContext(), DataStorage.getTaskNotGave(getContext()));
                        break;
                }
            }
        });
    }

    /**
     * 任务完成 领取奖励
     *
     * @param submitButton
     */
    private void complete(final SubmitRippleButton submitButton) {
        final LoadingDialog loadingDialog = new LoadingDialog(getContext(), true);
        loadingDialog.show();
        new DrawAwardLogic(taskEntry.getId()).getDrawAward(new BaseLogic.CommentLogicListener() {
            @Override
            public void failed(String errorMsg, int errorCode) {
                loadingDialog.dismiss();
                checkRedDot();
                if (errorCode == 701) {
                    submitButton.setBackgroundText("领取", 1);
                    CHToast.show(getContext(), errorMsg);
                    TaskForHomeNotify event = new TaskForHomeNotify();
                    event.setRefresh(true);
                    EventBus.getDefault().post(event);
                    taskAdapter.notifyDataSetChanged();
                    return;
                }
                submitButton.setBackgroundText("领取失败");
                CHToast.show(getContext(), errorMsg);
                notifyRecyclerList(3);
            }

            @Override
            public void success(String... entryResult) {
                AnalyticsHome.umOnEvent(AnalyticsHome.TASK_COMPLETE, "任务(Task)领取成功");
                checkRedDot();
                loadingDialog.dismiss();
                submitButton.setBackgroundComplete();
                TaskCompleteDialog completeDialog = new TaskCompleteDialog(getContext(), taskEntry);
                completeDialog.show();
                notifyRecyclerList(4);
                notifyForGrowthTask();
            }
        });
    }

    private void notifyForGrowthTask() {
        int i = 0;
        if (taskEntry instanceof TaskGrowthEntry) {
            List<Object> list = taskAdapter.getList();
            for (Object o : list) {
                if (o instanceof TaskGrowthEntry) {
                    TaskGrowthEntry growthEntry = (TaskGrowthEntry) o;
                    String task_status = growthEntry.getTask_status();
                    if (!TextUtils.isEmpty(task_status) && task_status.equals("4")) {
                        i++;
                    }
                }
            }
            if (i == 5) {
                TaskForHomeNotify notify = new TaskForHomeNotify();
                notify.setRefresh(true);
                EventBus.getDefault().post(notify);
            }
        }
    }

    private void notifyRecyclerList(int status) {
        if (taskEntry instanceof TaskDailyEntry) {
            TaskDailyEntry dailyEntry = (TaskDailyEntry) taskEntry;
            dailyEntry.setTask_status(status + "");
            taskAdapter.replace(dailyEntry, position);
        } else if (taskEntry instanceof TaskGameEntry) {
            TaskGameEntry gameEntry = (TaskGameEntry) taskEntry;
            gameEntry.setTask_status(status + "");
            taskAdapter.replace(gameEntry, position);
        } else if (taskEntry instanceof TaskGrowthEntry) {
            TaskGrowthEntry growthEntry = (TaskGrowthEntry) taskEntry;
            growthEntry.setTask_status(status + "");
            taskAdapter.replace(growthEntry, position);
        }
    }

    private void checkRedDot() {
        if (TaskRecyclerAdapter.statusList.size() > 0) {
            TaskRecyclerAdapter.statusList.remove(TaskRecyclerAdapter.statusList.size() - 1);
            if (TaskRecyclerAdapter.statusList.size() == 0) {
                TaskNotifyDotEntry entry = new TaskNotifyDotEntry();
                entry.setStatus(View.INVISIBLE);
                EventBus.getDefault().post(entry);
            }
        } else {
            TaskNotifyDotEntry entry = new TaskNotifyDotEntry();
            entry.setStatus(View.INVISIBLE);
            EventBus.getDefault().post(entry);
        }
    }

    /**
     * 是否完成判断
     *
     * @param method
     * @param value
     */
    private void noComplete(String method, String value) {
        FindContentActivity activity = (FindContentActivity) getContext();
        if (TextUtils.isEmpty(method)) {
            CHToast.show(getContext(), "您未完成该任务！");
        } else {
            Intent intent = null;
            AnalyticsHome.umOnEvent(AnalyticsHome.TASK_GO_VIEW, "任务(Task)去完成");
            if (TaskFilter.METHOD_RESOURCE.equals(method)) { //跳源生
                switch (value) {
                    case TaskFilter.VALUE_RESOURCE_MY_GAME:
                        intent = new Intent(getContext(), MineGameActivity.class);
                        getContext().startActivity(intent);
                        break;
                    case TaskFilter.VALUE_RESOURCE_RECHARGE:
                        intent = new Intent(getContext(), PayActionActivity.class);
                        getContext().startActivity(intent);
                        break;
                    case TaskFilter.VALUE_RESOURCE_MINE_FRAGMENT: //我的头像 我的
                        SkipNavigationEntry entry = new SkipNavigationEntry();
                        entry.setTabValue(3);
                        EventBus.getDefault().post(entry);
                        activity.finish();
                        break;
                    case TaskFilter.VALUE_RESOURCE_MULTIPLEGAME:
                        RankingDetailActivity.start(getContext(), DataMgr.DATA_TYPE_RANKING_SYNTH);
                        break;
                    case TaskFilter.VALUE_RESOURCE_ACCOUNT_SETTING: //账号设置
                        intent = new Intent(getContext(), AccountSettingActivity.class);
                        getContext().startActivity(intent);
                        break;
                    case TaskFilter.VALUE_RESOURCE_SEARCH_ACTIVITY:
                        intent = new Intent(getContext(), SearchActivity.class);
                        intent.putExtra("task_load", "load");
                        getContext().startActivity(intent);
                        break;
                    case TaskFilter.VALUE_RESOURCE_HOME_PAGER_ACTIVITY:
                        SkipNavigationEntry entryHome = new SkipNavigationEntry();
                        entryHome.setTabValue(0);
                        EventBus.getDefault().post(entryHome);
                        activity.finish();
                        break;
                }
            } else if (TaskFilter.METHOD_URL.equals(method)) { //跳网页
                LinkModel model = null;
                String params = null;
                switch (value) {
                    case TaskFilter.VALUE_URL_CHANGE_PASSWORD: //修改密码

                        break;
                    case TaskFilter.VALUE_URL_PHONE_BINDING: //手机绑定
                        WebActivity.startWebPageGetParam(getContext(), TaskFilter.VALUE_URL_PHONE_BINDING_URL, null);
                        break;
                    case TaskFilter.VALUE_URL_REAL_NAME_REGISTRATION: //实名注册
                        WebActivity.startWebPageGetParam(getContext(),TaskFilter.VALUE_URL_REAL_NAME_REGISTRATION_URL, null);
                        break;
                    default:
                        if (value.contains(TaskFilter.VALUE_URL_OPEN_TASK_DETAIL)) { //阅读攻略类
                            WebActivity.startWebPage(getContext(), value);
                        } else if (value.contains(TaskFilter.VALUE_URL_GAME_DETAIL_URL)) {// 游戏详情页
                            WebActivity.startWebPage(getContext(), value);
                        }
                        break;
                }
            } else if (TaskFilter.METHOD_INTERF.equals(method)) {
                intent = new Intent(activity, FindContentActivity.class);
                intent.putExtra(FindContentActivity.KEY, FindContentActivity.KEY_SHOP);
                activity.startActivity(intent);
            }
        }
    }

    private void setSubmitButton(int statusInt) {
        switch (statusInt) {
            case 1:
                if (TextUtils.isEmpty(taskEntry.getMethod())) {
                    submitButton.setBackgroundText("未完成");
                } else {
                    submitButton.setBackgroundText("去完成");
                }
                break;
            case 2:
                if (TaskRecyclerAdapter.checkSingleValue(position)) {
                    TaskRecyclerAdapter.statusList.add(position);
                    LogUtil.errorLog("TaskItemView setSubmitButton size = " + TaskRecyclerAdapter.statusList.size());
                }
                if (TaskRecyclerAdapter.statusList.size() == 1) {
                    TaskNotifyDotEntry entry = new TaskNotifyDotEntry();
                    entry.setStatus(View.VISIBLE);
                    EventBus.getDefault().post(entry);
                }
                submitButton.setBackgroundText("领取");
                break;
            case 3:
                submitButton.setBackgroundText("领取失败");
                break;
            case 4:
                submitButton.setBackgroundComplete();
                break;
            case 5:
                submitButton.setBackgroundText("领取", 1);
                break;
        }
    }

    /**
     * 设置草花豆，草花币，绑银
     *
     * @param taskEntry
     */
    private void setReward(TaskEntry taskEntry) {
        setReward(rewardChdou, taskEntry.getAward_points(), "草花豆", R.color.ch_color_download_normal_title
                , R.drawable.ch_task_caohuadou_icon, RewardLinearLayout.CAO_HUA_DOU);
        setReward(rewardChbi, taskEntry.getAward_money(), "草花币", R.color.ch_color_download_pause,
                R.drawable.ch_task_caohuabi_icon, RewardLinearLayout.CAO_HUA_BI);
        setReward(rewardby, taskEntry.getAward_silver(), "绑银", R.color.ch_red_text,
                R.drawable.ch_task_silver_icon, RewardLinearLayout.CAO_HUA_BANG_YIN);
        setReward(rewardXp, taskEntry.getAward_growexp(), "成长值", R.color.ch_task_purple,
                R.drawable.ch_task_jingyan_icon, RewardLinearLayout.CAO_HUA_EXP);
    }

    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * 设置奖励的值
     *
     * @param layout
     * @param count
     */
    private void setReward(RewardLinearLayout layout, String count, String describe,
                           int bg, int drawable, int type) {
        if (!TextUtils.isEmpty(count)) {
            int points = Integer.parseInt(count);
            if (points != 0) {
                layout.setVisibility(View.VISIBLE);
                layout.setPosition(position);
                layout.resetStatus("+" + points + describe,
                        bg,
                        drawable, type);
            } else {
                layout.setVisibility(View.GONE);
            }
        } else {
            layout.setVisibility(View.GONE);
        }

    }

    public void setTaskAdapter(TaskRecyclerAdapter taskAdapter) {
        this.taskAdapter = taskAdapter;
    }
}
