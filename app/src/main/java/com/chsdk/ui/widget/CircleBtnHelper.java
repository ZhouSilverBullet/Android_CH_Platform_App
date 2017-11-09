package com.chsdk.ui.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.LinearInterpolator;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;

/**
 * Created by ZengLei on 2016/10/15.
 */
public class CircleBtnHelper {

    private Paint mPaintBackCircle;
    private Paint mPaintMoveCircle;
    private RectF mMyRect = new RectF();

    ValueAnimator mAnimMoveX;
    ValueAnimator mAnimMoveY;
    ValueAnimator mAnimMoveRadius;

    ValueAnimator mAnimMoveAlpha;
    ValueAnimator mAnimBackAlpha;

    private int mMoveAlphaStart = 255;
    private int mMoveAlphaEnd   = 0;
    private int mBackAlphaStart = 70;
    private int mBackAlphaEnd   = 255;

    private int mBackColor = 0xFFE9EDF2;
    private int mMoveColor = 0xFFD9E0E7;

    private float mWidth;
    private float mHeight;

    private float mMoveMaxRadius;
    public float mBackMaxRadius;
    private boolean mIsPlayClicked;
    private InvalidateNotifyHelper mInvalidateNotifyHelper;

    private boolean mIsCircle = true;
    private boolean mPostingAnim = false;

    private static final int MSG_PLAY_CLICKED = 0;
    private static boolean sHandleNextClick = true;
    private boolean mIsMeClick = false;

    private long mMoveSlowCircleDuration = 1200;
    private long mMoveHideSlow = 500;

    private long mMoveFastCircleDuration = 250;
    private long mMoveHideFast = 150;

    private boolean mHandleClickBeforeBackAlpha = false;
    private boolean mFinishAllAnim = true;

    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_PLAY_CLICKED:
                    sHandleNextClick = true;
                    if (null != mOnClickListener) {
                        mOnClickListener.onClick(mView);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private View mView = null;

    public void resetAlpha(int moveAlphaStart,int moveAlphaEnd,int backAlphaStart,int backAlphaEnd){
        mMoveAlphaStart = moveAlphaStart;
        mMoveAlphaEnd   = moveAlphaEnd;
        mBackAlphaStart = backAlphaStart;
        mBackAlphaEnd   = backAlphaEnd;
    }

    public void resetColor(int back,int move){
        mBackColor = back;
        mMoveColor = move;
        if(null != mPaintBackCircle){
            mPaintBackCircle.setColor(mBackColor);
        }
        if(null != mPaintMoveCircle){
            mPaintMoveCircle.setColor(mMoveColor);
        }
    }

    public void setDuration(long fastMove,long fastHide,long slowMove,long slowHide){
        mMoveFastCircleDuration = fastMove;
        mMoveHideFast = fastHide;

        mMoveSlowCircleDuration = slowMove;
        mMoveHideSlow = slowHide;
    }

    public CircleBtnHelper(View v, boolean isCircle){
        mView = v;
        mIsCircle = isCircle;
        init();
    }

    public CircleBtnHelper(View v){
        this(v, true);
    }

    public void setFinishAllAnim(boolean v){
        mFinishAllAnim = v;
    }

    private void init() {
        mPaintBackCircle = new Paint();
        mPaintBackCircle.setColor(mBackColor);
        mPaintBackCircle.setAntiAlias(true);
        mPaintBackCircle.setAlpha(0);

        mPaintMoveCircle = new Paint();
        mPaintMoveCircle.setColor(mMoveColor);
        mPaintMoveCircle.setAntiAlias(true);
        mPaintMoveCircle.setAlpha(0);

        mInvalidateNotifyHelper = new InvalidateNotifyHelper(){
            @Override
            protected void onNeedNotify() {
                super.onNeedNotify();
                mView.invalidate();
            }
        };
    }

    float rectLeft = 0;
    float rectRight = 0;
    float rectTop = 0;
    float rectBottom = 0;
    public void setOrigin(float left, float right, float top, float bottom){
        rectLeft = left;
        rectRight = right;
        rectTop = top;
        rectBottom = bottom;
    }

    public void resetInitData(float height,float width) {
        mHeight = height;
        mWidth = width;
        float duijao = (float) Math.sqrt(Math.abs(mHeight) * Math.abs(mHeight) + Math.abs(mWidth)*Math.abs(mWidth));
        mMoveMaxRadius = mIsCircle ? Math.min(mHeight, mWidth) / 2.2f : duijao/2f;
        mBackMaxRadius = mIsCircle ? Math.min(mHeight, mWidth) / 2.2f : duijao/2f;
        onBackMaxCaled(mBackMaxRadius);
        mMyRect.set(rectLeft, rectRight, mWidth, mHeight);
        mView.invalidate();
    }

    protected void onBackMaxCaled(float backMax){

    }

    public void handleBeforeBackAlpha(){
        mHandleClickBeforeBackAlpha = true;
    }


    private void hideMoveCircle(long duration,long startDelay,final boolean handleClickListener) {
        mAnimMoveAlpha = ValueAnimator.ofInt(mMoveAlphaStart, mMoveAlphaEnd).setDuration(duration);
        mAnimMoveAlpha.setInterpolator(new LinearInterpolator());
        mAnimMoveAlpha.setStartDelay(startDelay);
        mAnimMoveAlpha.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {


            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();
                mPaintMoveCircle.setAlpha(value);
                mInvalidateNotifyHelper.add(false);
            }
        });

        mAnimMoveAlpha.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if(null != mAnimBackAlpha){
                    mAnimBackAlpha.cancel();
                }
                mPaintBackCircle.setAlpha(0);
                mInvalidateNotifyHelper.add(false);
                if (handleClickListener) {
                    mHandler.removeMessages(MSG_PLAY_CLICKED);
                    mHandler.sendEmptyMessageDelayed(MSG_PLAY_CLICKED,mHandleClickBeforeBackAlpha ? 0 : mMoveHideFast);
                }
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mInvalidateNotifyHelper.add(true);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        mAnimMoveAlpha.start();
    }

    Runnable mAnimRunnable = new Runnable() {
        @Override
        public void run() {
            mPostingAnim = false;
            animBackCircle(140);
        }
    };

    private void removeAnimRunnable(){
        mView.removeCallbacks(mAnimRunnable);
    }


    public void handleClick(MotionEvent ev) {
        if(!sHandleNextClick && !mIsMeClick){
            return;
        }
        if(mOnTouchListener != null){
            mOnTouchListener.onTouch(mView, ev);
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsMeClick = true;
                sHandleNextClick = false;
                resetData();
                mCenterX = ev.getX();
                mCenterY = ev.getY();
                if(mPostingAnim){
                    removeAnimRunnable();
                }
                mPostingAnim = true;
                mView.postDelayed(mAnimRunnable, ViewConfiguration.getTapTimeout());
                break;
            case MotionEvent.ACTION_UP:
                if(!mIsPlayClicked){
                    if(mPostingAnim){
                        removeAnimRunnable();
                        mAnimRunnable.run();
                    }
                    mIsPlayClicked = true;
                    handleUp(true);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(!mIsPlayClicked && !mMyRect.contains(ev.getX(),ev.getY())){
                    removeAnimRunnable();
                    mIsPlayClicked = true;
                    handleUp(false);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if(mPostingAnim){
                    removeAnimRunnable();
                }
                if(!mIsPlayClicked){
                    mIsPlayClicked = true;
                    handleUp(false);
                }
                break;
            default:
                break;
        }
    }

    private void resetData() {
        mIsPlayClicked = false;
        mMoveRadius = 0;
        if(null != mPaintBackCircle){
            mPaintBackCircle.setAlpha(0);
        }
        if(null != mPaintMoveCircle){
            mPaintMoveCircle.setAlpha(0);
        }
    }

    float mCenterX;
    float mCenterY;
    float mMoveRadius;
    private void animBackCircle(long duration){
        mAnimBackAlpha = ValueAnimator.ofInt(mBackAlphaStart, mBackAlphaEnd).setDuration(duration);
        mAnimBackAlpha.setInterpolator(new LinearInterpolator());
        mAnimBackAlpha.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();
                mPaintBackCircle.setAlpha(value);
                mInvalidateNotifyHelper.add(false);
            }
        });

        mAnimBackAlpha.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!mIsPlayClicked) {
                    animMoveCircle(mMoveSlowCircleDuration);
                }
                mInvalidateNotifyHelper.add(true);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        mAnimBackAlpha.start();
    }

    private void handleUp(boolean handleClickListener){
        mIsMeClick = false;
        if(!handleClickListener){
            sHandleNextClick = true;
        }
        if(mAnimMoveX != null && mAnimMoveX.isRunning() || mAnimBackAlpha != null && mAnimBackAlpha.isRunning()){
            boolean isMoveAnim = (mAnimMoveX != null  && mAnimMoveX.isRunning());
            if(null != mAnimMoveAlpha){
                mAnimMoveAlpha.cancel();
            }
            if(null != mAnimBackAlpha){
                mAnimBackAlpha.cancel();
            }
            if(null != mAnimMoveX){
                mAnimMoveX.cancel();
            }
            if(null != mAnimMoveY){
                mAnimMoveY.cancel();
            }
            float fraction = 1f;
            if(null != mAnimMoveRadius){
                if(mAnimMoveRadius.isRunning()){
                    fraction = 1 - mAnimMoveRadius.getAnimatedFraction();
                }
                mAnimMoveRadius.cancel();
            }
            if(null != mPaintMoveCircle){
                mPaintMoveCircle.setAlpha(mMoveAlphaStart);
            }
            if(handleClickListener || isMoveAnim || mFinishAllAnim){
                animMoveCircle((long) (mMoveFastCircleDuration * fraction));
            }
            hideMoveCircle(mMoveHideFast, (long)(mMoveFastCircleDuration * fraction), handleClickListener);
        }else{
            if(null != mAnimMoveAlpha){
                mAnimMoveAlpha.cancel();
            }
            if(null != mAnimBackAlpha){
                mAnimBackAlpha.cancel();
            }
            if(null != mPaintBackCircle){
                mPaintBackCircle.setAlpha(0);
                mInvalidateNotifyHelper.add(false);
            }
            hideMoveCircle(mMoveHideSlow, 0, handleClickListener);
        }
    }

    private void animMoveCircle(long duration){
        mPaintMoveCircle.setAlpha(mMoveAlphaStart);
        mAnimMoveX = ValueAnimator.ofFloat(mCenterX, mWidth / 2).setDuration(duration);
        mAnimMoveX.setInterpolator(new LinearInterpolator());
        mAnimMoveX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (Float) valueAnimator.getAnimatedValue();
                mCenterX = value;
            }
        });
        mAnimMoveX.start();

        mAnimMoveY = ValueAnimator.ofFloat(mCenterY, mHeight / 2).setDuration(duration);
        mAnimMoveY.setInterpolator(new LinearInterpolator());
        mAnimMoveY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (Float) valueAnimator.getAnimatedValue();
                mCenterY = value;
            }
        });
        mAnimMoveY.start();

        mAnimMoveRadius = ValueAnimator.ofFloat(mMoveRadius, mMoveMaxRadius).setDuration(duration);
        mAnimMoveRadius.setInterpolator(new LinearInterpolator());
        mAnimMoveRadius.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (Float) valueAnimator.getAnimatedValue();
                mMoveRadius = value;
                mInvalidateNotifyHelper.add(false);
            }
        });
        mAnimMoveRadius.start();
    }


    private View.OnClickListener mOnClickListener;
    public void setOnClickListener(View.OnClickListener l) {
        mOnClickListener = l;
    }

    public View.OnClickListener getOnClickListener(){
        return mOnClickListener;
    }

    private View.OnTouchListener mOnTouchListener;
    public void setOnTouchListener(View.OnTouchListener l){
        mOnTouchListener = l;
    }

    public void onDraw(Canvas canvas) {

        canvas.save();
        canvas.clipRect(rectLeft, rectTop, mWidth - rectLeft, mHeight);
        if(null != mPaintBackCircle && 0 != mPaintBackCircle.getAlpha()){
            canvas.drawCircle(mWidth / 2, mHeight / 2, mBackMaxRadius, mPaintBackCircle);
        }
        if(null != mPaintMoveCircle && 0 != mPaintMoveCircle.getAlpha()){
            canvas.drawCircle(mCenterX, mCenterY, mMoveRadius, mPaintMoveCircle);
        }
        canvas.restore();
    }
}
