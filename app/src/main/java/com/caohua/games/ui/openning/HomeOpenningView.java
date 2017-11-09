package com.caohua.games.ui.openning;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;

import com.caohua.games.biz.openning.OpenningEntry;
import com.caohua.games.ui.BaseHomeListView;
import com.caohua.games.ui.widget.HomeSubTitleView;

/**
 * Created by CXK on 2016/10/18.
 */

public class HomeOpenningView extends BaseHomeListView<OpenningEntry, HomeOpenningItemView> {
    public HomeOpenningView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HomeOpenningView(Context context) {
        super(context);
    }

    @Override
    protected int getMaxCount() {
        return 4;
    }

    @Override
    protected View getItemView() {
        return new HomeOpenningItemView(getContext());
    }

    @Override
    protected String getTitle() {
        return "开服表";
    }

    public void setOnMoreClickListener(HomeSubTitleView.OnMoreClickListener listener) {
        if (listener != null) {
            subTitleView.setOnMoreClickListener(listener);
        }
    }

    public void onDestroy() {
        SparseArray<HomeOpenningItemView> cacheView = getCacheViews();
        if (cacheView == null)
            return;

        for (int i = 0; i < cacheView.size(); i++) {
            cacheView.get(i).onDestory();
        }
    }
}
