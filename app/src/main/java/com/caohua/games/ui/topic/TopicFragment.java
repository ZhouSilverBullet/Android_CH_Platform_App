package com.caohua.games.ui.topic;

import android.view.View;

import com.caohua.games.R;
import com.caohua.games.biz.home.DataMgr;
import com.caohua.games.biz.topic.TopicEntry;
import com.caohua.games.ui.BasePageListFragment;

/**
 * Created by CXK on 2016/11/3.
 */

public class TopicFragment extends BasePageListFragment<TopicEntry, TopicItemView> {

    @Override
    protected int getLayoutId() {
        return R.layout.ch_fragment_topic;
    }

    protected int getType() {
        return DataMgr.DATA_TYPE_TOPIC;
    }

    protected int getChildViewId() {
        return R.id.ch_activity_topic_list;
    }

    protected View getItemView() {
        return new TopicItemView(getActivity());
    }
}
