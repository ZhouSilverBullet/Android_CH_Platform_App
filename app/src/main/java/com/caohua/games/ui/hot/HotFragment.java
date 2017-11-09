package com.caohua.games.ui.hot;

import android.view.View;

import com.caohua.games.R;
import com.caohua.games.biz.home.DataMgr;
import com.caohua.games.biz.hot.HotEntry;
import com.caohua.games.ui.BasePageListFragment;

import java.util.List;

/**
 * Created by ZengLei on 2016/11/3.
 */

public class HotFragment extends BasePageListFragment<HotEntry, HotItemView> {

    @Override
    protected void initLoad() {
        showLoadProgress(true);
        super.initLoad();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ch_fragement_hot;
    }

    protected int getType() {
        return DataMgr.DATA_TYPE_HOT;
    }

    protected int getChildViewId() {
        return R.id.ch_activity_hot_list;
    }

    protected View getItemView() {
        return new HotItemView(getActivity());
    }

    protected void handleData(List<HotEntry> data) {
        if (data != null && data.size() > 0) {
            if (data.get(0).isSubHot()) {
                data.remove(0);
            }
//            for (HotEntry hotEntry : data) {
//                if (hotEntry.getType() != 2) {
//                    data.add(hotEntry);
//                }
//            }
        }
    }
}
