package com.caohua.games.ui.task;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.home.DataMgr;
import com.caohua.games.biz.task.RewardItemBean;
import com.caohua.games.biz.task.SkipNavigationEntry;
import com.caohua.games.biz.task.TaskEntry;
import com.caohua.games.biz.task.TaskFilter;
import com.caohua.games.ui.account.AccountSettingActivity;
import com.caohua.games.ui.account.PayActionActivity;
import com.caohua.games.ui.find.FindContentActivity;
import com.caohua.games.ui.minegame.MineGameActivity;
import com.caohua.games.ui.ranking.RankingDetailActivity;
import com.caohua.games.ui.search.SearchActivity;
import com.chsdk.model.app.LinkModel;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.utils.ViewUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhou on 2017/3/14.
 */

public class TaskDescribeDialog extends TaskDialog {
    private Button btnComptele, btnKnow;
    private TextView tvTitle, tvContent;
    private String title;
    private String content;
    private int growExp;
    private int points;
    private int money;
    private int silver;
    private View describeOneLayout;
    private View describeTwoLayout;
    private View describeThreeLayout;
    private ImageView describeImage1;
    private ImageView describeImage2;
    private ImageView describeImage3;
    private ImageView describeImage4;
    private TextView describeText1;
    private TextView describeText2;
    private TextView describeText3;
    private TextView describeText4;
    private View divider;
    private String method;
    private String value;
    private View divider2;
    private View taskLL2;
    private String status;
    private View describeFourLayout;
    private TaskEntry taskEntry;
    private List<RewardItemBean> beanList;

    public TaskDescribeDialog(Context context, TaskEntry taskEntry) {
        super(context, taskEntry);
        this.context = context;
        this.taskEntry = taskEntry;
        growExp = stringToInt(taskEntry.getAward_growexp());
        points = stringToInt(taskEntry.getAward_points());
        money = stringToInt(taskEntry.getAward_money());
        silver = stringToInt(taskEntry.getAward_silver());
        method = taskEntry.getMethod();
        value = taskEntry.getValue();
        status = taskEntry.getTask_status();
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int getLayoutId() {
        return R.layout.ch_dialog_task_describe_app;
    }

    protected void initChildView(View view) {
        ViewUtil.setBackground(view, ViewUtil.addBtnBackgroundRound("#f4f4f4", true, ViewUtil.dp2px(context, 3)));
        tvTitle = ViewUtil.getView(view, R.id.ch_task_describe_dialog_alert_title);
        tvContent = ViewUtil.getView(view, R.id.ch_task_describe_dialog_alert_content);
//        tvContent.setText(content);
        tvContent.setText(taskEntry.getTask_desc());

        beanList = new ArrayList<>();
        if (points != 0) {
            RewardItemBean rewardItemBean =
                    new RewardItemBean(points, "+" + points + "草花豆", R.drawable.ch_task_caohuadou_icon, context.getResources().getColor(R.color.ch_color_download_normal_title));
            beanList.add(rewardItemBean);
        }

        if (money != 0) {
            RewardItemBean rewardItemBean =
                    new RewardItemBean(money, "+" + money + "草花币", R.drawable.ch_task_caohuabi_icon, context.getResources().getColor(R.color.ch_color_download_pause));
            beanList.add(rewardItemBean);
        }

        if (silver != 0) {
            RewardItemBean rewardItemBean =
                    new RewardItemBean(silver, "+" + silver + "绑银\t\t", R.drawable.ch_task_silver_icon, context.getResources().getColor(R.color.ch_red_text));
            beanList.add(rewardItemBean);
        }

        if (growExp != 0) {
            RewardItemBean rewardItemBean =
                    new RewardItemBean(growExp, "+" + growExp + "成长值", R.drawable.ch_task_jingyan_icon, context.getResources().getColor(R.color.ch_task_purple));
            beanList.add(rewardItemBean);
        }

        taskLL2 = ViewUtil.getView(view, R.id.ch_task_ll2);
        divider = ViewUtil.getView(view, R.id.ch_task_divider_1);
        describeOneLayout = ViewUtil.getView(view, R.id.ch_caohua_task_describe_one);
        describeImage1 = ViewUtil.getView(view, R.id.ch_task_image1);
        describeText1 = ViewUtil.getView(view, R.id.ch_task_text1);
        describeTwoLayout = ViewUtil.getView(view, R.id.ch_caohua_task_describe_two);
        int size = beanList.size();
        switch (size) {
            case 1:
                describeImage1.setImageResource(beanList.get(0).resDrawable);
                describeText1.setText(beanList.get(0).textValue);
                describeText1.setTextColor(beanList.get(0).resColor);
                describeTwoLayout.setVisibility(View.GONE);
                divider.setVisibility(View.GONE);
                break;
            case 2:
                describeImage1.setImageResource(beanList.get(0).resDrawable);
                describeText1.setText(beanList.get(0).textValue);
                describeText1.setTextColor(beanList.get(0).resColor);
                describeImage2 = ViewUtil.getView(view, R.id.ch_task_image2);
                describeText2 = ViewUtil.getView(view, R.id.ch_task_text2);
                describeImage2.setImageResource(beanList.get(1).resDrawable);
                describeText2.setText(beanList.get(1).textValue);
                describeText2.setTextColor(beanList.get(1).resColor);
                break;
            case 3:
                describeImage1.setImageResource(beanList.get(0).resDrawable);
                describeText1.setText(beanList.get(0).textValue);
                describeText1.setTextColor(beanList.get(0).resColor);
                describeImage2 = ViewUtil.getView(view, R.id.ch_task_image2);
                describeText2 = ViewUtil.getView(view, R.id.ch_task_text2);
                describeImage2.setImageResource(beanList.get(1).resDrawable);
                describeText2.setText(beanList.get(1).textValue);
                describeText2.setTextColor(beanList.get(1).resColor);
                describeThreeLayout = ViewUtil.getView(view, R.id.ch_caohua_task_describe_three);
                taskLL2.setVisibility(View.VISIBLE);
                describeThreeLayout.setVisibility(View.VISIBLE);
                describeImage3 = ViewUtil.getView(view, R.id.ch_task_image3);
                describeText3 = ViewUtil.getView(view, R.id.ch_task_text3);
                describeImage3.setImageResource(beanList.get(2).resDrawable);
                describeText3.setText(beanList.get(2).textValue);
                describeText3.setTextColor(beanList.get(2).resColor);
                break;
            case 4:
                describeImage1.setImageResource(beanList.get(0).resDrawable);
                describeText1.setText(beanList.get(0).textValue);
                describeText1.setTextColor(beanList.get(0).resColor);
                describeImage2 = ViewUtil.getView(view, R.id.ch_task_image2);
                describeText2 = ViewUtil.getView(view, R.id.ch_task_text2);
                describeImage2.setImageResource(beanList.get(1).resDrawable);
                describeText2.setText(beanList.get(1).textValue);
                describeText2.setTextColor(beanList.get(1).resColor);

                describeThreeLayout = ViewUtil.getView(view, R.id.ch_caohua_task_describe_three);
                taskLL2.setVisibility(View.VISIBLE);
                describeThreeLayout.setVisibility(View.VISIBLE);
                describeImage3 = ViewUtil.getView(view, R.id.ch_task_image3);
                describeText3 = ViewUtil.getView(view, R.id.ch_task_text3);
                describeImage3.setImageResource(beanList.get(2).resDrawable);
                describeText3.setText(beanList.get(2).textValue);
                describeText3.setTextColor(beanList.get(2).resColor);

                describeFourLayout = ViewUtil.getView(view, R.id.ch_caohua_task_describe_four);
                describeFourLayout.setVisibility(View.VISIBLE);
                describeImage4 = ViewUtil.getView(view, R.id.ch_task_image4);
                describeText4 = ViewUtil.getView(view, R.id.ch_task_text4);
                describeImage4.setImageResource(beanList.get(3).resDrawable);
                describeText4.setText(beanList.get(3).textValue);
                describeText4.setTextColor(beanList.get(3).resColor);
                break;
        }

        btnComptele = ViewUtil.getView(view, R.id.ch_task_describe_dialog_alert_ok);
        btnKnow = ViewUtil.getView(view, R.id.ch_task_describe_dialog_alert_cancel);

        btnComptele.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                if (TextUtils.isEmpty(value)) {
                    return;
                }
                noComplete(method, value);
            }
        });
        btnKnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        divider2 = ViewUtil.getView(view, R.id.ch_dialog_phone_divider);
        if (!TextUtils.isEmpty(status)) {
            int statusInt = Integer.parseInt(status);
            if (statusInt == 4 || statusInt == 2) {
                divider2.setVisibility(View.GONE);
                btnComptele.setVisibility(View.GONE);
            }
        }
        if (TextUtils.isEmpty(method)) {
            divider2.setVisibility(View.GONE);
            btnComptele.setVisibility(View.GONE);
        }
    }

    private void noComplete(String method, String value) {
        FindContentActivity activity = (FindContentActivity) context;
        if (TextUtils.isEmpty(method)) {
            CHToast.show(context, "您未完成该任务！");
        } else {
            Intent intent = null;
            if (TaskFilter.METHOD_RESOURCE.equals(method)) { //跳源生
                switch (value) {
                    case TaskFilter.VALUE_RESOURCE_MY_GAME:
                        intent = new Intent(context, MineGameActivity.class);
                        context.startActivity(intent);
                        break;
                    case TaskFilter.VALUE_RESOURCE_RECHARGE:
                        intent = new Intent(context, PayActionActivity.class);
                        context.startActivity(intent);
                        break;
                    case TaskFilter.VALUE_RESOURCE_MINE_FRAGMENT: //我的头像 我的
                        SkipNavigationEntry entry = new SkipNavigationEntry();
                        entry.setTabValue(3);
                        EventBus.getDefault().post(entry);
                        activity.finish();
                        break;
                    case TaskFilter.VALUE_RESOURCE_MULTIPLEGAME:
                        RankingDetailActivity.start(context, DataMgr.DATA_TYPE_RANKING_SYNTH);
                        break;
                    case TaskFilter.VALUE_RESOURCE_ACCOUNT_SETTING: //账号设置
                        intent = new Intent(context, AccountSettingActivity.class);
                        context.startActivity(intent);
                        break;
                    case TaskFilter.VALUE_RESOURCE_SEARCH_ACTIVITY:
                        intent = new Intent(context, SearchActivity.class);
                        intent.putExtra("task_load", "load");
                        context.startActivity(intent);
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
                        WebActivity.startWebPageGetParam(context,TaskFilter.VALUE_URL_PHONE_BINDING_URL, null);
                        break;
                    case TaskFilter.VALUE_URL_REAL_NAME_REGISTRATION: //实名注册
                        WebActivity.startWebPageGetParam(context,TaskFilter.VALUE_URL_REAL_NAME_REGISTRATION_URL, null);
                        break;
                    default:
                        if (value.contains(TaskFilter.VALUE_URL_OPEN_TASK_DETAIL)) { //阅读攻略类
                            WebActivity.startWebPage(context, value);
                        } else if (value.contains(TaskFilter.VALUE_URL_GAME_DETAIL_URL)) {// 游戏详情页
                            WebActivity.startWebPage(context, value);
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

    public AlertDialog getDialog() {
        return dialog;
    }
}
