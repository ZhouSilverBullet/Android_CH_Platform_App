package com.caohua.games.ui.download;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ClipDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.chsdk.ui.widget.CircleBtnHelper;

/**
 * Created by ZengLei on 2016/10/25.
 */

public class DownloadProgressButton extends LinearLayout implements IDownloadView {

//    private TextView tv;   //进度条
    private CircleBtnHelper mHelper = null;
    private ClipDrawable clipDrawable;
    private TextView downloading;

    public DownloadProgressButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(getContext()).inflate(R.layout.ch_download_tv_btn, this, true);

        mHelper = new CircleBtnHelper(this,false);
        mHelper.handleBeforeBackAlpha();
        mHelper.resetColor(0x000000, 0x000000); //original - 0xFFE9EDF2 , 0xFFD9E0E7
        mHelper.resetAlpha(26, 0, 7, 26); //original - 255,0,70,255
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

//        tv = (TextView) findViewById(R.id.ch_download_btn_tv);
        setGravity(Gravity.CENTER);
        downloading = ((TextView) findViewById(R.id.ch_downloading_tv));
        clipDrawable = (ClipDrawable)getResources().getDrawable(R.drawable.ch_progress_selector);
        setBackgroundDrawable(clipDrawable);
        downloading.setText("下载");

    }
    public void setDownloadingText(String text){
        downloading.setText(text);
    }

    public void setText(String text) {
//        tv.setText(text);
    }

    public void setProgress(int progress) {
        clipDrawable.setAlpha(255);
//        tv.setText(progress + "%");
        clipDrawable.setLevel(100*progress);
        if (TextUtils.isEmpty(downloading.getText())&&clipDrawable.getLevel()==0) {
            downloading.setText("下载");
        }
        if (TextUtils.isEmpty(downloading.getText())&&clipDrawable.getLevel()>0) {
            downloading.setText("继续下载");
        }
    }

    public void setPause() {
//        clipDrawable.setAlpha(40);
        downloading.setText("继续下载");
    }

    public void setError() {
        downloading.setText("下载失败，请重试");
    }

    public void setDownloading() {
        setDownloadingText("下载中");
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

    public void setClipDrawableProgress(int level){
        clipDrawable.setLevel(level);
    }
}
