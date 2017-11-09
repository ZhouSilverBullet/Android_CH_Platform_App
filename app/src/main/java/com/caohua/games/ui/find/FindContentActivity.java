package com.caohua.games.ui.find;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.caohua.games.R;
import com.caohua.games.ui.BaseActivity;
import com.caohua.games.ui.fragment.StoreFragment;
import com.caohua.games.ui.fragment.TaskFragment;

/**
 * Created by zhouzhou on 2017/5/12.
 */

public class FindContentActivity extends BaseActivity {
    public static final String KEY = "find_content";
    public static final int KEY_SHOP = 100;
    public static final int KEY_TASK = 101;
    private StoreFragment storeFragment;
    private TaskFragment taskFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch_activity_find_content);
        Intent intent = getIntent();
        if (intent != null) {
            Uri data = intent.getData();
            if (data != null) {
                doUri(data);
            } else {
                int intExtra = intent.getIntExtra(KEY, -1);
                initView(intExtra);
            }
        }
    }

    private void doUri(Uri data) {
        String scheme_key = data.getQueryParameter("ch_key");
        switch (scheme_key) {
            case "1":
                if (storeFragment == null) {
                    storeFragment = new StoreFragment();
                }
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.ch_activity_find_content, storeFragment).commit();
                break;
            case "2":
                if (taskFragment == null) {
                    taskFragment = new TaskFragment();
                }
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.ch_activity_find_content, taskFragment).commit();
                break;
            default:
                openWelcomeActivity(this, 2);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        openWelcomeActivity(this, 2);
    }

    private void initView(int extra) {
        switch (extra) {
            case KEY_SHOP:
                if (storeFragment == null) {
                    storeFragment = new StoreFragment();
                }
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.ch_activity_find_content, storeFragment).commit();
                break;
            case KEY_TASK:
                if (taskFragment == null) {
                    taskFragment = new TaskFragment();
                }
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.ch_activity_find_content, taskFragment).commit();
                break;
        }
    }
}
