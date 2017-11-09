package com.caohua.games.ui.openning;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.home.DataMgr;
import com.caohua.games.biz.openning.OpenningEntry;
import com.caohua.games.ui.LazyLoadFragment;
import com.caohua.games.ui.widget.PinnedHeaderExpandableListView;
import com.chsdk.api.AppOperator;
import com.chsdk.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CXK on 2016/10/14.
 */

public class OpenningFragment extends LazyLoadFragment implements
        ExpandableListView.OnChildClickListener,
        ExpandableListView.OnGroupClickListener,
        PinnedHeaderExpandableListView.OnHeaderUpdateListener {

    private PinnedHeaderExpandableListView expandableListView;
    private ArrayList<String> groupList;
    private ArrayList<List<OpenningEntry>> childList;
    private ExpandableListAdapter adapter;
    private List<OpenningItemView> mOpenningItemViews;

    @Override
    protected int getLayoutId() {
        return R.layout.ch_fragment_open;
    }

    @Override
    protected void initChildView() {
        expandableListView = findView(R.id.ch_home_open_list);
    }

    @Override
    protected List<LoadParams> getDataType() {
        List<LoadParams> types = new ArrayList<>();

        LoadParams params = new LoadParams();
        params.requestType = DataMgr.DATA_TYPE_OPENNING_TOTAL;
        types.add(params);
        return types;
    }

    @Override
    protected void handleData(LoadParams params, Object o) {
        if (params.requestType != DataMgr.DATA_TYPE_OPENNING_TOTAL) {
            return;
        }

        if (o instanceof List) {
            List<OpenningEntry> data = (List<OpenningEntry>) o;
            if (adapter == null) {
                groupList = new ArrayList<>();
                childList = new ArrayList<>();
                LogUtil.errorLog("OpenningFragment firstTime ");
                handleData(data);
                adapter = new ExpandableListAdapter(getActivity());
                expandableListView.setAdapter(adapter);
                expandableListView.setGroupIndicator(null);
                expandableListView.setOnHeaderUpdateListener(OpenningFragment.this);
                expandableListView.setOnChildClickListener(OpenningFragment.this);
                expandableListView.setOnGroupClickListener(OpenningFragment.this);
            } else {
                LogUtil.errorLog("OpenningFragment SecondTime ");
                groupList.clear();
                childList.clear();
                handleData(data);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void handleData(final List<OpenningEntry> data, final Runnable runnable) {
        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                handleData(data);
                if (getActivity() != null && !getActivity().isFinishing()) {
                    getActivity().runOnUiThread(runnable);
                }
            }
        });
    }

    private void handleData(List<OpenningEntry> data) {
        ArrayList<OpenningEntry> childTemp = null;
        for (int i = 0; i < data.size(); i++) {
            OpenningEntry entry = data.get(i);
            String day = entry.getDay();
            String hour = entry.getHour();
            if (!groupList.contains(day)) {
                groupList.add(day);

                childTemp = new ArrayList<>();
                childTemp.add(entry);

                if (childTemp != null) {
                    childList.add(childTemp);
                }
            } else {
                if (childTemp != null) {
                    childTemp.add(entry);
                }
            }
        }
    }

    class ExpandableListAdapter extends BaseExpandableListAdapter {
        private Context context;

        public ExpandableListAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return childList.get(groupPosition).size();
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return childList.get(groupPosition).get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public Object getGroup(int groupPosition) {
            try {
                return groupList.get(groupPosition);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public int getGroupCount() {
            return groupList.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }


        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView textView;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().from(context).inflate(R.layout.ch_fragment_expandable_tag, null);
                textView = (TextView) convertView
                        .findViewById(R.id.group);
                convertView.setTag(textView);
            } else {
                textView = (TextView) convertView.getTag();
            }
            textView.setText(getGroup(groupPosition).toString());
            expandableListView = (PinnedHeaderExpandableListView) parent;
            if (!expandableListView.isGroupExpanded(groupPosition)) {
                expandableListView.expandGroup(groupPosition);
            }
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (!(convertView instanceof OpenningItemView)) {
                if (mOpenningItemViews == null) {
                    mOpenningItemViews = new ArrayList<>();
                }
                convertView = new OpenningItemView(getActivity());
                mOpenningItemViews.add((OpenningItemView) convertView);
            }
            ((OpenningItemView) convertView).setData((OpenningEntry) getChild(groupPosition, childPosition));
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    @Override
    public View getPinnedHeader() {
        if (getActivity() == null || getActivity().isFinishing()) {
            return null;
        }
        
        View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.ch_fragment_expandable_tag, null);
        headerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return headerView;
    }

    private TextView pinnedHeaderText;
    @Override
    public void updatePinnedHeader(View headerView, int firstVisibleGroupPos) {
        if (headerView == null)
            return;

        if (pinnedHeaderText == null) {
            pinnedHeaderText = (TextView) headerView.findViewById(R.id.group);
        }
        String firstVisibleGroup = (String) adapter.getGroup(firstVisibleGroupPos);
        pinnedHeaderText.setText(firstVisibleGroup);
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        return false;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        return false;
    }

    @Override
    public void onDestroy() {
        if (mOpenningItemViews != null) {
            for (OpenningItemView openningItemView : mOpenningItemViews) {
                if (openningItemView != null) {
                    openningItemView.onDestroy();
                }
            }
        }
        super.onDestroy();
    }
}
