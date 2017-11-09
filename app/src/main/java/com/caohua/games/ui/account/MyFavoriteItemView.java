package com.caohua.games.ui.account;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.DataInterface;
import com.caohua.games.biz.account.MyFavoriteEntry;
import com.caohua.games.biz.article.ArticleCollectLogic;
import com.caohua.games.biz.article.OperateLogic;
import com.caohua.games.biz.bbs.ForumShareEntry;
import com.caohua.games.biz.mymsg.MyMsgEntry;
import com.chsdk.biz.BaseLogic;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.RiffEffectLinearLayout;
import com.chsdk.ui.widget.RiffEffectRelativeLayout;
import com.chsdk.utils.PicUtil;
import com.chsdk.utils.ViewUtil;

/**
 * Created by Zenglei on 2017/05/23.
 */

public class MyFavoriteItemView extends RiffEffectRelativeLayout implements DataInterface {
    private ImageView chPrefectureStrategyImage;
    private TextView chPrefectureStrategyTitle;
    private TextView chPrefectureStrategyDate;
    private DataInterface listener;
    private MyFavoriteEntry entry;

    public MyFavoriteItemView(Context context, DataInterface listener) {
        super(context);
        lodXml();
        this.listener = listener;
    }

    public MyFavoriteItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        lodXml();
    }

    public MyFavoriteItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        lodXml();
    }

    private void lodXml() {
        LayoutInflater.from(getContext()).inflate(R.layout.ch_view_my_favorite_item, this, true);
        int padding = ViewUtil.dp2px(getContext(), 10);
        setPadding(padding, padding, padding, padding);
        setClickable(true);
        init();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setItems(new String[]{"取消收藏"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                cancelFavorite();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isShow = false;
            }
        });
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                isShow = true;
                alertDialog.show();
                return true;
            }
        });
    }

    private boolean isShow = false;

    private void init() {
        chPrefectureStrategyImage = (ImageView) findViewById(R.id.ch_prefecture_strategy_image);
        chPrefectureStrategyTitle = (TextView) findViewById(R.id.ch_prefecture_strategy_title);
        chPrefectureStrategyDate = (TextView) findViewById(R.id.ch_prefecture_strategy_date);
    }

    public void setData(Object o) {
        chPrefectureStrategyImage.setVisibility(View.GONE);
        if (o instanceof MyFavoriteEntry) {
            final MyFavoriteEntry entry = (MyFavoriteEntry) o;
            this.entry = entry;
            chPrefectureStrategyTitle.setText(entry.title);
            chPrefectureStrategyDate.setText(entry.add_time);
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isShow) {
                        return;
                    }
                    String detail_url = entry.detail_url;
                    if (!TextUtils.isEmpty(detail_url)) {
                        try {
                            String[] split = detail_url.split("id=");
                            if (split.length >= 2) {
                                String articleId = split[1];
                                ForumShareEntry forumShareEntry = new ForumShareEntry();
                                forumShareEntry.setTitle(entry.title);
                                forumShareEntry.setGameIcon(entry.game_icon);
                                forumShareEntry.setGameName(entry.forum_name);
                                WebActivity.startForForumPage(getContext(), detail_url, articleId, forumShareEntry, -1);
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            });
            if (!TextUtils.isEmpty(entry.article_img)) {
                chPrefectureStrategyImage.setVisibility(View.VISIBLE);
                PicUtil.displayImg(getContext(), chPrefectureStrategyImage, entry.article_img, R.drawable.ch_default_pic);
            }
        }
    }

    private void cancelFavorite() {
        if (entry.type.equals("1")) { //帖子
            OperateLogic logic = new OperateLogic();
            logic.doOperate(entry.article_id, OperateLogic.OPERATE_COLLECT, OperateLogic.CANCEL, new BaseLogic.LogicListener() {
                @Override
                public void failed(String errorMsg) {
                    CHToast.show(getContext(), errorMsg);
                }

                @Override
                public void success(String... result) {
                    CHToast.show(getContext(), "取消收藏成功");
                    if (listener != null) {
                        listener.setData(entry);
                    }
                }
            });
        } else {
            ArticleCollectLogic logic = new ArticleCollectLogic();
            logic.articleCollect(entry.article_id, "2", new BaseLogic.LogicListener() {
                @Override
                public void failed(String errorMsg) {
                    CHToast.show(getContext(), errorMsg);
                }

                @Override
                public void success(String... result) {
                    CHToast.show(getContext(), "取消收藏成功");
                    if (listener != null) {
                        listener.setData(entry);
                    }
                }
            });
        }

    }
}
