package com.caohua.games.ui.coupon;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.caohua.games.R;
import com.caohua.games.biz.coupon.ColorEggSearchLogic;
import com.caohua.games.biz.coupon.EggSearchEntry;
import com.caohua.games.biz.coupon.FlyingCardSuccessDialog;
import com.caohua.games.ui.BaseActivity;
import com.chsdk.biz.BaseLogic;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.LoadingDialog;
import com.chsdk.utils.LogUtil;
import com.chsdk.utils.ViewUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by admin on 2017/8/22.
 */

public class FlyingCardActivity extends BaseActivity {
    private List<ImageView> viewList = new ArrayList<>();
    private List<Float> viewY = new ArrayList<>();
    private List<Float> viewX = new ArrayList<>();

    private static final int NUMBER = 20;
    private EditText cardEdit;
    private ImageView cardSearch;
    private RelativeLayout cardContainer;
    private ImageView imageView;
    private int w;
    private RelativeLayout.LayoutParams lp;
    private int width;
    private int height;

    private FlyingCardActivity activity;
    private EggSearchEntry entry;
    private View cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch_activity_flying_card);
        activity = FlyingCardActivity.this;
        initView();
        initData();
    }

    private void initData() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        width = dm.widthPixels;
        height = dm.heightPixels;
        cardSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
        cardContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cancel = getView(R.id.ch_activity_flying_card_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStartEggCard) {
                    CHToast.show(activity, "正在显示彩蛋");
                    return;
                }
                finish();
            }
        });
        cardEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) v
                            .getContext().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(
                                v.getApplicationWindowToken(), 0);
                    }
                    search();
                    return true;
                }
                return false;
            }
        });
    }

    private void search() {
        String value = cardEdit.getText().toString().trim();
        if (TextUtils.isEmpty(value)) {
            CHToast.show(this, "不能为空，请输入关键字！");
            return;
        }

        final LoadingDialog dialog = new LoadingDialog(this, true);
        dialog.show();
        new ColorEggSearchLogic().eggSearch(value, new BaseLogic.DataLogicListner<EggSearchEntry>() {

            @Override
            public void failed(String errorMsg, int errorCode) {
                if (isFinishing()) {
                    return;
                }
                CHToast.show(activity, errorMsg);
//                if (errorCode == 605) {
//                    showPowerDialog();
//                    dialog.dismiss();
//                    return;
//                }
                dialog.dismiss();
            }

            @Override
            public void success(EggSearchEntry entry) {
                if (isFinishing()) {
                    return;
                }

                if (entry != null) {
                    FlyingCardActivity.this.entry = entry;

                    if (entry.getData() != null) {
                        if (entry.getData().getUrl() != null) {
                            showPowerDialog();
                            dialog.dismiss();
                            return;
                        }
                        isShow = false;
                        final String actImg = entry.getData().getAct_img();
                        if (!TextUtils.isEmpty(actImg)) {
                            Glide.with(activity).load(actImg).asBitmap()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    dialog.dismiss();
                                    isShow = true;
                                    LogUtil.errorLog("FlyingCardActivity resource: " + resource);
                                    if (resource != null) {
                                        for (int i = 0; i < NUMBER; i++) {
                                            addView(0, resource);
                                        }
                                    } else {
                                        for (int i = 0; i < NUMBER; i++) {
                                            addView(R.mipmap.ic_launcher, null);
                                        }
                                    }
                                    startEggCard();
                                }
                            });
                            cardSearch.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (isFinishing()) {
                                        return;
                                    }

                                    if (!isShow) {
                                        if (dialog != null) {
                                            dialog.dismiss();
                                            CHToast.show(activity, "该图片可能存在！");
                                        }
                                    }
                                }
                            }, 6000);
                        } else {
                            dialog.dismiss();
                            for (int i = 0; i < NUMBER; i++) {
                                addView(R.mipmap.ic_launcher, null);
                            }
                            startEggCard();
                        }
                    } else {
                        dialog.dismiss();
                        for (int i = 0; i < NUMBER; i++) {
                            addView(R.mipmap.ic_launcher, null);
                        }
                        startEggCard();
                    }
                }
            }
        });
    }

    private void showPowerDialog() {
        EggSearchEntry.DataBean data = entry.getData();
        if (data != null) {
            final FlyingCardSuccessDialog successDialog = new FlyingCardSuccessDialog(activity);
            successDialog.show();
            final String url = data.getUrl();
            String cancel = "关闭";
            String ok = "如何满足要求？";
            if (TextUtils.isEmpty(url)) {
                successDialog.cancelToGone();
                ok = "我知道了";
            }
            successDialog.setContent(entry.getMsg());
            successDialog.setTitle("提示！");
            AlertDialog dialog = successDialog.getDialog();
            dialog.setCanceledOnTouchOutside(true);
            dialog.setCancelable(true);
            successDialog.setOkButton(ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    successDialog.dismiss();
                    if (TextUtils.isEmpty(url)) {
                        return;
                    }
                    WebActivity.startWebPage(activity, url);
                    FlyingCardActivity.this.finish();
                }
            });

            successDialog.setCancelButton(cancel, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    successDialog.dismiss();
                }
            });
        }
    }

    private boolean isStartEggCard;

    @Override
    public void onBackPressed() {
        if (isStartEggCard) {
            CHToast.show(activity, "正在显示彩蛋");
        } else {
            super.onBackPressed();
        }
    }

    private void startEggCard() {
        for (int i = 0; i < NUMBER; i++) {
            if (i <= 20) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(viewList.get(i), "translationY", viewY.get(i), height);
                animator.setDuration(new Random().nextInt(2000) + 4000);
                animator.start();
                if (i == 0) {
                    isStartEggCard = true;
                    cardEdit.setFocusable(false);
                    cardEdit.setFocusableInTouchMode(false);
                    cardSearch.setClickable(false);
                    cardContainer.setClickable(false);
                } else {
                    if(i == 19) {
                        animator.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                isStartEggCard = false;
                                cardEdit.setFocusable(true);
                                cardEdit.setFocusableInTouchMode(true);
                                cardSearch.setClickable(true);
                                cardContainer.setClickable(true);
                                showEggSuccessDialog();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {
                            }
                        });
                    }
                }
            } else if (i <= 15) {
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(viewList.get(i), "translationY", viewY.get(i), height);
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(viewList.get(i), "translationX", viewX.get(i), width * 3 / 4);
                AnimatorSet set = new AnimatorSet();
                set.setDuration(new Random().nextInt(2000) + 4000);
                set.playTogether(animator1, animator2);
                set.start();
            } else {
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(viewList.get(i), "translationY", viewY.get(i), height);
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(viewList.get(i), "translationX", viewX.get(i), width / 4);
                AnimatorSet set = new AnimatorSet();
                set.setDuration(new Random().nextInt(2000) + 4000);
                set.playTogether(animator1, animator2);
                set.start();
                if (i == 19) {
                    set.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            isStartEggCard = false;
                            cardEdit.setFocusable(true);
                            cardEdit.setFocusableInTouchMode(true);
                            cardSearch.setClickable(true);
                            cardContainer.setClickable(true);
                            showEggSuccessDialog();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    });
                }
            }
        }
    }

    private boolean isShow;

    private void showEggSuccessDialog() {
        isShow = false;
        final LoadingDialog dialog = new LoadingDialog(activity, true);
        dialog.show();
        String success_img = "https://cdn-sdk.caohua.com/www/img/shouyou.jpg";
        if (entry.getData() != null) {
            if (!TextUtils.isEmpty(entry.getData().getSuccess_img())) {
                success_img = entry.getData().getSuccess_img();
            }
            cardSearch.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isFinishing()) {
                        return;
                    }

                    if (!isShow) {
                        if (dialog != null) {
                            dialog.dismiss();
                            CHToast.show(activity, "该图片可能存在！");
                        }
                    }
                }
            }, 6000);
            Glide.with(activity).load(success_img).asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    dialog.dismiss();
                    isShow = true;
                    if (isFinishing()) {
                        return;
                    }
                    LogUtil.errorLog("FlyingCardActivity resource: " + resource);
                    new FlyingCardDialog(activity, R.drawable.ch_default_apk_icon, entry.getData().getId(), resource).show();
                }
            });
        }
    }

    private void addView(int defaultIcon, Bitmap actImgBitmap) {
        imageView = new ImageView(this);
        if (defaultIcon != 0) {
            imageView.setImageResource(defaultIcon);
        } else {
            imageView.setImageBitmap(actImgBitmap);
        }
        imageView.setX(new Random().nextInt(width - 150) + 50);
        imageView.setY(-(new Random().nextInt(440) + 90));
        viewList.add(imageView);
        viewY.add(imageView.getY());
        viewX.add(imageView.getX());
        w = ViewUtil.dp2px(this, new Random().nextInt(15) + 35);
        lp = new RelativeLayout.LayoutParams(w, w);
        cardContainer.addView(imageView, lp);
    }

    private void initView() {
        cardEdit = getView(R.id.ch_activity_flying_card_edit);
        cardSearch = getView(R.id.ch_activity_flying_card_search);
        cardContainer = getView(R.id.ch_activity_flying_card_container);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, FlyingCardActivity.class);
        context.startActivity(intent);
    }
}
