package com.caohua.games.ui.bbs;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.TransmitDataInterface;
import com.caohua.games.biz.article.OperateLogic;
import com.caohua.games.biz.article.ReadArticleEntry;
import com.caohua.games.ui.article.ReportActivity;
import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.app.AnalyticsHome;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.utils.ViewUtil;

/**
 * Created by zhouzhou on 2017/6/1.
 */

public class MorePopView extends PopupWindow {

    public static final int TYPE_BIAN_JI = 100;
    public static final int TYPE_SHOU_CHANG = 101;
    public static final int TYPE_JIA_JIN = 102;
    public static final int TYPE_SHAN_CHU = 103;
    public static final int TYPE_YIN_CHANG = 104;
    public static final int TYPE_FU_ZHI = 105;
    public static final int TYPE_JU_BAO = 106;
    private final String url;
    private final String articleId;
    private ReadArticleEntry articleEntry;

    private View anchor;
    private Context context;
    private MorePopItemView zhiDing;
    private MorePopItemView fuZhi;
    private MorePopItemView juBao;
    private MorePopItemView jiaJin;
    private MorePopItemView suoDing;
    private MorePopItemView shouChang;
    private MorePopItemView yinChang;
    private View lineFuzhi;
    private View lineJiajin;
    private View lineSuoding;
    private View lineYinchang;
    private View lineZhiding;

    public MorePopView(Context context, View anchor, ReadArticleEntry articleEntry, String url, String articleId) {
        super(context);
        this.context = context;
        this.anchor = anchor;
        this.articleEntry = articleEntry;
        this.url = url;
        this.articleId = articleId;
        init();
    }

    private void init() {

        View view = LayoutInflater.from(context).inflate(R.layout.ch_favorite_more_pop, null);
        initView(view);
        setContentView(view);
        setWidth(ViewUtil.dp2px(context, 100));
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable());
        int[] location = new int[2];
        anchor.getLocationOnScreen(location);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        showAtLocation(anchor, Gravity.NO_GRAVITY, location[0] - ViewUtil.dp2px(context, 70),
                location[1] + ViewUtil.dp2px(context, 35));
        AnalyticsHome.umOnEvent(AnalyticsHome.FORUM_POP_WINDOW, "弹出用户操作界面");
        setOutsideTouchable(true);
        update();
        setBackgroundAlpha(0.5f, ((WebActivity) context));
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1.0f, (WebActivity) context);
            }
        });
        setData();
    }

    private void setData() {
        //复制
        fuZhi.setData("复制", R.drawable.ch_more_pop_copy);
        fuZhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnalyticsHome.umOnEvent(AnalyticsHome.FORUM_COPY, "帖子详情复制内容");
                ClipboardManager copyManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                copyManager.setText(url);
                CHToast.show(context, "复制链接成功");
                dismiss();
            }
        });
        //收藏
        final int collect = getInt(articleEntry.is_collect);
        if (collect == 0) {
            shouChang.setData("收藏", R.drawable.ch_more_pop_souchang);
        } else {
            shouChang.setData("取消收藏", R.drawable.ch_more_pop_souchang);
        }
        shouChang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppContext.getAppContext().isLogin()) {
                    AppContext.getAppContext().login((Activity) context, new TransmitDataInterface() {
                        @Override
                        public void transmit(Object o) {

                        }
                    });
                    return;
                }
                AnalyticsHome.umOnEvent(AnalyticsHome.FORUM_COLLECT, "帖子详情收藏点击");
                logic(OperateLogic.OPERATE_COLLECT, collect + 1);
                dismiss();
            }
        });
        //置顶
        final int top = getInt(articleEntry.priv_top);
        final int isTop = getInt(articleEntry.is_top);
        if (top == 1) {
            lineZhiding.setVisibility(View.VISIBLE);
            zhiDing.setVisibility(View.VISIBLE);
            if (isTop == 0) {
                zhiDing.setData("置顶", R.drawable.ch_more_pop_bianji);
            } else {
                zhiDing.setData("取消置顶", R.drawable.ch_more_pop_bianji);
            }
        } else {
            lineZhiding.setVisibility(View.GONE);
            zhiDing.setVisibility(View.GONE);
        }
        zhiDing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logic(OperateLogic.OPERATE_TOP, isTop + 1);
                dismiss();
            }
        });
        //隐藏
        final int hide = getInt(articleEntry.priv_hide);
        final int isHide = getInt(articleEntry.is_hide);
        if (hide == 1) {
            lineYinchang.setVisibility(View.VISIBLE);
            yinChang.setVisibility(View.VISIBLE);
            if (isHide == 0) {
                yinChang.setData("隐藏", R.drawable.ch_more_pop_gone);
            } else {
                yinChang.setData("取消置顶", R.drawable.ch_more_pop_gone);
            }
        } else {
            lineYinchang.setVisibility(View.GONE);
            yinChang.setVisibility(View.GONE);
        }
        yinChang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logic(OperateLogic.OPERATE_HIDE, isHide + 1);
                dismiss();
            }
        });

        //锁定
        final int lock = getInt(articleEntry.priv_lock);
        final int isLock = getInt(articleEntry.is_lock);
        if (lock == 1) {
            lineSuoding.setVisibility(View.VISIBLE);
            suoDing.setVisibility(View.VISIBLE);
            if (isLock == 0) {
                suoDing.setData("锁定", R.drawable.ch_more_pop_delete);
            } else {
                suoDing.setData("取消锁定", R.drawable.ch_more_pop_delete);
            }
        } else {
            lineSuoding.setVisibility(View.GONE);
            suoDing.setVisibility(View.GONE);
        }
        suoDing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logic(OperateLogic.OPERATE_LOCK, isLock + 1);
                dismiss();
            }
        });
        //加精
        final int good = getInt(articleEntry.priv_good);
        final int isGood = getInt(articleEntry.is_good);
        if (good == 1) {
            lineJiajin.setVisibility(View.VISIBLE);
            jiaJin.setVisibility(View.VISIBLE);
            if (isGood == 0) {
                jiaJin.setData("加精", R.drawable.ch_more_pop_jiajin);
            } else {
                jiaJin.setData("取消加精", R.drawable.ch_more_pop_jiajin);
            }
        } else {
            lineJiajin.setVisibility(View.GONE);
            jiaJin.setVisibility(View.GONE);
        }
        jiaJin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logic(OperateLogic.OPERATE_GOOD, isGood + 1);
                dismiss();
            }
        });
        //举报
        juBao.setData("举报", R.drawable.ch_more_pop_jubao);
        juBao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnalyticsHome.umOnEvent(AnalyticsHome.FORUM_REPORT, "帖子详情举报点击");
                ReportActivity.start(context, articleId);
                dismiss();
            }
        });
    }

    private void logic(final String operateType, final int operateResult) {
        OperateLogic logic = new OperateLogic();
        logic.doOperate(articleId, operateType, operateResult + "", new BaseLogic.LogicListener() {
            @Override
            public void failed(String errorMsg) {
                CHToast.show(context, errorMsg);
            }

            @Override
            public void success(String... result) {
                switch (operateType) {
                    case OperateLogic.OPERATE_TOP: //置顶
                        if (operateResult == 1) {
                            CHToast.show(context, "置顶成功");
                            zhiDing.setText("取消置顶");
                            articleEntry.is_top = 1 + "";
                        } else if (operateResult == 2) {
                            CHToast.show(context, "取消置顶成功");
                            zhiDing.setText("置顶");
                            articleEntry.is_top = 0 + "";
                        }
                        break;
                    case OperateLogic.OPERATE_COLLECT: //收藏
                        if (operateResult == 1) {
                            CHToast.show(context, "收藏成功");
                            shouChang.setText("取消收藏");
                            articleEntry.is_collect = 1 + "";
                        } else if (operateResult == 2) {
                            CHToast.show(context, "取消收藏成功");
                            shouChang.setText("收藏");
                            articleEntry.is_collect = 0 + "";
                        }
                        break;
                    case OperateLogic.OPERATE_GOOD: //加精
                        if (operateResult == 1) {
                            CHToast.show(context, "加精成功");
                            jiaJin.setText("取消加精");
                            articleEntry.is_good = 1 + "";
                        } else if (operateResult == 2) {
                            CHToast.show(context, "取消加精成功");
                            jiaJin.setText("加精");
                            articleEntry.is_good = 0 + "";
                        }
                        break;
                    case OperateLogic.OPERATE_LOCK: //锁定
                        if (operateResult == 1) {
                            CHToast.show(context, "锁定成功");
                            suoDing.setText("取消锁定");
                            articleEntry.is_lock = 1 + "";
                        } else if (operateResult == 2) {
                            CHToast.show(context, "取消锁定成功");
                            suoDing.setText("锁定");
                            articleEntry.is_lock = 0 + "";
                        }
                        break;
                    case OperateLogic.OPERATE_HIDE: //隐藏
                        if (operateResult == 1) {
                            CHToast.show(context, "隐藏成功");
                            yinChang.setText("取消隐藏");
                            articleEntry.is_hide = 1 + "";
                        } else if (operateResult == 2) {
                            CHToast.show(context, "取消隐藏成功");
                            yinChang.setText("隐藏");
                            articleEntry.is_hide = 0 + "";
                            ((Activity) context).finish();
                        }
                        break;
                }
            }
        });
    }

    private int getInt(String value) {
        if (!TextUtils.isEmpty(value)) {
            return Integer.parseInt(value);
        }
        return 0;
    }

    private void initView(View view) {
        zhiDing = ((MorePopItemView) view.findViewById(R.id.ch_more_pop_zhiding));
        fuZhi = ((MorePopItemView) view.findViewById(R.id.ch_more_pop_fuzhi));
        juBao = ((MorePopItemView) view.findViewById(R.id.ch_more_pop_jubao));
        jiaJin = ((MorePopItemView) view.findViewById(R.id.ch_more_pop_jiajin));
        suoDing = ((MorePopItemView) view.findViewById(R.id.ch_more_pop_suoding));
        shouChang = ((MorePopItemView) view.findViewById(R.id.ch_more_pop_shouchang));
        yinChang = ((MorePopItemView) view.findViewById(R.id.ch_more_pop_yinchang));

        lineFuzhi = view.findViewById(R.id.ch_line_fuzhi);
        lineJiajin = view.findViewById(R.id.ch_line_jiajin);
        lineSuoding = view.findViewById(R.id.ch_line_suoding);
        lineYinchang = view.findViewById(R.id.ch_line_yinchang);
        lineZhiding = view.findViewById(R.id.ch_line_zhiding);
    }

    public void setBackgroundAlpha(float bgAlpha, Activity activity) {
        WindowManager.LayoutParams lp = activity.getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        activity.getWindow().setAttributes(lp);
    }
}
