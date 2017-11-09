package com.caohua.games.ui.dataopen;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.dataopen.DataOpenBindListLogic;
import com.caohua.games.biz.dataopen.DataOpenSwitchLogic;
import com.caohua.games.biz.dataopen.entry.DataOpenBindListEntry;
import com.caohua.games.biz.dataopen.entry.DataOpenSwitchEntry;
import com.caohua.games.ui.dataopen.widget.DataOpenBindGridView;
import com.caohua.games.ui.dataopen.widget.DataOpenNotifyRuleView;
import com.caohua.games.ui.dataopen.widget.DataOpenNotifySwitchListView;
import com.caohua.games.ui.vip.CommonActivity;
import com.chsdk.biz.BaseLogic;

import java.util.List;

/**
 * Created by admin on 2017/9/19.
 */

public class DataOpenNotifyActivity extends CommonActivity {
    private DataOpenNotifySwitchListView notifySwitch;
    private DataOpenBindGridView bindView;
    private DataOpenNotifyRuleView ruleView;
    private String managerGameId;
    private String titleName;

    @Override
    protected void initVariables() {
        Intent intent = getIntent();
        if (intent != null) {
            managerGameId = intent.getStringExtra(DataOpenActivity.MANAGER_GAME_ID);
            titleName = intent.getStringExtra("title_name");
        }
    }

    @Override
    protected String subTitle() {
        return "提醒管理";
    }

    @Override
    protected int childViewId() {
        return R.layout.ch_activity_data_open_notify;
    }

    @Override
    protected void initView() {
        notifySwitch = getView(R.id.ch_data_open_notify_switch);
        bindView = getView(R.id.ch_data_open_notify_bind);
        ruleView = getView(R.id.ch_data_open_notify_rule);
        bindView.setDataOpenBindListener(new DataOpenBindGridView.DataOpenBindListener() {
            @Override
            public void onDataOpenBind() {
                bindLogic();
            }
        });
    }

    @Override
    protected void loadData() {
        switchLogic();
        bindLogic();
    }

    private void switchLogic() {
        showLoadProgress(true);
        new DataOpenSwitchLogic().dataOpenSwitch(managerGameId, new BaseLogic.DataLogicListner<List<DataOpenSwitchEntry>>() {
            @Override
            public void failed(String errorMsg, int errorCode) {
                showLoadProgress(false);
                ruleView.setVisibility(View.VISIBLE);
                handleAllHaveData();
            }

            @Override
            public void success(List<DataOpenSwitchEntry> entryResult) {
                showLoadProgress(false);
                ruleView.setVisibility(View.VISIBLE);
                if (entryResult != null) {
                    notifySwitch.setData(managerGameId, entryResult, titleName);
                }
                handleAllHaveData();
            }
        });
    }

    private void bindLogic() {
        showLoadProgress(true);
        new DataOpenBindListLogic().openBindLogic(managerGameId, new BaseLogic.DataLogicListner<List<DataOpenBindListEntry>>() {
            @Override
            public void failed(String errorMsg, int errorCode) {
                showLoadProgress(false);
                handleAllHaveData();
            }

            @Override
            public void success(List<DataOpenBindListEntry> list) {
                showLoadProgress(false);
                if (list != null) {
                    bindView.setData(list, managerGameId);
                }
                handleAllHaveData();
            }
        });
    }

    @Override
    public void handleAllHaveData() {
        super.handleAllHaveData();
        if (bindView.existBack() || notifySwitch.existBack()) {
            ruleView.setVisibility(View.VISIBLE);
            showEmptyView(false);
            showNoNetworkView(false);
        } else {
            ruleView.setVisibility(View.GONE);
            if (!AppContext.getAppContext().isNetworkConnected()) {
                showNoNetworkView(true);
                return;
            }
            showEmptyView(true);
        }
    }

    public static void startOpen(Context context, String managerGameId, String titleName, Class clazz) {
        Intent intent = new Intent(context, clazz);
        intent.putExtra(DataOpenActivity.MANAGER_GAME_ID, managerGameId);
        intent.putExtra("title_name", titleName);
        context.startActivity(intent);
    }
}
