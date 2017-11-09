package com.caohua.games.ui.download;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.chsdk.ui.widget.CircleBtnHelper;

/**
 * Created by ZengLei on 2016/10/25.
 */

public class DownloadButton extends LinearLayout implements IDownloadView {

    private TextView tv;
    private ImageView img;
    private CircleBtnHelper mHelper = null;

    public DownloadButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(getContext()).inflate(R.layout.ch_download_btn, this, true);

        mHelper = new CircleBtnHelper(this,false);
        mHelper.handleBeforeBackAlpha();
        mHelper.resetColor(0x000000, 0x000000); //original - 0xFFE9EDF2 , 0xFFD9E0E7
        mHelper.resetAlpha(26, 0, 7, 26); //original - 255,0,70,255
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        tv = (TextView) findViewById(R.id.ch_download_btn_tv);
        img = (ImageView) findViewById(R.id.ch_download_btn_img);
    }

    @Override
    public void setDownloadingText(String text) {
        tv.setText(text);
        img.setVisibility(View.GONE);
//        setBackgroundResource(R.drawable.ch_blue_bg);
        setBackgroundResource(R.drawable.download_button_shape_normal);
    }

    @Override
    public void setProgress(int progress) {
        tv.setText(progress + "%");
        img.setVisibility(View.VISIBLE);
//        setBackgroundResource(R.drawable.ch_blue_bg);
        setBackgroundResource(R.drawable.download_button_shape_downloading);
    }

    @Override
    public void setPause() {
        img.setVisibility(View.VISIBLE);
        img.setImageResource(R.drawable.ch_download_puase);
//        setBackgroundResource(R.drawable.ch_red_bg);
        setBackgroundResource(R.drawable.download_button_shape_pause);
    }

    public void setError() {
        img.setVisibility(View.VISIBLE);
        img.setImageResource(R.drawable.ch_download_error);
//        setBackgroundResource(R.drawable.ch_gray_bg);
        setBackgroundResource(R.drawable.download_button_shape_error);
    }

    @Override
    public void setDownloading() {
        img.setVisibility(View.VISIBLE);
        img.setImageResource(R.drawable.ch_download_restart);
//        setBackgroundResource(R.drawable.ch_blue_bg);
        setBackgroundResource(R.drawable.download_button_shape_downloading);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if(h > 0 && w > 0 && mHelper != null){
            mHelper.resetInitData(h,w);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = super.onTouchEvent(event);
        if(result){
            mHelper.handleClick(event);
        }
        return result;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        mHelper.setOnClickListener(l);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isInEditMode()){
            return;
        }
        super.onDraw(canvas);
        mHelper.onDraw(canvas);
    }
}
