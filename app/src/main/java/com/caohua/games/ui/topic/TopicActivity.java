package com.caohua.games.ui.topic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.caohua.games.R;
import com.caohua.games.biz.topic.TopicEntry;
import com.caohua.games.ui.BaseActivity;
import com.caohua.games.ui.BaseFragment;

import java.io.Serializable;
import java.util.List;

/**
 * Created by CXK on 2016/10/26.
 */

public class TopicActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch_activity_topic);
    }

    public static void start(Context context, List<TopicEntry> list) {
        Intent intent = new Intent(context, TopicActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(BaseFragment.KEY_INTENT_DATA, (Serializable) list);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
