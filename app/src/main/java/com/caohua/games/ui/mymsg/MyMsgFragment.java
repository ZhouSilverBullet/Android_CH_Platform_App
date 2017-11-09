package com.caohua.games.ui.mymsg;

import android.view.View;

import com.caohua.games.R;
import com.caohua.games.biz.home.DataMgr;
import com.caohua.games.biz.mymsg.MyMsgEntry;
import com.caohua.games.biz.topic.TopicEntry;
import com.caohua.games.ui.BasePageListFragment;
import com.caohua.games.ui.topic.TopicItemView;

/**
 * Created by Zenglei on 2017/05/23.
 */

public class MyMsgFragment extends BasePageListFragment<MyMsgEntry, MyMsgItemView> {

    @Override
    protected int getLayoutId() {
        return R.layout.ch_fragment_my_msg;
    }

    protected int getType() {
        return DataMgr.DATA_TYPE_MY_MSG;
    }

    protected int getChildViewId() {
        return R.id.ch_activity_my_msg_list;
    }

    protected View getItemView() {
        return new MyMsgItemView(getActivity());
    }

    @Override
    protected boolean hasLogin() {
        return true;
    }
}
