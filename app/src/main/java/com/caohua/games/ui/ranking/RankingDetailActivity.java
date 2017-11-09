package com.caohua.games.ui.ranking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.caohua.games.R;
import com.caohua.games.biz.home.DataMgr;
import com.caohua.games.ui.BaseActivity;
import com.caohua.games.ui.BaseFragment;
import com.caohua.games.ui.widget.SubActivityTitleView;

/**
 * Created by CXK on 2016/10/17.
 */

public class RankingDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch_activity_ranking_detail);
        setTitle();
    }

    private void setTitle() {
        if (getIntent().getExtras() != null) {
            int index = getIntent().getExtras().getInt(BaseFragment.KEY_INTENT_DATA);
            SubActivityTitleView titleView = (SubActivityTitleView) findViewById(R.id.ch_activity_ranking_detail_title);
            if (index == DataMgr.DATA_TYPE_RANKING_SYNTH) {
                titleView.setTitle("综合排行榜");
            } else if (index == DataMgr.DATA_TYPE_RANKING_NEW_GAME) {
                titleView.setTitle("新游排行榜");
            } else if (index == DataMgr.DATA_TYPE_RANKING_RECOMM) {
                titleView.setTitle("推荐排行榜");
            } else if (index == DataMgr.DATA_TYPE_RANKING_NEW) {
                titleView.setTitle("最新上架");
            }
        }
    }

    public static void start(Context context, int type) {
        Intent intent = new Intent(context, RankingDetailActivity.class);
        intent.putExtra(BaseFragment.KEY_INTENT_DATA, type);
        context.startActivity(intent);
    }
}
