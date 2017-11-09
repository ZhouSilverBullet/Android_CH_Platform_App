package com.caohua.games.ui.bbs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.bbs.BBSTopEntry;
import com.chsdk.ui.widget.RiffEffectImageButton;

/**
 * Created by zhouzhou on 2017/5/25.
 */

public class BBSRadioGridItemView extends RelativeLayout {
    private RiffEffectImageButton imageView;
    private TextView textView;
    private OnRadioItemClickListener mListener;

    public BBSRadioGridItemView(Context context) {
        this(context, null);
    }

    public BBSRadioGridItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BBSRadioGridItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    public void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.ch_bbs_radio_grid_item, this, true);
        imageView = ((RiffEffectImageButton) findViewById(R.id.ch_radio_grid_item_image));
        textView = ((TextView) findViewById(R.id.ch_radio_grid_item_text));

    }

    public void doItemSelector() {
        imageView.setImageResource(R.drawable.ch_bbs_selector_pop_selector);
        textView.setTextColor(getResources().getColor(R.color.ch_white));
    }

    public void doItemNotSelector() {
        imageView.setImageResource(R.drawable.download_button_shape_error);
        textView.setTextColor(getResources().getColor(R.color.ch_black));
    }

    public void setData(final int position, final BBSTopEntry.DataBean.ForumTagBean forumTagBean) {
        textView.setText(forumTagBean.getTag_name());
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(position, forumTagBean.getTag_id());
                }
            }
        });
    }
    public void setOnRadioItemClickListener(OnRadioItemClickListener listener) {
        mListener = listener;
    }

    public interface OnRadioItemClickListener {
        void onItemClick(int position, String tagId);
    }

}
