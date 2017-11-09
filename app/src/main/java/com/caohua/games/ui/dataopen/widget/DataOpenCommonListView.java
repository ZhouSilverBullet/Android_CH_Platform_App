package com.caohua.games.ui.dataopen.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.caohua.games.ui.dataopen.DataExistCallback;
import com.caohua.games.ui.vip.more.VipCommonListView;
import com.chsdk.model.BaseEntry;

import java.util.List;

/**
 * Created by admin on 2017/9/26.
 */

public abstract class DataOpenCommonListView<D extends BaseEntry> extends VipCommonListView<D> implements DataExistCallback {
    protected String managerGameId;

    public DataOpenCommonListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setData(String managerGameId, List<D> list) {
        super.setData(list);
        this.managerGameId = managerGameId;
    }

    @Override
    public boolean existBack() {
        if (adapter != null && adapter.getItemCount() > 0) {
            return true;
        }
        return false;
    }
}
