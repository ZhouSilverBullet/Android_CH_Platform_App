package com.chsdk.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;

import com.chsdk.ui.widget.CircleBtnHelper;

public class RiffEffectImageButton extends ImageButton {

    private CircleBtnHelper mHelper = null;

    public RiffEffectImageButton(Context context){
        super(context);
        init();
    }

    public RiffEffectImageButton(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    public RiffEffectImageButton(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        if (isInEditMode()){
            return;
        }

        mHelper = new CircleBtnHelper(this, false);
        mHelper.handleBeforeBackAlpha();
        mHelper.resetAlpha(80, 0, 7, 80); //original - 255,0,70,255
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
