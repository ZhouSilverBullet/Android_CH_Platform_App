package com.caohua.games.ui.hot;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.caohua.games.R;
import com.chsdk.biz.app.AnalyticsHome;
import com.caohua.games.biz.hot.HotEntry;
import com.caohua.games.ui.widget.HomeSubTitleView;
import com.chsdk.configure.DataStorage;
import com.chsdk.utils.ViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * Created by CXK on 2016/10/18.
 */

public class HomeHotView extends LinearLayout {
    private static final int MAX_COUNT = 4;
    private HomeSubTitleView giftView;
    private GridView gridView;
    private List<HotEntry> data;
    private GridAdatper gridAdatper;
    private List<HotEntry> tampData;

    public HomeHotView(Context context) {
        super(context);
        loadXml();
    }

    public HomeHotView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadXml();
    }

    private void loadXml() {
        LayoutInflater.from(getContext()).inflate(R.layout.ch_view_home_hot, this, true);
        setVisibility(GONE);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    private void init() {
        giftView = (HomeSubTitleView) findViewById(R.id.ch_view_hot);
        giftView.setTopTitle("热门活动");
        HomeSubTitleView.OnMoreClickListener listener = new HomeSubTitleView.OnMoreClickListener() {
            @Override
            public void moreClick() {
                AnalyticsHome.umOnEvent(AnalyticsHome.HOME_HOT_ACTIVITY_ANALYTICS, "热门活动二级页面");
                HotActivity.start(getContext(), data);
            }
        };
        giftView.setOnMoreClickListener(listener);

        gridView = (GridView) findViewById(R.id.ch_view_hot_grid);
        gridView.setFocusable(false);
    }

    @Subscribe
    public void notifyHomeHotView(NotifyHotEntry hotEntry) {
        if (data != null && gridAdatper != null) {
            boolean hotGiftOnce = DataStorage.getHotGiftOnce(getContext());
            if (hotGiftOnce) {
                if (data.get(0).isSubHot()) {
                    data.remove(0);
                    gridAdatper.notifyDataSetChanged();
                }
            }
        }
    }

    public void unregisterEventBus() {
        EventBus.getDefault().unregister(this);
    }

    public void initData(List<HotEntry> list) {
        boolean hotGiftOnce = DataStorage.getHotGiftOnce(getContext());
        if (hotGiftOnce) {
            if (list.get(0).isSubHot()) {
                list.remove(0);
            }
        }
        if (gridAdatper != null) {
            synchronized (data) {
                data.clear();
                data.addAll(list);
                if (data.size() == 1) {
                    gridView.setNumColumns(1);
                } else {
                    gridView.setNumColumns(2);
                }
                gridAdatper.notifyDataSetChanged();
            }
        } else {
            data = list;
            if (data != null && data.size() > 0) {
                setVisibility(VISIBLE);
                if (data.size() == 1) {
                    gridView.setNumColumns(1);
                } else {
                    gridView.setNumColumns(2);
                }
                gridView.setAdapter(gridAdatper = new GridAdatper());
            }
        }
    }

    class GridAdatper extends BaseAdapter {

        @Override
        public int getCount() {
            synchronized (data) {
                return data.size() > MAX_COUNT ? MAX_COUNT : data.size();
            }
        }

        @Override
        public Object getItem(int position) {
            synchronized (data) {
                return data.get(position);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            boolean one = (getCount() == 1);
            if (convertView == null || one) {
                boolean isLeft = position % 2 == 0;
                if (isLeft && !one) {
                    convertView = new HomeHotItemView(getContext(), HomeHotItemView.TYPE_IS_LEFT);
                } else if (!isLeft && !one) {
                    convertView = new HomeHotItemView(getContext(), HomeHotItemView.TYPE_IS_RIGHT);
                } else {
                    convertView = new HomeHotItemView(getContext(), HomeHotItemView.TYPE_ONE_PAGER);
                }
            }
            HomeHotItemView itemView = (HomeHotItemView) convertView;
            itemView.setData((HotEntry) getItem(position));
            return convertView;
        }
    }
}
