package com.caohua.games.ui.giftcenter;

import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.gift.GiftCenterSaveEntry;
import com.caohua.games.biz.gift.GiftCenterSaveLogic;
import com.caohua.games.ui.adapter.CommonRecyclerViewAdapter;
import com.caohua.games.ui.adapter.ViewHolder;
import com.chsdk.biz.BaseLogic;
import com.chsdk.http.HttpConsts;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.utils.PicUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;

/**
 * Created by admin on 2017/10/26.
 */

public class GiftCenterSaveFragment extends GiftCenterListFragment {
    private SaveAdapter adapter;

    @Override
    protected void loadMore(RefreshLayout refreshlayout) {
        onLogic(adapter.getItemCount(), false);
    }

    @Override
    protected void initChildView() {
        super.initChildView();
        if (getActivity() instanceof GiftCenterActivity) {
            if (((GiftCenterActivity) getActivity()).getTabValue() == 2) {
                refreshLayout.autoRefresh();
            }
        }
    }

    /**
     * 需要登录
     * @return
     */
    @Override
    protected boolean hasLogin() {
        return true;
    }

    protected void onLogic(int pageCount, final boolean isRefreshLayout) {
        super.onLogic(pageCount, isRefreshLayout);
        new GiftCenterSaveLogic().centerSave(pageCount, new BaseLogic.DataLogicListner<List<GiftCenterSaveEntry>>() {
            @Override
            public void failed(String errorMsg, int errorCode) {
                if (refreshLayout != null) {
                    if (isRefreshLayout) {
                        refreshLayout.finishRefresh();
                    } else {
                        refreshLayout.finishLoadmore();
                    }
                }
                if (!HttpConsts.ERROR_CODE_PARAMS_VALID.equals(errorMsg)) {
                    if (!TextUtils.isEmpty(errorMsg) && errorMsg.contains("参数错误")) {
                        //不弹提示
                    } else {
                        CHToast.show(activity, errorMsg);
                    }
                }
                if (adapter != null && adapter.getItemCount() > 0) {
                    showEmptyView(false);
                    showNoNetworkView(false);
                    refreshLayout.setLoadmoreFinished(true);
                    return;
                }
                if (HttpConsts.ERROR_NO_NETWORK.equals(errorMsg)) {
                    showNoNetworkView(true);
                    return;
                }
                showEmptyView(true);
            }

            @Override
            public void success(List<GiftCenterSaveEntry> entryList) {
                if (refreshLayout != null) {
                    if (isRefreshLayout) {
                        refreshLayout.finishRefresh();
                    } else {
                        refreshLayout.finishLoadmore(0);
                    }
                }
                if (entryList != null && entryList.size() > 0) {
                    showEmptyView(false);
                    if (adapter == null) {
                        adapter = new SaveAdapter(activity, entryList, R.layout.ch_gift_center_save_item);
                        recyclerView.setAdapter(adapter);
                    } else {
                        if (isRefreshLayout) {
                            adapter.addAll(entryList);
                        } else {
                            adapter.addAllNotClear(entryList);
                        }
                    }
                } else {
                    if (adapter != null && adapter.getItemCount() > 0) {
                        return;
                    }
                    showEmptyView(true);
                }
            }
        });
    }

    private class SaveAdapter extends CommonRecyclerViewAdapter<GiftCenterSaveEntry> {

        public SaveAdapter(Context context, List<GiftCenterSaveEntry> list, int layoutIds) {
            super(context, list, layoutIds);
        }

        @Override
        protected void covert(ViewHolder holder, GiftCenterSaveEntry entry) {
            ImageView image = holder.getView(R.id.ch_gift_center_save_item_image);
            TextView title = holder.getView(R.id.ch_gift_center_save_item_title);
            TextView type = holder.getView(R.id.ch_gift_center_save_item_type);
            TextView value = holder.getView(R.id.ch_gift_center_save_item_value);
            TextView btn = holder.getView(R.id.ch_gift_center_save_item_btn);
            TextView time = holder.getView(R.id.ch_gift_center_save_item_time);

            PicUtil.displayImg(context, image, entry.game_icon, R.drawable.ch_default_apk_icon);
            title.setText(entry.gift_name);
            type.setText(entry.game_name);
            final String cardno = entry.cardno;
            value.setText("礼包码：" + cardno);
            time.setText("有效期：" + entry.expire_time);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    ClipboardManager copyManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    copyManager.setText(cardno);
                    CHToast.show(context, "复制成功");
                }
            });
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }
    }
}
