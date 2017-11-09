package com.caohua.games.ui.bbs;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caohua.games.R;
import com.chsdk.ui.widget.RiffEffectLinearLayout;

/**
 * Created by zhouzhou on 2017/6/2.
 */

public class MorePopItemView extends RiffEffectLinearLayout {
    private ImageView imageView;
    private TextView textView;

    public MorePopItemView(Context context) {
        this(context, null);
    }

    public MorePopItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MorePopItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.ch_favorite_more_pop_item, this, true);

        imageView = ((ImageView) findViewById(R.id.ch_more_pop_item_image));
        textView = ((TextView) findViewById(R.id.ch_more_pop_item_text));
    }

    public void setData(String text, @DrawableRes int imageRec) {
        textView.setText(text);
        imageView.setImageResource(imageRec);
    }

    public void setText(String text) {
        textView.setText(text);
    }
}
