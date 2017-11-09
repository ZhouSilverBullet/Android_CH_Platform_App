package com.caohua.games.ui.prefecture;

import android.content.Context;
import android.support.v4.util.SparseArrayCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.prefecture.GameCenterEntry;
import com.chsdk.biz.app.AnalyticsHome;
import com.chsdk.biz.download.DownloadEntry;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.utils.PicUtil;
import com.chsdk.utils.ViewUtil;

import java.util.List;

/**
 * Created by zhouzhou on 2017/5/23.
 */

public class GameCenterHorizontalScrollView extends HorizontalScrollView {
    public static final int TYPE_MY_GAME = 100;
    public static final int TYPE_MY_MULTIPLE = 101;

    private LinearLayout linearLayout;

    public GameCenterHorizontalScrollView(Context context) {
        this(context, null);
    }

    public GameCenterHorizontalScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameCenterHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        linearLayout = new LinearLayout(getContext());
        addView(linearLayout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
    }

    public int getDp2pxInt(int padding) {
        return ViewUtil.dp2px(getContext(), padding);
    }

    private SparseArrayCompat<View> mViews;

    public void setData(Object o, int type) {
        if (mViews == null) {
            mViews = new SparseArrayCompat<>();
        }
        if (o instanceof List) {
            if (type == TYPE_MY_GAME) {
                List<GameCenterEntry.DataBean.MygameBean> mygameBeen = (List<GameCenterEntry.DataBean.MygameBean>) o;
                linearLayout.removeAllViews();
                if (mygameBeen.size() == 0) {
                    TextView textView = new TextView(getContext());
                    textView.setText("您可能没有玩过草花游戏");
                    float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 5, getResources().getDisplayMetrics());
                    textView.setTextSize(size);
                    textView.setPadding(getDp2pxInt(15), getDp2pxInt(5), getDp2pxInt(15), getDp2pxInt(5));
                    linearLayout.addView(textView);
                    return;
                }
                for (int i = 0; i < mygameBeen.size(); i++) {
                    setItemStatus(i, TYPE_MY_GAME, mygameBeen.get(i));
                }
            } else if (type == TYPE_MY_MULTIPLE) {
                List<GameCenterEntry.DataBean.MultipleBean> multipleBeen = (List<GameCenterEntry.DataBean.MultipleBean>) o;
                linearLayout.removeAllViews();
                if (multipleBeen.size() == 0) {
                    TextView textView = new TextView(getContext());
                    textView.setText("现在没有推荐的草花游戏");
                    float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 5, getResources().getDisplayMetrics());
                    textView.setTextSize(size);
                    textView.setPadding(getDp2pxInt(15), getDp2pxInt(5), getDp2pxInt(15), getDp2pxInt(5));
                    linearLayout.addView(textView);
                    return;
                }
                for (int i = 0; i < multipleBeen.size(); i++) {
                    setItemStatus(i, TYPE_MY_MULTIPLE, multipleBeen.get(i));
                }
            }
        }
    }

    public void setItemStatus(int i, final int type, Object obj) {
        View view = getCacheView(i);
        ImageView image = (ImageView) view.findViewById(R.id.ch_game_center_item_recycler_image);
        TextView name = (TextView) view.findViewById(R.id.ch_game_center_item_recycler_text);
        view.setPadding(getDp2pxInt(15), getDp2pxInt(5), getDp2pxInt(15), getDp2pxInt(5));
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.addView(view, layoutParams);
        String game_icon = null;
        String game_name = null;
        String detail_url = null;
        final DownloadEntry downloadEntry = new DownloadEntry();
        switch (type) {
            case TYPE_MY_GAME:
                GameCenterEntry.DataBean.MygameBean mygameBeen = (GameCenterEntry.DataBean.MygameBean) obj;
                game_icon = mygameBeen.getGame_icon();
                game_name = mygameBeen.getGame_name();
                detail_url = mygameBeen.getDetail_url();
                downloadEntry.setTitle(game_name);
                downloadEntry.setIconUrl(game_icon);
                downloadEntry.setDetail_url(detail_url);
                downloadEntry.setDownloadUrl(mygameBeen.getGame_url());
                downloadEntry.setPkg(mygameBeen.getPackage_name());
                break;
            case TYPE_MY_MULTIPLE:
                GameCenterEntry.DataBean.MultipleBean multipleBean = (GameCenterEntry.DataBean.MultipleBean) obj;
                game_icon = multipleBean.getGame_icon();
                game_name = multipleBean.getGame_name();
                detail_url = multipleBean.getDetail_url();
                downloadEntry.setTitle(game_name);
                downloadEntry.setIconUrl(game_icon);
                downloadEntry.setDetail_url(detail_url);
                downloadEntry.setDownloadUrl(multipleBean.getGame_url());
                downloadEntry.setPkg(multipleBean.getPackage_name());
                break;
        }

        if (!TextUtils.isEmpty(game_icon)) {
            PicUtil.displayImg(getContext(), image, game_icon, R.drawable.ch_default_apk_icon);
        } else {
            image.setImageResource(R.drawable.ch_default_apk_icon);
        }

        name.setText(game_name);
        final String detailUrl = detail_url;
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (type) {
                    case TYPE_MY_GAME:
                        AnalyticsHome.umOnEvent(AnalyticsHome.GAME_CENTER_ITEM_MINE, "游戏中心我的游戏item点击");
                        break;
                    case TYPE_MY_MULTIPLE:
                        AnalyticsHome.umOnEvent(AnalyticsHome.GAME_CENTER_ITEM_RECOMMENT, "游戏中心推荐游戏item点击");
                        break;
                }
                if (!TextUtils.isEmpty(detailUrl)) {
                    WebActivity.startGameDetail(getContext(), downloadEntry);
                }
            }
        });

    }

    public View getCacheView(int i) {
        View view = mViews.get(i);
        if (view == null) {
            view = View.inflate(getContext(), R.layout.ch_game_center_item_recycler_item, null);
            mViews.put(i, view);
        }
        return view;
    }
}
