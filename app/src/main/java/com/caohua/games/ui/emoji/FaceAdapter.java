package com.caohua.games.ui.emoji;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.caohua.games.R;

import java.util.List;

/**
 * 表情填充器
 */
public class FaceAdapter extends BaseAdapter {

    private List<ChatEmojiEntry> data;

    private LayoutInflater inflater;

    private int size = 0;

    public FaceAdapter(Context context, List<ChatEmojiEntry> list) {
        this.inflater = LayoutInflater.from(context);
        this.data = list;
        this.size = list.size();
    }

    @Override
    public int getCount() {
        return this.size;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatEmojiEntry emoji = data.get(position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.ch_emoji_item_face, null);
            viewHolder.iv_face = (ImageView) convertView.findViewById(R.id.item_iv_face);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (emoji.getId() == R.drawable.ch_emoji_face_del_icon) {
            convertView.setBackgroundDrawable(null);
            viewHolder.iv_face.setImageResource(emoji.getId());
        } else if (TextUtils.isEmpty(emoji.getCharacter())) {
            convertView.setBackgroundDrawable(null);
            viewHolder.iv_face.setImageDrawable(null);
        } else {
            viewHolder.iv_face.setTag(emoji);
            viewHolder.iv_face.setImageResource(emoji.getId());
        }

        return convertView;
    }

    class ViewHolder {

        public ImageView iv_face;
    }
}