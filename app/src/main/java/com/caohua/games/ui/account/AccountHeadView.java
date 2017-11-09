package com.caohua.games.ui.account;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.caohua.games.R;
import com.chsdk.utils.PicUtil;
import com.chsdk.utils.ViewUtil;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by zhouzhou on 2017/6/13.
 */

public class AccountHeadView extends FrameLayout {
    private CircleImageView accountImage;
    private ImageView accountHeadBg;
    private int imageWidth;
    private boolean imageClickBg;

    public AccountHeadView(Context context) {
        this(context, null);
    }

    public AccountHeadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AccountHeadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.AccountHeadView, defStyleAttr, 0);
        imageWidth = (int) a.getDimension(R.styleable.AccountHeadView_imageWidth, getDp2px(70));
        imageClickBg = a.getBoolean(R.styleable.AccountHeadView_imageClickBg, true);
        a.recycle();
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.ch_account_head_layout, this, true);
        accountImage = ((CircleImageView) findViewById(R.id.ch_account_head_icon));
        accountHeadBg = ((ImageView) findViewById(R.id.ch_account_head_bg));
        LayoutParams params = getParams(imageWidth);
        accountImage.setLayoutParams(params);
        LayoutParams params1 = getParams(imageWidth + getDp2px(5));
        accountHeadBg.setLayoutParams(params1);
        //设置图片点击效果
        if (imageClickBg)
            ViewUtil.setImageViewColorFilter(accountImage);
    }

    public void setAccountImage(String imgUrl, boolean init) {
        if (init) {
            accountImage.setImageResource(R.drawable.ch_account);
            return;
        }
        PicUtil.displayImg(getContext(), accountImage, imgUrl, R.drawable.ch_account);
    }

    public void setAccountImage(String imgUrl, boolean init, int defaultImage) {
        if (init) {
            accountImage.setImageResource(defaultImage);
            return;
        }
        PicUtil.displayImg(getContext(), accountImage, imgUrl, defaultImage);
    }


    private int getDp2px(int value) {
        return ViewUtil.dp2px(getContext(), value);
    }

    public void setAccountHeadBg(String img_mask) {
        LayoutParams params;
        switch (img_mask) {
            case "1":
                params = getParams(imageWidth + getDp2px(30));
                accountHeadBg.setImageResource(R.drawable.ch_level_copper);
                break;
            case "2":
                params = getParams(imageWidth + getDp2px(30));
                accountHeadBg.setImageResource(R.drawable.ch_level_silver);
                break;
            case "3":
                params = getParams(imageWidth + getDp2px(30));
                accountHeadBg.setImageResource(R.drawable.ch_level_gold);
                break;
            default:
                params = getParams(imageWidth + getDp2px(5));
                accountHeadBg.setImageResource(R.drawable.ch_account_circle_bg);
                break;
        }
        accountHeadBg.setLayoutParams(params);
    }

    public void setAccountWidthHeadBg(String img_mask, int width) {
        if (TextUtils.isEmpty(img_mask)) {
            img_mask = "0";
        }
        LayoutParams params;
        switch (img_mask) {
            case "1":
                params = getParams(imageWidth + getDp2px(width));
                accountHeadBg.setImageResource(R.drawable.ch_level_copper);
                break;
            case "2":
                params = getParams(imageWidth + getDp2px(width));
                accountHeadBg.setImageResource(R.drawable.ch_level_silver);
                break;
            case "3":
                params = getParams(imageWidth + getDp2px(width));
                accountHeadBg.setImageResource(R.drawable.ch_level_gold);
                break;
            default:
                params = getParams(imageWidth + getDp2px(width));
                accountHeadBg.setImageResource(R.drawable.ch_account_circle_bg);
                break;
        }
        accountHeadBg.setLayoutParams(params);
    }

    private LayoutParams getParams(int imageWidth) {
        LayoutParams layoutParams = new LayoutParams(imageWidth, imageWidth);
        layoutParams.gravity = Gravity.CENTER;
        return layoutParams;
    }


    public CircleImageView getAccountImage() {
        return accountImage;
    }
}
