package com.caohua.games.ui.prefecture;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.DataInterface;

/**
 * Created by zhouzhou on 2017/5/23.
 */

public class PrefectureBigImgItemView extends RelativeLayout implements DataInterface {
    private ImageView bigImage;
    private TextView timeText;
    private TextView contentText;

    public PrefectureBigImgItemView(Context context) {
        this(context, null);
    }

    public PrefectureBigImgItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PrefectureBigImgItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.ch_prefecture_item_big_img, this, true);
        bigImage = ((ImageView) findViewById(R.id.ch_prefecture_big_img_img));
        timeText = ((TextView) findViewById(R.id.ch_prefecture_big_img_time));
        contentText = ((TextView) findViewById(R.id.ch_prefecture_big_img_content));
    }

    @Override
    public void setData(Object o) {

    }
}
