package com.caohua.games.ui.emoji;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.TransmitDataInterface;
import com.caohua.games.biz.article.ArticleAddCommentLogic;
import com.caohua.games.biz.article.ArticleAddReplyLogic;
import com.caohua.games.biz.article.ArticleAddVoteLogic;
import com.caohua.games.biz.article.ArticleCollectLogic;
import com.caohua.games.biz.article.ReadArticleEntry;
import com.caohua.games.biz.bbs.BBSNotifyCommentData;
import com.caohua.games.biz.bbs.BBSNotifyPraiseData;
import com.caohua.games.biz.bbs.ForumShareEntry;
import com.caohua.games.biz.comment.CommentEntry;
import com.caohua.games.biz.comment.CommentPraiseLogic;
import com.caohua.games.biz.comment.CommentScrollEntry;
import com.caohua.games.biz.comment.CommentTimesLogic;
import com.caohua.games.biz.comment.LoginUpdateEntry;
import com.caohua.games.biz.comment.TimesEntry;
import com.caohua.games.biz.comment.TimesPraiseEntry;
import com.caohua.games.biz.comment.UpdateCommentEntry;
import com.caohua.games.biz.comment.UpdatePraiseEntry;
import com.caohua.games.biz.share.CHShareLayout;
import com.caohua.games.ui.send.MaxLengthWatcher;
import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.app.AnalyticsHome;
import com.chsdk.configure.DataStorage;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.LoadingDialog;
import com.chsdk.ui.widget.RiffEffectImageButton;
import com.chsdk.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @文件描述 : 带表情的自定义输入框
 */
public class FaceRelativeLayout extends RelativeLayout implements
        OnItemClickListener, OnClickListener {
    public static final int PRAISE_FORUM_LIKE_FOR_H5 = 501;
    public String TAG = getClass().getSimpleName();
    private Context context;

    /**
     * 显示表情页的viewpager
     */
    private ViewPager vp_face;

    /**
     * 表情页界面集合
     */
    private ArrayList<View> pageViews;

    /**
     * 游标显示布局
     */
    private LinearLayout layout_point;

    /**
     * 游标点集合
     */
    private ArrayList<ImageView> pointViews;

    /**
     * 表情集合
     */
    private List<List<ChatEmojiEntry>> emojis;

    /**
     * 表情区域
     */
    private View view;

    /**
     * 输入框
     */
    private EditText et_sendmessage;

    /**
     * 表情数据填充器
     */
    private List<FaceAdapter> faceAdapters;

    /**
     * 当前表情页
     */
    private int current = 0;
    private TextView commentTextView;
    private LinearLayout commentLayout;
    private LinearLayout rlInput;
    private boolean isShowContent;
    private boolean isShowSoft;
    private ViewPagerAdapter mAdapter;
    private ProgressBar emojiProgress;
    private TimesEntry timesEntry;
    private TextView recmText;
    private TextView likeText;
    private TimesPraiseEntry mEntry;
    private String mCommentID;
    private ImageView likeImage;
    private String mCommentUrlTarget;
    private CommentEntry mCommentEntry;

    private InputFilter emojiFilter = new EmojiInputFilter();  //过滤输入法的emoji表情
    public InputFilter[] emojiFilters = {emojiFilter};
    private ReadArticleEntry articleEntry;
    private String articleId;
    private ForumShareEntry forumShareEntry;
    private int position;

    public FaceRelativeLayout(Context context) {
        super(context);
        this.context = context;
    }

    public FaceRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public FaceRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public void setCommentUrlTarget(String commentUrlTarget) {
        mCommentUrlTarget = commentUrlTarget;
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        //如果列表为空就加载表情
        if (FaceConversionUtil.getInstance().getEmojiLists().size() == 0) {
            //加载资源配置中的表情
            new Thread() {
                @Override
                public void run() {
                    FaceConversionUtil.getInstance().getFileText(context);
                }
            }.start();
        }
        emojis = FaceConversionUtil.getInstance().getEmojiLists();
        onCreate();
    }

    private void onCreate() {
        init_View();
        init_Event();
        if (emojis.size() == 0) {
            //为空时，不给viewpager填写数据
            emojiProgress.setVisibility(VISIBLE);
            LogUtil.errorLog("onCreate----->" + emojis.size());
        } else {
            init_viewPager();
            init_Point();
            init_Data();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loadEmoji(final EmojiLoadedEntry entry) {
        emojiProgress.setVisibility(GONE);
        if (entry.isLoad()) {
            emojis = FaceConversionUtil.getInstance().getEmojiLists();
            init_viewPager();
            init_Point();
            init_Data();
        } else {
            //加载表情失败
        }

    }

    private void init_Event() {
        et_sendmessage.setOnClickListener(this);
        findViewById(R.id.btn_face).setOnClickListener(this);
        commentTextView.setOnClickListener(this);
        commentTextView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (isShowContent && !isShowSoft) {
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                view.setVisibility(GONE);
                                isShowContent = true;
                            }
                        }, 100);
                    }
                }
                return false;
            }
        });
        findViewById(R.id.btn_send).setOnClickListener(this);
        findViewById(R.id.ch_recommend_detail_share).setOnClickListener(this);
        findViewById(R.id.ch_recommend_detail_recom).setOnClickListener(this);
        likeImage = (ImageView) findViewById(R.id.ch_recommend_detail_like);
        likeImage.setOnClickListener(this);
        et_sendmessage.addTextChangedListener(new MaxLengthWatcher(140, et_sendmessage));
//        et_sendmessage.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (!TextUtils.isEmpty(s) && s.length() >= 140) {
//                    CHToast.show(getContext(), "已经超过140位");
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
    }

    /**
     * 初始化控件
     */
    private void init_View() {
        vp_face = (ViewPager) findViewById(R.id.vp_contains);
        pageViews = new ArrayList<View>();
        mAdapter = new ViewPagerAdapter(pageViews);
        vp_face.setAdapter(mAdapter);
//        vp_face.setAdapter(new ViewPagerAdapter(pageViews));
        et_sendmessage = (EditText) findViewById(R.id.et_sendmessage);
        et_sendmessage.setFilters(emojiFilters);
        layout_point = (LinearLayout) findViewById(R.id.iv_image);
        rlInput = ((LinearLayout) findViewById(R.id.rl_input));
        emojiProgress = (ProgressBar) findViewById(R.id.ch_emoji_load_progress);


        view = findViewById(R.id.ll_facechoose);
        commentTextView = ((TextView) findViewById(R.id.ch_comment_text_view));
        commentLayout = ((LinearLayout) findViewById(R.id.ch_face_layout_ll));
        recmText = ((TextView) findViewById(R.id.ch_recommend_detail_recom_text));
        likeText = ((TextView) findViewById(R.id.ch_recommend_detail_like_text));
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isCommentDetail) {
                        commentLayout.setVisibility(VISIBLE);
                    } else {
                        commentLayout.setVisibility(GONE);
                    }
                    rlInput.setVisibility(GONE);
                    view.setVisibility(GONE);
                }
            }, 100);
            hideSoftKey();
            return true;
        }
        return super.dispatchKeyEventPreIme(event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_face:
                if (isFastDoubleClick()) {
                    return;
                }
                // 隐藏表情选择框
                if (view.getVisibility() == View.VISIBLE) {
                    view.setVisibility(View.GONE);
                } else {
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            view.setVisibility(View.VISIBLE);
                        }
                    }, 200);
                }
                isShowSoft = false;
                hideSoftKey();

                break;
            case R.id.et_sendmessage:
                // 隐藏表情选择框
                if (view.getVisibility() == View.VISIBLE) {
                    view.setVisibility(View.GONE);
                }

                break;
            case R.id.ch_comment_text_view:
                setCommentID("0");
                showContent();
                break;
            case R.id.btn_send:

                String content = et_sendmessage.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    CHToast.show(getContext(), "内容不能为空");
                    return;
                }

                if (isFastDoubleClick()) {
                    return;
                }

                if (!AppContext.getAppContext().isLogin()) {
                    AppContext.getAppContext().login((Activity) getContext(), new TransmitDataInterface() {
                        @Override
                        public void transmit(Object o) {
                            if (o instanceof LoginUserInfo) {
                                EventBus.getDefault().post(new LoginUpdateEntry());
                            }
                        }
                    });
                    return;
                }

                hideSoftKey();
                hideFaceLayout();
                String readCity = DataStorage.getLocationCity(context);
                LogUtil.errorLog("readCity --- " + readCity);
                if (articleEntry != null) {
                    forumCommentLogic(content, "0");
                } else {
                    commentLogic(readCity, "0", mCommentEntry);
                }
                break;
            case R.id.ch_recommend_detail_share:

                showShare();
                break;
            case R.id.ch_recommend_detail_like:
                if (!AppContext.getAppContext().isLogin()) {
                    AppContext.getAppContext().login((Activity) getContext(), new TransmitDataInterface() {
                        @Override
                        public void transmit(Object o) {
                            if (o instanceof LoginUserInfo) {
                                EventBus.getDefault().post(new LoginUpdateEntry());
                            }
                        }
                    });
                    return;
                }

                if (articleEntry != null) {
                    if (!TextUtils.isEmpty(articleEntry.is_vote)) {
                        if (articleEntry.is_vote.equals("1")) {
                            CHToast.show(getContext(), "您已经点过赞了!");
                            return;
                        }
                    }
                    if (!TextUtils.isEmpty(articleId)) {
                        praiseForumLike(Integer.parseInt(articleId), 0);
                    }
                } else {
                    praiseLike(timesEntry, "0");
                }
                break;
            case R.id.ch_recommend_detail_recom:
                if (isFastDoubleClick()) {
                    return;
                }
                CommentScrollEntry event = new CommentScrollEntry();
                event.setCommentTarget(mCommentUrlTarget);
                EventBus.getDefault().post(event);
                break;
        }
    }

    public void openSoft() {
        rlInput.setVisibility(VISIBLE);
        commentLayout.setVisibility(GONE);
        showContent();
    }

    public void praiseLike(TimesEntry timesEntry, final String commentID) {
        final LoadingDialog loadingDialog = new LoadingDialog(getContext(), "");
        loadingDialog.show();
        CommentPraiseLogic commentPraiseLogic = new CommentPraiseLogic(timesEntry, commentID);
        commentPraiseLogic.getCommentPraise(new BaseLogic.AppLogicListner() {
            @Override
            public void failed(String errorMsg) {
                CHToast.show(getContext(), errorMsg);
                loadingDialog.dismiss();
            }

            @Override
            public void success(Object entryResult) {
                CHToast.show(getContext(), "点赞成功！");
                if (commentID.equals("0")) {
                    likeImage.setImageResource(R.drawable.ch_bottom_emoji_favor_pressed);
                    likeText.setVisibility(VISIBLE);
                    if (!TextUtils.isEmpty(likeText.getText().toString())) {
                        String trim = likeText.getText().toString().trim();
                        if (trim.equals("999+") || trim.equals("999")) {
                            //什么也不干
                            likeText.setText("999+");
                        } else {
                            int i = Integer.parseInt(trim) + 1;
                            likeText.setText("" + getTextValue(i));
                        }

                    } else {
                        likeText.setText("1");
                    }
                }
                UpdatePraiseEntry updatePraiseEntry = new UpdatePraiseEntry();
                updatePraiseEntry.setCommentID(commentID);
                updatePraiseEntry.setCommentTarget(mCommentUrlTarget);
                EventBus.getDefault().post(updatePraiseEntry);
                loadingDialog.dismiss();
            }
        });
    }

    private String getTextValue(int i) {
        if (i > 999) {
            return 999 + "+";
        } else {
            return i + "";
        }
    }

    public void praiseForumLike(int articleId, final int commentID) {
        praiseForumLike(articleId, commentID, 0);
    }

    public void praiseForumLike(int articleId, final int commentID, final int h5) {
        if (!AppContext.getAppContext().isLogin()) {
            AppContext.getAppContext().login((Activity) getContext(), new TransmitDataInterface() {
                @Override
                public void transmit(Object o) {
                    if (o instanceof LoginUserInfo) {
                        EventBus.getDefault().post(new LoginUpdateEntry());
                    }
                }
            });
            return;
        }
        final LoadingDialog loadingDialog = new LoadingDialog(getContext(), "");
        loadingDialog.show();
        int voteType = 0;
        if (articleId != 0) {
            voteType = 1;/*帖子*/
        } else if (commentID != 0) {
            voteType = 2;/*评论 或则 回复*/
        }
        ArticleAddVoteLogic voteLogic = new ArticleAddVoteLogic();
        voteLogic.addVote(articleId + "", commentID + "", voteType + "", new BaseLogic.CommentLogicListener() {
            @Override
            public void failed(String errorMsg, int errorCode) {
                CHToast.show(getContext(), errorMsg);
//                if (errorCode == 1111) {
//                    praiseLikeStatus(commentID, 1111);
//                }
                loadingDialog.dismiss();
            }

            @Override
            public void success(String... result) {
                praiseLikeStatus(commentID, h5);
                loadingDialog.dismiss();
            }
        });
    }

    private void praiseLikeStatus(int commentID, int h5) {
        CHToast.show(getContext(), "点赞成功！");
        if (commentID == 0) {
            articleEntry.is_vote = 1 + "";
            likeImage.setImageResource(R.drawable.ch_bottom_emoji_favor_pressed);
            likeText.setVisibility(VISIBLE);
            if (!TextUtils.isEmpty(likeText.getText().toString())) {
                String trim = likeText.getText().toString().trim();
                if (trim.equals("999+") || trim.equals("999")) {
                    //什么也不干
                    likeText.setText("999+");
                } else {
                    int i = Integer.parseInt(trim) + 1;
                    likeText.setText("" + (getTextValue(i)));
                }
            } else {
                likeText.setText("1");
            }
        }

        if (position != -1 && h5 == 0) {
            BBSNotifyPraiseData event = new BBSNotifyPraiseData();
            event.setPosition(position);
            EventBus.getDefault().post(event);
        }
        if (h5 != 1111) {
            UpdatePraiseEntry updatePraiseEntry = new UpdatePraiseEntry();
            updatePraiseEntry.setCommentID(commentID + "");
            updatePraiseEntry.setCommentTarget(mCommentUrlTarget);
            EventBus.getDefault().post(updatePraiseEntry);
        }
    }

    public void timesComment(TimesEntry timesEntry, final RiffEffectImageButton imageCollect) {
        if (timesEntry == null) {
            LogUtil.errorLog("timesEntry为空,请重试！");
//            CHToast.show(getContext(), "timesEntry为空,请重试！");
            return;
        }
        CommentTimesLogic commentTimesLogic = new CommentTimesLogic(timesEntry);
        commentTimesLogic.getCommentTimes(new BaseLogic.AppLogicListner() {
            @Override
            public void failed(String errorMsg) {
                if (getContext() instanceof Activity) {
                    if (((Activity) getContext()).isFinishing()) {
                        return;
                    }
                }
                CHToast.show(getContext(), errorMsg);
            }

            @Override
            public void success(Object entryResult) {
                if (getContext() instanceof Activity) {
                    if (((Activity) getContext()).isFinishing()) {
                        return;
                    }
                }
                if (entryResult != null) {
                    mEntry = (TimesPraiseEntry) entryResult;
                    String comment_times = mEntry.getComment_times();
                    if (!TextUtils.isEmpty(comment_times)) {
                        if (comment_times.equals("999+") || comment_times.equals("999")) {
                            //什么也不干
                            recmText.setVisibility(VISIBLE);
                            recmText.setText("999+");
                        } else {
                            int commentTimes = Integer.parseInt(comment_times);
                            if (commentTimes > 0) {
                                recmText.setVisibility(VISIBLE);
                                recmText.setText("" + (getTextValue(commentTimes)));
                            }
                        }

                    }
                    String praise_times = mEntry.getPraise_times();
                    if (!TextUtils.isEmpty(praise_times)) {
                        if (praise_times.equals("999+") || praise_times.equals("999")) {
                            //什么也不干
                            likeText.setVisibility(VISIBLE);
                            likeText.setText("999+");
                        } else {
                            int praiseTimes = Integer.parseInt(praise_times);
                            if (praiseTimes > 0) {
                                likeText.setVisibility(VISIBLE);
                                likeText.setText("" + (getTextValue(praiseTimes)));
                            }
                        }

                    }
                    if ("1".equals(mEntry.getIs_praise())) {
                        likeImage.setImageResource(R.drawable.ch_bottom_emoji_favor_pressed);
                    }

                    //文章收藏
                    if (imageCollect != null) {
                        final String temp;
                        if ("1".equals(mEntry.getIs_collect())) {
                            temp = "2";
                            imageCollect.setImageResource(R.drawable.ch_proxy_collect_selector);
                        } else {
                            temp = "1";
                            imageCollect.setImageResource(R.drawable.ch_proxy_collect_selector_n);
                        }

                        imageCollect.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!AppContext.getAppContext().isLogin()) {
                                    AppContext.getAppContext().login((Activity) getContext(), new TransmitDataInterface() {
                                        @Override
                                        public void transmit(Object o) {

                                        }
                                    });
                                    return;
                                }
                                AnalyticsHome.umOnEvent(AnalyticsHome.FIND_XIANG_COLLECT, "文章详情页收藏点击");
                                ArticleCollectLogic logic = new ArticleCollectLogic();
                                logic.articleCollect(mEntry.getArticle_id(), temp, new BaseLogic.LogicListener() {
                                    @Override
                                    public void failed(String errorMsg) {
                                        CHToast.show(getContext(), errorMsg);
                                    }

                                    @Override
                                    public void success(String... result) {
                                        if ("0".equals(mEntry.getIs_collect())) {
                                            mEntry.setIs_collect("1");
                                            CHToast.show(getContext(), "收藏成功！");
                                            imageCollect.setImageResource(R.drawable.ch_proxy_collect_selector);
                                        } else {
                                            mEntry.setIs_collect("0");
                                            CHToast.show(getContext(), "取消收藏成功！");
                                            imageCollect.setImageResource(R.drawable.ch_proxy_collect_selector_n);
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * 出现验证码
     */
    private void showVerificationCodeDialog() {
        VerificationCodeAlertDialog dialog = new VerificationCodeAlertDialog(getContext());
        dialog.setVerificationCallback(new VerificationCodeAlertDialog.VerificationCallback() {
            @Override
            public void onSuccess() {
                String readCity = DataStorage.getLocationCity(context);
                LogUtil.errorLog("readCity --- " + readCity);
                commentLogic(readCity, "1", mCommentEntry);  // 此时为"1" 通过验证码
            }

            @Override
            public void onFailure() {

            }
        });
        dialog.showVerificationCodeDialog();
    }


    /**
     * 隐藏表情选择框
     */
    public boolean hideFaceView() {
        // 隐藏表情选择框
        if (view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    /**
     * 初始化显示表情的viewpager
     */
    private void init_viewPager() {

        // 左侧添加空页
        View nullView1 = new View(context);
        // 设置透明背景
        nullView1.setBackgroundColor(Color.TRANSPARENT);
        pageViews.add(nullView1);

        // 中间添加表情页

        faceAdapters = new ArrayList<FaceAdapter>();
        for (int i = 0; i < emojis.size(); i++) {
            GridView view = new GridView(context);
            FaceAdapter adapter = new FaceAdapter(context, emojis.get(i));
            view.setAdapter(adapter);
            faceAdapters.add(adapter);
            view.setOnItemClickListener(this);
            view.setNumColumns(7);
            view.setBackgroundColor(Color.TRANSPARENT);
            view.setHorizontalSpacing(1);
            view.setVerticalSpacing(1);
            view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
            view.setCacheColorHint(0);
            view.setPadding(5, 0, 5, 0);
            view.setSelector(new ColorDrawable(Color.TRANSPARENT));
            view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT));
            view.setGravity(Gravity.CENTER);
            pageViews.add(view);
        }

        // 右侧添加空页面
        View nullView2 = new View(context);
        // 设置透明背景
        nullView2.setBackgroundColor(Color.TRANSPARENT);
        pageViews.add(nullView2);
    }

    /**
     * 初始化游标
     */
    private void init_Point() {

        pointViews = new ArrayList<ImageView>();
        ImageView imageView;
        for (int i = 0; i < pageViews.size(); i++) {
            imageView = new ImageView(context);
            imageView.setBackgroundResource(R.drawable.ch_emoji_d1);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 10;
            layoutParams.rightMargin = 10;
            layoutParams.width = 8;
            layoutParams.height = 8;
            layout_point.addView(imageView, layoutParams);
            if (i == 0 || i == pageViews.size() - 1) {
                imageView.setVisibility(View.GONE);
            }
            if (i == 1) {
                imageView.setBackgroundResource(R.drawable.ch_emoji_d2);
            }
            pointViews.add(imageView);

        }
    }

    /**
     * 填充数据
     */
    private void init_Data() {

        mAdapter.notifyDataSetChanged();
        vp_face.setCurrentItem(1);
        current = 0;

        vp_face.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                current = arg0 - 1;
                // 描绘分页点
                draw_Point(arg0);
                // 如果是第一屏或者是最后一屏禁止滑动，其实这里实现的是如果滑动的是第一屏则跳转至第二屏，如果是最后一屏则跳转到倒数第二屏.
                if (arg0 == pointViews.size() - 1 || arg0 == 0) {
                    if (arg0 == 0) {
                        vp_face.setCurrentItem(arg0 + 1);// 第二屏 会再次实现该回调方法实现跳转.
                        pointViews.get(1).setBackgroundResource(R.drawable.ch_emoji_d2);
                    } else {
                        vp_face.setCurrentItem(arg0 - 1);// 倒数第二屏
                        pointViews.get(arg0 - 1).setBackgroundResource(
                                R.drawable.ch_emoji_d2);
                    }
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

    }

    /**
     * 绘制游标背景
     */
    public void draw_Point(int index) {
        for (int i = 1; i < pointViews.size(); i++) {
            if (index == i) {
                pointViews.get(i).setBackgroundResource(R.drawable.ch_emoji_d2);
            } else {
                pointViews.get(i).setBackgroundResource(R.drawable.ch_emoji_d1);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        ChatEmojiEntry emoji = (ChatEmojiEntry) faceAdapters.get(current).getItem(arg2);
        if (emoji.getId() == R.drawable.ch_emoji_face_del_icon) {
            int selection = et_sendmessage.getSelectionStart();
            String text = et_sendmessage.getText().toString();
            if (selection > 0) {
                String text2 = text.substring(selection - 1);
                if ("]".equals(text2)) {
                    int start = text.lastIndexOf("[");
                    int end = selection;
                    et_sendmessage.getText().delete(start, end);
                    return;
                }
                et_sendmessage.getText().delete(selection - 1, selection);
            }
        }
        if (!TextUtils.isEmpty(emoji.getCharacter())) {
            SpannableString spannableString = FaceConversionUtil.getInstance()
                    .addFace(getContext(), emoji.getId(), emoji.getCharacter());
            et_sendmessage.append(spannableString);
        }

    }

    /**
     * 隐藏或者显示输入框
     */
    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            /**
             *这堆数值是算我的下边输入区域的布局的，
             * 规避点击输入区域也会隐藏输入区域
             */
            v.getLocationInWindow(leftTop);
            int left = leftTop[0] - 50;
            int top = leftTop[1] - 50;
            int bottom = top + v.getHeight() + 300;
            int right = left + v.getWidth() + 120;
            if (event.getY() > top) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public void showContent() {
        commentLayout.setVisibility(GONE);
        rlInput.setVisibility(VISIBLE);
        showSoftKey();
    }

    public void showSoftKey() {
        et_sendmessage.requestFocus();
        InputMethodManager imm = (InputMethodManager)
                getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        view.setVisibility(GONE);
        isShowContent = false;
        isShowSoft = true;
    }

    public void hideSoftKey() {
        InputMethodManager imm = (InputMethodManager)
                getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_sendmessage.getWindowToken(), 0);
    }

    public void hideContent() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isCommentDetail) {
                    commentLayout.setVisibility(VISIBLE);
                } else {
                    commentLayout.setVisibility(GONE);
                }
                rlInput.setVisibility(GONE);
                view.setVisibility(GONE);
            }
        }, 100);
    }

    public boolean getShowContent() {
        return isShowContent;
    }

    public void hideFaceLayout() {
        view.setVisibility(GONE);
        rlInput.setVisibility(GONE);
        if (!isCommentDetail) {
            commentLayout.setVisibility(VISIBLE);
        } else {
            commentLayout.setVisibility(GONE);
        }
        LogUtil.errorLog("isCommentDetail  =  " + isCommentDetail);
        isShowContent = false;
        isShowSoft = false;
    }

    public void release() {
        EventBus.getDefault().unregister(this);
    }

    /**
     * 分享
     */
    private void showShare() {
        if (forumShareEntry != null) {
            new CHShareLayout(((Activity) getContext()), forumShareEntry).show();
        } else {
            new CHShareLayout(((Activity) getContext()), mEntry).show();
        }
    }

    public void setTimesEntry(TimesEntry timesEntry) {
        this.timesEntry = timesEntry;
    }

    /**
     * @param commentID 评论id
     */
    public void setCommentID(String commentID) {
        this.mCommentID = commentID;
    }

    public void setCommentEntryForJs(String cid, String ct, String ctid, String cgt) {
        mCommentEntry = new CommentEntry();
        mCommentEntry.setCommentID(cid);
        mCommentEntry.setCommentType(ct);
        mCommentEntry.setCommentTypeId(ctid);
        mCommentEntry.setCommentGameType(cgt);
    }

    /**
     * @param entryResult
     * @param isVerify    验证码
     */
    private void commentLogic(final String entryResult, String isVerify, CommentEntry commentEntry) {

        CommentResponse commentResponse = new CommentResponse(getContext(), et_sendmessage, mCommentID);
        commentResponse.commentResponseLogic(entryResult, isVerify, commentEntry, timesEntry);
        commentResponse.setCallback(new CommentResponse.CommentResponseCallback() {
            @Override
            public void onSuccess(String... entryResult) {
                if (recmText.getVisibility() != VISIBLE) {
                    recmText.setVisibility(VISIBLE);
                    recmText.setText("1");
                } else {
                    String trim = recmText.getText().toString().trim();
                    if (!TextUtils.isEmpty(trim) && (trim.equals("999+") || trim.equals("999"))) {
                        //什么也不干
                        recmText.setVisibility(VISIBLE);
                        recmText.setText("999+");
                    } else {
                        if (!TextUtils.isEmpty(trim)) {
                            int i = Integer.parseInt(trim) + 1;
                            recmText.setText("" + (getTextValue(i)));
                        }
                    }
                }

                if (entryResult != null && !TextUtils.isEmpty(entryResult[0])) {
                    LogUtil.errorLog("commentID = " + mCommentID);
                    //在自己评论的时候是 "0" ,　isCommentDetail 是在评论的评论 都要刷新界面，调用js
                    if ("0".equals(mCommentID) || isCommentDetail) {
                        String comment_id = entryResult[0];
                        UpdateCommentEntry updateCommentEntry = new UpdateCommentEntry();
                        updateCommentEntry.setCommentID(comment_id);
                        updateCommentEntry.setCommentTarget(mCommentUrlTarget);
                        EventBus.getDefault().post(updateCommentEntry);
                        mCommentID = null;
                    }
                }
            }

            @Override
            public void onFailure(String errorMsg, int errorCode) {
                showVerificationCodeDialog();
            }
        });
    }

    private void forumCommentLogic(String entryResult, String isVerify) {
        final LoadingDialog loadingDialog = new LoadingDialog(getContext(), "");
        loadingDialog.show();
        if (!TextUtils.isEmpty(mCommentID) && !"0".equals(mCommentID)) {
            ArticleAddReplyLogic logic = new ArticleAddReplyLogic();
            logic.addReply(entryResult, mCommentID, new BaseLogic.LogicListener() {
                @Override
                public void failed(String errorMsg) {
                    loadingDialog.dismiss();
                    CHToast.show(getContext(), errorMsg);
                }

                @Override
                public void success(String... result) {
                    loadingDialog.dismiss();
                    CHToast.show(getContext(), "回复成功！");
                    if (recmText.getVisibility() != VISIBLE) {
                        recmText.setVisibility(VISIBLE);
                        recmText.setText("1");
                    } else {
                        String trim = recmText.getText().toString().trim();
                        if (!TextUtils.isEmpty(trim)) {
                            if (trim.equals("999+") || trim.equals("999")) {
                                recmText.setText("999+");
                            } else {
                                int i = Integer.parseInt(trim) + 1;
                                recmText.setText("" + (getTextValue(i)));
                            }
                        }
                    }
                    if (position != -1) {
                        BBSNotifyCommentData event = new BBSNotifyCommentData();
                        event.setPosition(position);
                        EventBus.getDefault().post(event);
                    }
                    et_sendmessage.setText("");
                    if (result != null && !TextUtils.isEmpty(result[0])) {
                        LogUtil.errorLog("commentID = " + mCommentID);
                        //在自己评论的时候是 "0" ,　isCommentDetail 是在评论的评论 都要刷新界面，调用js
//                        if (!"0".equals(mCommentID) || isCommentDetail) {
                        String comment_id = result[0];
                        UpdateCommentEntry updateCommentEntry = new UpdateCommentEntry();
                        updateCommentEntry.setCommentID(mCommentID);
                        updateCommentEntry.setCommentTarget(mCommentUrlTarget);
                        EventBus.getDefault().post(updateCommentEntry);
//                            mCommentID = null;
//                        }
                    }
                }
            });
        } else {
            ArticleAddCommentLogic commentLogic = new ArticleAddCommentLogic();
            commentLogic.addComment(articleId, entryResult, new BaseLogic.LogicListener() {
                @Override
                public void failed(String errorMsg) {
                    CHToast.show(getContext(), errorMsg);
                    loadingDialog.dismiss();
                }

                @Override
                public void success(String... result) {
                    loadingDialog.dismiss();
                    CHToast.show(getContext(), "评论成功！");
                    if (recmText.getVisibility() != VISIBLE) {
                        recmText.setVisibility(VISIBLE);
                        recmText.setText("1");
                    } else {
                        String trim = recmText.getText().toString().trim();
                        if (!TextUtils.isEmpty(trim)) {
                            if (trim.equals("999+") || trim.equals("999")) {
                                recmText.setText("999+");
                            } else {
                                int i = Integer.parseInt(trim) + 1;
                                recmText.setText("" + (getTextValue(i)));
                            }
                        }
                    }
                    if (position != -1) {
                        BBSNotifyCommentData event = new BBSNotifyCommentData();
                        event.setPosition(position);
                        EventBus.getDefault().post(event);
                    }
                    et_sendmessage.setText("");
                    if (result != null && !TextUtils.isEmpty(result[0])) {
                        LogUtil.errorLog("commentID = " + mCommentID);
                        //在自己评论的时候是 "0" ,　isCommentDetail 是在评论的评论 都要刷新界面，调用js
//                        if (!"0".equals(mCommentID) || isCommentDetail) {
                        String comment_id = result[0];
                        UpdateCommentEntry updateCommentEntry = new UpdateCommentEntry();
                        updateCommentEntry.setCommentID("0");
                        updateCommentEntry.setCommentTarget(mCommentUrlTarget);
                        EventBus.getDefault().post(updateCommentEntry);
//                            mCommentID = null;
//                        }
                    }
                }
            });
        }
    }

    private boolean isCommentDetail; //判断是评论的评论  true表示 是 主要隐藏输入框

    public void setCommentDetail(boolean commentDetail) {
        isCommentDetail = commentDetail;
    }

    private long lastClickTime;

    private boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 1000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 论坛
     *
     * @param articleId
     * @param articleEntry
     */
    public void forumCommentAndPraise(ReadArticleEntry articleEntry, String articleId) {
        this.articleEntry = articleEntry;
        if (!TextUtils.isEmpty(articleId)) {
            this.articleId = articleId;
        }
        String comment_times = articleEntry.comment_total;
        if (!TextUtils.isEmpty(comment_times)) {
            if (comment_times.equals("999+") || comment_times.equals("999")) {
                recmText.setVisibility(VISIBLE);
                recmText.setText("999+");
            } else {
                int commentTimes = Integer.parseInt(comment_times);
                if (commentTimes > 0) {
                    recmText.setVisibility(VISIBLE);
                    recmText.setText("" + (getTextValue(commentTimes)));
                }
            }

        }
        String praise_times = articleEntry.upvote_total;
        if (!TextUtils.isEmpty(praise_times)) {
            if (praise_times.equals("999+") || praise_times.equals("999")) {
                likeText.setVisibility(VISIBLE);
                likeText.setText("999+");
            } else {
                int praiseTimes = Integer.parseInt(praise_times);
                if (praiseTimes > 0) {
                    likeText.setVisibility(VISIBLE);
                    likeText.setText("" + (getTextValue(praiseTimes)));
                }
            }

        }
        if ("1".equals(articleEntry.is_vote)) {
            likeImage.setImageResource(R.drawable.ch_bottom_emoji_favor_pressed);
        }
    }

    public void setForumShareEntry(ForumShareEntry forumShareEntry, int position) {
        this.forumShareEntry = forumShareEntry;
        this.position = position;
    }
}
