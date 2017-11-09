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

public class PrefectureStrategyItemView extends RelativeLayout implements DataInterface {
    private ImageView strategyImage;
    private TextView strategyTitle;
    private TextView strategyType;
    private TextView strategyDate;

    public PrefectureStrategyItemView(Context context) {
        this(context, null);
    }

    public PrefectureStrategyItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PrefectureStrategyItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.ch_prefecture_item_strategy, this, true);
        strategyImage = (ImageView) findViewById(R.id.ch_prefecture_strategy_image);
        strategyTitle = ((TextView) findViewById(R.id.ch_prefecture_strategy_title));
        strategyType = ((TextView) findViewById(R.id.ch_prefecture_strategy_type));
        strategyDate = ((TextView) findViewById(R.id.ch_prefecture_strategy_date));
    }

    @Override
    public void setData(Object o) {
//        o instanceof
    }
}
