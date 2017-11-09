package com.caohua.games.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.download.ViewDownloadMgr;
import com.caohua.games.biz.minegame.MineGameListEntry;
import com.caohua.games.ui.download.DownloadButton;
import com.chsdk.ui.WebActivity;
import com.chsdk.utils.PicUtil;

import java.util.List;

/**
 * Created by zhouzhou on 2017/2/22.
 */

public class MineGameRecyclerAdapter extends RecyclerView.Adapter<MineGameRecyclerAdapter.MineGameHolder> {
    private final LayoutInflater inflater;
    private List<MineGameListEntry> list;
    private Context context;

    public MineGameRecyclerAdapter(List<MineGameListEntry> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MineGameHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = inflater.inflate(R.layout.mine_game_item_view, parent, false);
        return new MineGameHolder(inflate);
    }

    @Override
    public void onBindViewHolder(MineGameHolder holder, int position) {
        final MineGameListEntry entry = list.get(position);
        if (entry == null)
            return;

        if (!TextUtils.isEmpty(entry.getGame_icon())) {
            PicUtil.displayImg(context, holder.imgIcon, entry.getGame_icon(), R.drawable.ch_default_apk_icon);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebActivity.startGameDetail(context, entry.getDownloadEntry());
            }
        });
        holder.tvTitle.setText(entry.getGame_name());
        holder.tvDes.setText(entry.getGame_introduct());
        if (entry.getStatus() == 3) {
            holder.downloadMgr.setData(entry.getDownloadEntry(), entry.getStatus() == 3);
            holder.tvType.setText("版本:" + entry.getGame_version() + " | " + entry.getGame_size() + "MB");
        } else {
            holder.tvType.setText(entry.getClassify_name() + " | " + entry.getGame_size() + "MB");
            holder.downloadMgr.setData(entry.getDownloadEntry());
        }
    }

    public void addAll(List<MineGameListEntry> entryList) {
        if (list == null) {
            return;
        }
        list.addAll(entryList);
        notifyDataSetChanged();
    }

    public void clearAll() {
        list.clear();
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class MineGameHolder extends RecyclerView.ViewHolder {
        private ViewDownloadMgr downloadMgr;
        private ImageView imgIcon;
        private TextView tvTitle, tvType, tvDes;

        public MineGameHolder(View itemView) {
            super(itemView);
            imgIcon = (ImageView) itemView.findViewById(R.id.ch_view_home_rcmd_item_icon);
            tvTitle = (TextView) itemView.findViewById(R.id.ch_view_home_rcmd_item_title);
            tvType = (TextView) itemView.findViewById(R.id.ch_view_home_rcmd_item_type_and_size);
            tvDes = (TextView) itemView.findViewById(R.id.ch_view_home_rcmd_item_des);
            downloadMgr = new ViewDownloadMgr((DownloadButton) itemView.findViewById(R.id.ch_view_home_rcmd_item_btn));
        }
    }

}
