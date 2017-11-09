package com.caohua.games.ui.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.caohua.games.R;
import com.caohua.games.ui.BaseActivity;

/**
 * Created by ZengLei on 2017/5/24.
 */

public class MyFavoriteActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch_activity_myfavorite);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, MyFavoriteActivity.class);
        context.startActivity(intent);
    }
}
