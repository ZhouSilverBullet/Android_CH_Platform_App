package com.caohua.games.ui.account;

import android.view.View;

import com.caohua.games.R;
import com.caohua.games.biz.DataInterface;
import com.caohua.games.biz.account.MyFavoriteEntry;
import com.caohua.games.biz.home.DataMgr;
import com.caohua.games.biz.mymsg.MyMsgEntry;
import com.caohua.games.ui.BasePageListFragment;
import com.caohua.games.ui.mymsg.MyMsgItemView;

import java.util.List;

/**
 * Created by Zenglei on 2017/05/23.
 */

public class MyFavoriteFragment extends BasePageListFragment<MyFavoriteEntry, MyFavoriteItemView> {

    @Override
    protected int getLayoutId() {
        return R.layout.ch_fragment_my_favorite;
    }

    protected int getType() {
        return DataMgr.DATA_TYPE_MY_FAVORITE;
    }

    protected int getChildViewId() {
        return R.id.ch_activity_topic_list;
    }

    protected View getItemView() {
        return new MyFavoriteItemView(getActivity(), new DataInterface() {

            @Override
            public void setData(Object o) {
                MyFavoriteFragment.this.data.remove(o);
                MyFavoriteFragment.this.listAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected boolean hasLogin() {
        return true;
    }
}
