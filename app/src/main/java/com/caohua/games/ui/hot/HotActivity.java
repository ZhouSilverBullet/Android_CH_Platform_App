package com.caohua.games.ui.hot;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.caohua.games.R;
import com.caohua.games.biz.hot.HotEntry;
import com.caohua.games.ui.BaseActivity;
import com.caohua.games.ui.BaseFragment;
import com.caohua.games.ui.widget.SubActivityTitleView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by CXK on 2016/10/19.
 */

public class HotActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch_activity_hot);
        initView();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        openWelcomeActivity(this, 0);
    }

    private void initView() {
        ((SubActivityTitleView) findViewById(R.id.ch_activity_hot_title)).getBackImage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWelcomeActivity(v.getContext(), 0);
                finish();
            }
        });
    }

    public static void start(Context context, List<HotEntry> list) {
//        List<HotEntry> tempList = new ArrayList<HotEntry>();
//        for (HotEntry hotEntry : list) {
//            if (hotEntry.getType() != 2) {
//                tempList.add(hotEntry);
//            }
//        }
        Intent intent = new Intent(context, HotActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(BaseFragment.KEY_INTENT_DATA, (Serializable) list);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
