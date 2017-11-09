package com.caohua.games.ui.vip.more;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.caohua.games.R;
import com.caohua.games.ui.adapter.CommonRecyclerViewAdapter;
import com.caohua.games.ui.adapter.ItemDivider;
import com.caohua.games.ui.adapter.ViewHolder;
import com.caohua.games.ui.vip.VipCertificationActivity;
import com.caohua.games.ui.vip.widget.VipTitleView;
import com.chsdk.biz.BaseLogic;
import com.chsdk.model.BaseEntry;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.CHAlertDialog;
import com.chsdk.utils.ViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/8/22.
 */

public abstract class VipCommonListView<D extends BaseEntry> extends LinearLayout {

    protected Context context;
    protected VipTitleView titleView;
    protected RecyclerView recyclerView;
    protected VipCommonAdapter adapter;
    private boolean isIconGone;
    private String moreValue;
    private String nameValue;
    private View divider2;
    protected boolean isAuth;
    protected boolean isVip;

    public VipCommonListView(Context context) {
        this(context, null);
    }

    public VipCommonListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VipCommonListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.VipCommonListView, defStyleAttr, 0);
        isIconGone = a.getBoolean(R.styleable.VipCommonListView_isIconGone, false);
        moreValue = a.getString(R.styleable.VipCommonListView_moreValue);
        nameValue = a.getString(R.styleable.VipCommonListView_nameValue);
        a.recycle();
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        setVisibility(GONE);
        LayoutInflater.from(context).inflate(R.layout.ch_vip_common_list_view, this, true);
        titleView = (VipTitleView) findViewById(R.id.ch_vip_common_title_view);
        if (isIconGone) {
            titleView.setIconAndMoreTextGone();
            titleView.getNameText().setText(nameValue);
        } else {
            titleView.getMoreText().setText(moreValue);
            titleView.getNameText().setText(nameValue);
        }
        recyclerView = (RecyclerView) findViewById(R.id.ch_vip_common_recycler_view);
        recyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new ItemDivider().setDividerWidth(ViewUtil.dp2px(context, 1)).setDividerColor(getResources().getColor(R.color.ch_gray)));
        adapter = new VipCommonAdapter(context, new ArrayList<D>(), recyclerItemLayoutId());
        recyclerView.setAdapter(adapter);
        divider2 = findViewById(R.id.ch_vip_common_item_divider2);
    }

    public View getDivider2() {
        return divider2;
    }

    public void setData(List<D> list, boolean isAuth, boolean isVip) {
        if (list == null || list.size() == 0) {
            setVisibility(GONE);
            return;
        }
        this.isAuth = isAuth;
        this.isVip = isVip;
        setVisibility(VISIBLE);
        adapter.addAll(list);
    }

    public void setData(List<D> list) {
        if (list == null || list.size() == 0) {
            setVisibility(GONE);
            return;
        }
        setVisibility(VISIBLE);
        adapter.addAll(list);
    }

    public class VipCommonAdapter extends CommonRecyclerViewAdapter<D> {

        public VipCommonAdapter(Context context, List<D> list, int layoutId) {
            super(context, list, layoutId);
        }

        @Override
        protected void covert(ViewHolder holder, D d) {
            onCommonCovert(holder, d);
        }
    }

    protected boolean isVipOrAuth() {
        if (!isVip) {
            final CHAlertDialog vipDialog = new CHAlertDialog((Activity) context, true, true);
            vipDialog.show();
            vipDialog.setContent("还未达到VIP条件，去看看怎么成为草花VIP吧");
            vipDialog.setCancelButton("取消", new OnClickListener() {
                @Override
                public void onClick(View v) {
                    vipDialog.dismiss();
                }
            });
            vipDialog.setOkButton("去查看", new OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebActivity.startAppLink(context, BaseLogic.HOST_APP + "vip/expLogView");
                    vipDialog.dismiss();
                }
            });
            return false;
        }

        if (!isAuth) {
            final CHAlertDialog vipDialog = new CHAlertDialog((Activity) context, true, true);
            vipDialog.show();
            vipDialog.setContent("还未进行VIP认证，请先前往认证");
            vipDialog.setCancelButton("取消", new OnClickListener() {
                @Override
                public void onClick(View v) {
                    vipDialog.dismiss();
                }
            });
            vipDialog.setOkButton("去认证", new OnClickListener() {
                @Override
                public void onClick(View v) {
                    VipCertificationActivity.start(context);
                    vipDialog.dismiss();
                }
            });
            return false;
        }
        return true;
    }


    protected abstract int recyclerItemLayoutId();

    protected abstract void onCommonCovert(ViewHolder holder, D d);

}
