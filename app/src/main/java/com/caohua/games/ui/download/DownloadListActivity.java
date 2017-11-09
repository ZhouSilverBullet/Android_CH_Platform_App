package com.caohua.games.ui.download;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.ui.BaseActivity;
import com.chsdk.utils.ApkUtil;

import org.greenrobot.eventbus.EventBus;
import org.wlf.filedownloader.DownloadFileInfo;
import org.wlf.filedownloader.FileDownloader;
import org.wlf.filedownloader.base.Status;
import org.wlf.filedownloader.listener.OnDownloadFileChangeListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZengLei on 2016/10/18.
 */

public class DownloadListActivity extends BaseActivity {
    private ExpandableListView expandableListView;
    private FileChangeListener downloadListener;
    private DownloadAdapter downloadAdapter;

    private List<DownloadFileInfo> downloadingList;
    private List<DownloadFileInfo> completedList;
    private List<DownloadItemView> mDownloadItemViews;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDownloadItemViews != null) {
            for (DownloadItemView downloadItemView : mDownloadItemViews) {
                downloadItemView.unRegister("");
                if (EventBus.getDefault().isRegistered(downloadItemView)) {
                    EventBus.getDefault().unregister(downloadItemView);
                }
            }
            mDownloadItemViews.clear();
        }
        if (downloadListener != null)
            FileDownloader.unregisterDownloadFileChangeListener(downloadListener);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.ch_activity_download);
//        mDownloadFileInfos = Collections.synchronizedList(new ArrayList<DownloadFileInfo>());
        expandableListView = getView(R.id.ch_download_expand_list);
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                return true;
            }
        });
        expandableListView.setEmptyView(getView(R.id.ch_download_empty));
        mDownloadItemViews = new ArrayList<>();

        refresh();
        if (downloadListener == null) {
            downloadListener = new FileChangeListener();
            FileDownloader.registerDownloadFileChangeListener(downloadListener);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void refresh() {
        handleData();
        downloadAdapter = new DownloadAdapter();
        expandableListView.setAdapter(downloadAdapter);
        expandAll();
    }

    private void expandAll() {
        if (hasCompletedItem()|| hasDownloadingItem()) {
            expandableListView.expandGroup(0);
        }

        if (hasCompletedItem() && hasDownloadingItem()) {
            expandableListView.expandGroup(1);
        }
    }

    private void handleData() {
        List<DownloadFileInfo> list = FileDownloader.getDownloadFiles();
        if (list != null && list.size() > 0) {
            for (DownloadFileInfo info : list) {
                int status = info.getStatus();
                if (status == Status.DOWNLOAD_STATUS_COMPLETED || isAppInstalled(info.getPkg())) {
                    addCompletedList(info);
                } else {
                    addDownloadingList(info);
                }
            }
        }
    }

    private boolean isAppInstalled(String pkg) {
        return ApkUtil.checkAppInstalled(AppContext.getAppContext(), pkg);
    }

    private void addDownloadingList(DownloadFileInfo info) {
        if (downloadingList == null) {
            downloadingList = new ArrayList<>();
        } else if (downloadingList.contains(info)) {
            downloadingList.remove(info);
        }
        downloadingList.add(info);
    }

    private void addCompletedList(DownloadFileInfo info) {
        if (completedList == null) {
            completedList = new ArrayList<>();
        } else if (completedList.contains(info)) {
            completedList.remove(info);
        }
        completedList.add(info);
    }

    private boolean removeCompletedList(DownloadFileInfo info) {
        if (completedList == null)
            return false;

        return completedList.remove(info);
    }

    private boolean removeDownloadingList(DownloadFileInfo info) {
        if (downloadingList == null)
            return false;

        return downloadingList.remove(info);
    }

    private boolean hasCompletedItem() {
        return completedList != null && completedList.size() > 0;
    }

    private boolean hasDownloadingItem() {
        return downloadingList != null && downloadingList.size() > 0;
    }

    private boolean isInCompletedList(DownloadFileInfo info) {
        return completedList != null && completedList.contains(info);
    }

    private void deleteInfo(DownloadFileInfo info) {
        if (info == null)
            return;

        if ((info.getStatus() == Status.DOWNLOAD_STATUS_COMPLETED || isAppInstalled(info.getPkg()))
                && isInCompletedList(info)) {
            removeCompletedList(info);
        } else {
            removeDownloadingList(info);
        }
        adapterNotify();
    }

    private void adapterNotify() {
        downloadAdapter.notifyDataSetChanged();
        expandAll();
    }

    class FileChangeListener implements OnDownloadFileChangeListener {

        @Override
        public void onDownloadFileCreated(DownloadFileInfo downloadFileInfo) {
            removeCompletedList(downloadFileInfo);
            addDownloadingList(downloadFileInfo);
            adapterNotify();
        }

        @Override
        public void onDownloadFileUpdated(DownloadFileInfo downloadFileInfo, Type type) {
//            for (int i = 0; i < mDownloadFileInfos.size(); i++) {
//                DownloadFileInfo info = mDownloadFileInfos.get(i);
//                if (info.getUrl() == downloadFileInfo.getUrl()) {
//                    mDownloadFileInfos.remove(i);
//                    mDownloadFileInfos.add(i, downloadFileInfo);
//                    downloadAdapter.notifyDataSetChanged();
//                    return;
//                }
//            }
        }

        @Override
        public void onDownloadFileDeleted(DownloadFileInfo downloadFileInfo) {
//            deleteInfo(downloadFileInfo);
        }
    }

    class DownloadAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return (hasCompletedItem() ? 1 : 0) + (hasDownloadingItem() ? 1 : 0);
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            if (groupPosition == 0 && hasDownloadingItem()) {
                return downloadingList == null ? 0 : downloadingList.size();
            }
            return completedList == null ? 0 : completedList.size();
        }

        @Override
        public String getGroup(int groupPosition) {
            if (groupPosition == 0 && hasDownloadingItem()) {
                return  "正在下载";
            }
            return "已下载";
        }

        @Override
        public DownloadFileInfo getChild(int groupPosition, int childPosition) {
            if (groupPosition == 0 && hasDownloadingItem()) {
                return downloadingList.get(childPosition);
            }
            return completedList.get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.ch_view_download_group, null);
            }
            TextView tv = (TextView) convertView.findViewById(R.id.ch_view_download_group_tv);
            tv.setText(getGroup(groupPosition));

            if (groupPosition == 1) {
                convertView.findViewById(R.id.ch_view_download_group_divider).setVisibility(View.VISIBLE);
            } else {
                convertView.findViewById(R.id.ch_view_download_group_divider).setVisibility(View.GONE);
            }
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            DownloadFileInfo info = getChild(groupPosition, childPosition);
            DownloadItemView itemView = new DownloadItemView(DownloadListActivity.this);
            itemView.setDeleteListener(new DownloadListener());
            mDownloadItemViews.add(itemView);
            itemView.setData(info);
            return itemView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }

    class DownloadListener implements DownloadItemView.OnDownloadListener {

        @Override
        public void deleteItem(DownloadFileInfo info) {
            deleteInfo(info);
        }

        @Override
        public void completed(DownloadFileInfo info) {
            removeDownloadingList(info);
            addCompletedList(info);
            adapterNotify();
        }
    }
}
