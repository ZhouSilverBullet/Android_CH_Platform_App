package com.caohua.games.ui.mymsg;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.caohua.games.R;
import com.caohua.games.ui.BaseActivity;

/**
 * Created by ZengLei on 2017/5/23.
 */

public class MyMsgActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch_activity_mymsg);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, MyMsgActivity.class);
        context.startActivity(intent);
    }
}
