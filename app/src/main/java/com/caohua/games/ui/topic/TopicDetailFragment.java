package com.caohua.games.ui.topic;

import android.view.View;

import com.caohua.games.R;
import com.caohua.games.biz.home.DataMgr;
import com.caohua.games.biz.topic.TopicDetailEntry;
import com.caohua.games.ui.BasePageListFragment;

import java.util.List;

/**
 * Created by CXK on 2016/11/3.
 */

public class TopicDetailFragment extends BasePageListFragment<TopicDetailEntry, TopicDetailItemView> {

    @Override
    protected void initLoad() {
        showLoadProgress(true);
        loadData(true);
    }

    @Override
    protected LoadPageParams getLoadPageParams(int type) {
        LoadPageParams pageParams = super.getLoadPageParams(type);
        pageParams.object = getIntentData();
        return pageParams;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ch_fragment_topic;
    }

    protected int getType() {
        return DataMgr.DATA_TYPE_TOPIC_DETAIL;
    }

    protected int getChildViewId() {
        return R.id.ch_activity_topic_list;
    }

    protected View getItemView() {
        return new TopicDetailItemView(getActivity());
    }

    @Override
    protected void handleData(List<TopicDetailEntry> data) {
        super.handleData(data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (listView != null) {
            for (int i = 0; i < listView.getCount(); i++ ) {
                if (listView.getChildAt(i) instanceof TopicDetailItemView) {
                    ((TopicDetailItemView) listView.getChildAt(i)).release();
                }
            }
        }
    }
}
