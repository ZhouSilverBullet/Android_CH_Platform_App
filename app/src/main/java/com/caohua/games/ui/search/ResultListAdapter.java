package com.caohua.games.ui.search;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.TransmitDataInterface;
import com.caohua.games.biz.search.AssigCatEntry;
import com.chsdk.ui.widget.CHToast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CXK on 2016/11/10.
 */

public class ResultListAdapter extends BaseAdapter {
    private List<AssigCatEntry> list;
    private LayoutInflater inflater;
    private TransmitDataInterface transmitDataInterface;

    public ResultListAdapter(Activity activity, List<AssigCatEntry> list, TransmitDataInterface transmitDataInterface) {
        inflater = LayoutInflater.from(activity);
        this.list = new ArrayList<>();
        this.list.addAll(list);
        this.transmitDataInterface = transmitDataInterface;
    }

    public void setData(List<AssigCatEntry> data) {
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    public AssigCatEntry getItemData(int position) {
        if (list != null && list.size() > position) {
            return list.get(position);
        }
        return null;
    }

    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        if (list == null) {
            return null;
        }
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.ch_activity_result_list_item, null);
            viewHolder.gameName = (TextView) convertView.findViewById(R.id.ch_list_username);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.gameName.setText(list.get(position).getGame_name());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (transmitDataInterface != null) {
                    transmitDataInterface.transmit(position);
                }
            }
        });
        return convertView;
    }

    private class ViewHolder {
        TextView gameName;
    }
}
