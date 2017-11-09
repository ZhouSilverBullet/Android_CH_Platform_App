package com.caohua.games.ui.vip;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.vip.VipActSecondEntry;
import com.caohua.games.biz.vip.VipActSecondLogic;
import com.caohua.games.ui.CreditActivity;
import com.caohua.games.ui.widget.BottomLoadListView;
import com.chsdk.biz.BaseLogic;
import com.chsdk.http.HttpConsts;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.utils.PicUtil;

import java.util.Collection;
import java.util.List;

/**
 * Created by admin on 2017/8/26.
 */

public class VipActSecondActivity extends CommonActivity {
    private ListView listView;
    private ListAdapter adapter;

    @Override
    protected String subTitle() {
        return "专属活动";
    }

    @Override
    protected int childViewId() {
        return R.layout.ch_activity_vip_act_second;
    }

    protected void loadData() {
        showLoadProgress(true);
        new VipActSecondLogic().getActSecond(new BaseLogic.DataLogicListner<VipActSecondEntry>() {
            @Override
            public void failed(String errorMsg, int errorCode) {
                if (isFinishing()) {
                    return;
                }
                CHToast.show(activity, errorMsg);
                showLoadProgress(false);
                if (HttpConsts.ERROR_NO_NETWORK.equals(errorMsg)) {
                    showNoNetworkView(true);
                    return;
                }
                showEmptyView(true);
            }

            @Override
            public void success(VipActSecondEntry entry) {
                showNoNetworkView(false);
                if (isFinishing()) {
                    return;
                }
                if (entry != null) {
                    if (adapter == null) {
                        adapter = new ListAdapter(entry.getData(), activity);
                        listView.setAdapter(adapter);
                    } else {
                        adapter.addAll(entry.getData());
                    }
                }
                showLoadProgress(false);
                if (adapter.getCount() > 0) {
                    showEmptyView(false);
                } else {
                    showEmptyView(true);
                }
            }
        });

    }

    protected void initView() {
        listView = getView(R.id.ch_activity_vip_act_second_list);
    }

    private class ListAdapter extends BaseAdapter {

        private List<VipActSecondEntry.DataBean> list;
        private Context context;
        private LayoutInflater inflater;

        public ListAdapter(List<VipActSecondEntry.DataBean> list, Context context) {
            this.list = list;
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void addAll(Collection<VipActSecondEntry.DataBean> collection) {
            if (list != null) {
                list.addAll(collection);
                notifyDataSetChanged();
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.ch_vip_act_second_recycler_item, null);
                viewHolder.img = (ImageView) convertView.findViewById(R.id.ch_act_second_recycler_item_img);
                viewHolder.title = (TextView) convertView.findViewById(R.id.ch_act_second_recycler_item_title);
                viewHolder.des = (TextView) convertView.findViewById(R.id.ch_act_second_recycler_item_des);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            VipActSecondEntry.DataBean entry = list.get(position);
            final String url = entry.getUrl();
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(url)) {
                        WebActivity.startWebPage(context, url);
                    }
                }
            });
            if (!TextUtils.isEmpty(entry.getImage())) {
                PicUtil.displayImg(context, viewHolder.img, entry.getImage(), R.drawable.ch_default_pic);
            }
            viewHolder.title.setText(entry.getName());
            viewHolder.des.setText(entry.getMemo());
            return convertView;
        }

        class ViewHolder {
            ImageView img;
            TextView title;
            TextView des;
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, VipActSecondActivity.class);
        context.startActivity(intent);
    }
}
