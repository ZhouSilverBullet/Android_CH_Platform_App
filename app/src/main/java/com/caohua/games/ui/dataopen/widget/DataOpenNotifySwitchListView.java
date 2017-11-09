package com.caohua.games.ui.dataopen.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.dataopen.entry.DataOpenSwitchEntry;
import com.caohua.games.biz.dataopen.SwitchStatusLogic;
import com.caohua.games.ui.adapter.ViewHolder;
import com.caohua.games.ui.widget.SwitchView;
import com.chsdk.biz.BaseLogic;
import com.chsdk.ui.widget.CHToast;

import java.util.List;

/**
 * Created by admin on 2017/9/19.
 */

public class DataOpenNotifySwitchListView extends DataOpenCommonListView<DataOpenSwitchEntry> {
    public DataOpenNotifySwitchListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        titleView.setIconAndMoreTextGone();
        titleView.getNameText().setText("");
    }

    public void setData(String managerGameId, List<DataOpenSwitchEntry> list, String titleName) {
        super.setData(managerGameId, list);
        titleView.getNameText().setText(titleName);
    }

    @Override
    protected int recyclerItemLayoutId() {
        return R.layout.ch_view_data_open_notify_switch_item_view;
    }

    @Override
    protected void onCommonCovert(ViewHolder holder, DataOpenSwitchEntry entry) {
        final SwitchView switchView = holder.getView(R.id.ch_data_open_notify_switch_view);
        final String index = entry.index;
        switchView.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(SwitchView view) {
                switchView.setOpened(true);
                doSwitchLogic("1", index);
            }

            @Override
            public void toggleToOff(SwitchView view) {
                switchView.setOpened(false);
                doSwitchLogic("0", index);
            }
        });
        String status = entry.status;
        if (!TextUtils.isEmpty(status)) {
            if (status.equals("0")) {
                switchView.setOpened(false);
            } else {
                switchView.setOpened(true);
            }
        } else {
            switchView.setOpened(false);
        }
        TextView switchText = holder.getView(R.id.ch_data_open_notify_switch_text);
        switchText.setText(entry.desc);
    }

    private void doSwitchLogic(final String status, String index) {
        new SwitchStatusLogic().switchStatus(managerGameId, index, status, new BaseLogic.DataLogicListner<String>() {
            @Override
            public void failed(String errorMsg, int errorCode) {
            }

            @Override
            public void success(String entryResult) {
                if (!TextUtils.isEmpty(status)) {
                    if (status.equals("0")) {
                        CHToast.show(context, "关闭成功");
                    } else {
                        CHToast.show(context, "开启成功");
                    }
                }
            }
        });
    }
}
