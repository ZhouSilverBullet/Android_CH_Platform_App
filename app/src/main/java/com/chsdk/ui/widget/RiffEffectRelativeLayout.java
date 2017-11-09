package com.chsdk.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.caohua.games.R;

public class RiffEffectRelativeLayout extends RelativeLayout {

    private CircleBtnHelper mHelper = null;

    public RiffEffectRelativeLayout(Context context){
        super(context);
        init();
    }

    public RiffEffectRelativeLayout(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    public RiffEffectRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        if (isInEditMode()){
            return;
        }
        setClickable(true);
        setBackgroundResource(R.color.ch_white);
        mHelper = new CircleBtnHelper(this,false);
        mHelper.handleBeforeBackAlpha();
        mHelper.resetColor(0x000000, 0x000000); //original - 0xFFE9EDF2 , 0xFFD9E0E7
        mHelper.resetAlpha(26, 0, 7, 26); //original - 255,0,70,255
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
        super.onDraw(canvas);
        mHelper.onDraw(canvas);
    }

}
