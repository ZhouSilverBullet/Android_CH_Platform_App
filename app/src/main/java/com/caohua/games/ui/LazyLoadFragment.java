package com.caohua.games.ui;

/**
 * Created by Administrator on 2016/10/16.
 */

public abstract class LazyLoadFragment extends BaseFragment {
    private boolean initLoaded;

    protected void initLoad() {
    }

    public void onSelected() {
        if (!initLoaded) {
            initLoaded = true;
            super.initLoad();
        }
    }
}
