package com.caohua.games.ui.task;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.task.RewardItemBean;
import com.caohua.games.biz.task.TaskEntry;
import com.chsdk.utils.ViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhou on 2017/3/14.
 */

public class TaskCompleteDialog extends TaskDialog {
    private int growExp;
    private int points;
    private int money;
    private int silver;
    private View describeOneLayout;
    private View describeTwoLayout;
    private View describeThreeLayout;
    private View describeFourLayout;
    private ImageView describeImage1;
    private ImageView describeImage2;
    private ImageView describeImage3;
    private ImageView describeImage4;
    private TextView describeText1;
    private TextView describeText2;
    private TextView describeText3;
    private TextView describeText4;
    private View completeQuit;
    private View divider;
    private View taskLL2;
    private List<RewardItemBean> beanList;

    public TaskCompleteDialog(Context context, TaskEntry taskEntry) {
        super(context, taskEntry);
        points = stringToInt(taskEntry.getAward_points());
        money = stringToInt(taskEntry.getAward_money());
        silver = stringToInt(taskEntry.getAward_silver());
        growExp = stringToInt(taskEntry.getAward_growexp());
    }

    @Override
    public int getLayoutId() {
        return R.layout.ch_dialog_task_complete_app;
    }

    protected void initChildView(View view) {
        completeQuit = ViewUtil.getView(view, R.id.ch_task_complete_quit);
        completeQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

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
    }

    public AlertDialog getDialog() {
        return dialog;
    }
}
