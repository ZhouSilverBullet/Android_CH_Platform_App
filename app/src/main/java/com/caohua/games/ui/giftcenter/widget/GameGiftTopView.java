package com.caohua.games.ui.giftcenter.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.gift.GameGiftEntry;
import com.chsdk.utils.PicUtil;

/**
 * Created by admin on 2017/10/31.
 */

public class GameGiftTopView extends RelativeLayout {

    private Context context;
    private ImageView imageView;
    private TextView title;
    private TextView count;
    private TextView des;

    public GameGiftTopView(Context context) {
        this(context, null);
    }

    public GameGiftTopView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameGiftTopView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.ch_game_gfit_top_view, this, true);
        setVisibility(GONE);
        imageView = (ImageView) findViewById(R.id.ch_game_gift_top_image);
        title = (TextView) findViewById(R.id.ch_game_gift_top_title);
        count = (TextView) findViewById(R.id.ch_game_gift_top_count);
        des = (TextView) findViewById(R.id.ch_game_gift_top_des);
    }

    public void setData(GameGiftEntry.GameBean entry) {
        if (entry == null) {
            setVisibility(GONE);
            return;
        }
        setVisibility(VISIBLE);
        PicUtil.displayImg(context, imageView, entry.getGame_icon(), R.drawable.ch_default_apk_icon);
        title.setText(entry.getGame_name());
        des.setText(entry.getShort_desc());
    }

}
