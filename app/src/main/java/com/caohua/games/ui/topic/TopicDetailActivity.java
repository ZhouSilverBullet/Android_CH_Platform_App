package com.caohua.games.ui.topic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.caohua.games.R;
import com.caohua.games.ui.BaseActivity;
import com.caohua.games.ui.BaseFragment;

/**
 * Created by ZengLei on 2016/11/10.
 */

public class TopicDetailActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch_activity_topic_detail);
        getSupportFragmentManager().beginTransaction().add(R.id.ch_activity_topic_fl, new TopicDetailFragment()).commit();
    }

    public static void start(Context context, String id) {
        Intent intent = new Intent(context, TopicDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(BaseFragment.KEY_INTENT_DATA, id);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}

