package com.caohua.games.ui.dataopen.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.dataopen.entry.DataOpenServerEntry;
import com.caohua.games.ui.adapter.CommonRecyclerViewAdapter;
import com.caohua.games.ui.adapter.ViewHolder;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.utils.ViewUtil;

import java.util.ArrayList;
import java.util.List;

public class DataOpenServerAlertDialog {
    private Activity activity;
    private Dialog dialog;
    private Button btnOk, btnCancel;
    private boolean outSide;
    private boolean cancelable;
    private RecyclerView recyclerView;
    private ServerAdapter adapter;

    public DataOpenServerAlertDialog(Activity activity) {
        this.activity = activity;
        this.outSide = true;
        this.cancelable = true;
    }

    public void setData(List<DataOpenServerEntry> list) {
        if (list == null || list.size() == 0) {
            CHToast.show(activity, "获取数据失败！");
            return;
        }
        if (adapter != null) {
            adapter.addAll(list);
        }
    }

    public void dismiss() {
        if (dialog != null)
            dialog.dismiss();
    }

    public void show() {
        dialog = new Dialog(activity, R.style.ch_translucent_style);
        dialog.setCanceledOnTouchOutside(outSide);
        dialog.setCancelable(cancelable);
        dialog.show();

        View view = LayoutInflater.from(activity).inflate(R.layout.ch_dialog_data_open_server_view, null);
        dialog.setContentView(view);

        initView(view);
    }

    public void setOkButton(String btnName, OnClickListener listener) {
        btnOk.setText(btnName);
        if (listener != null) {
            btnOk.setOnClickListener(listener);
        }
    }

    public void setCancelButton(String btnName, OnClickListener listener) {
        btnCancel.setText(btnName);
        if (listener != null) {
            btnCancel.setOnClickListener(listener);
        }
    }

    private void initView(View view) {
        btnOk = ViewUtil.getView(view, R.id.ch_dialog_data_open_server_alert_ok);
        btnCancel = ViewUtil.getView(view, R.id.ch_dialog_data_open_server_alert_cancel);
        recyclerView = ViewUtil.getView(view, R.id.ch_dialog_data_open_server_alert_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(activity, 3));
        recyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        adapter = new ServerAdapter(activity, new ArrayList<DataOpenServerEntry>(), R.layout.ch_dialog_data_open_server_item_view);
        recyclerView.setAdapter(adapter);
        adapter.setOnClickItemListener(new CommonRecyclerViewAdapter.OnClickItemListener() {
            @Override
            public void onClickItem(CommonRecyclerViewAdapter ViewAdapter, ViewHolder holder, int position) {
                if (!adapter.isChecked(position)) {
                    adapter.setChecked(position);
                }
            }
        });
        btnOk.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private int checkedPosition = -1;

    public Dialog getDialog() {
        return dialog;
    }

    private class ServerAdapter extends CommonRecyclerViewAdapter<DataOpenServerEntry> {

        public ServerAdapter(Context context, List<DataOpenServerEntry> list, int layoutId) {
            super(context, list, layoutId);
        }

        @Override
        protected void covert(ViewHolder holder, DataOpenServerEntry entry) {
//            CheckBox checkBox = holder.getView(R.id.ch_dialog_data_open_server_item_check);
//            checkBox.setText(entry.cp_sname);
        }

        @Override
        protected void covert(ViewHolder holder, DataOpenServerEntry entry, final int position) {
            TextView textView = holder.getView(R.id.ch_dialog_data_open_server_item_text);
            textView.setText(entry.cp_sname);
            if (position == checkedPosition) {
                textView.setTextColor(activity.getResources().getColor(R.color.green));
            } else {
                textView.setTextColor(activity.getResources().getColor(R.color.ch_black));
            }
        }

        public boolean isChecked(int position) {
            return checkedPosition == position;
        }

        public void setChecked(int position) {
            int prevChecked = checkedPosition;
            checkedPosition = position;

            if (prevChecked != -1) {
                notifyItemChanged(prevChecked);
            }
            notifyItemChanged(checkedPosition);
        }
    }
}
