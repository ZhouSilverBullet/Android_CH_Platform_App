package com.caohua.games.ui.widget.refresh;

/**
 * Created by admin on 2017/11/4.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import com.caohua.games.R;
import com.scwang.smartrefresh.layout.util.DensityUtil;

/**
 * Created by cjj on 2015/8/27.
 */
public class CHRoundDotView extends View {

    private int num = 5;
    private Paint mPath;
    private float mRadius;
    private float fraction;
    private RectF rectF;

    public CHRoundDotView(Context context) {
        super(context);
        mPath = new Paint();
        mPath.setAntiAlias(true);
        mPath.setColor(getResources().getColor(R.color.ch_green));
        mRadius = DensityUtil.dp2px(7);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        for (int i = 0; i < num; i++) {
            float x = DensityUtil.px2dp(height);
            float radius = mRadius * (1 - 1 / ((x / 10 + 1)));//y6 = mRadius*(1-1/(x/10+1));半径
            float percent = fraction > 1 ? 1 : fraction;
            float drawWidth = 0;
            if (i < 2 && percent != 1) {
                drawWidth = (width / 2) * percent - radius / 2;
                if (i == 1) {
                    drawWidth = drawWidth - 30 - 30;
                    float alpha = 255 * 0.4f;
                    mPath.setAlpha((int) alpha);
                } else {
                    drawWidth -= 30;
                    float alpha = 255;
                    mPath.setAlpha((int) alpha);
                }
            } else if (i < 4 && percent != 1) {
                drawWidth = width - (width / 2 * percent) - radius / 2;
                if (i == 3) {
                    drawWidth = drawWidth + 30 + 30;
                    float alpha = 255 * 0.6f;
                    mPath.setAlpha((int) alpha);
                } else {
                    drawWidth += 30;
                    float alpha = 255 * 0.8f;
                    mPath.setAlpha((int) alpha);
                }
            } else if (i == 4 && percent != 1) {
                drawWidth = width - (width / 2) - radius / 2;
                float alpha = 255;
                mPath.setAlpha((int) alpha);
                canvas.rotate(-90 * percent, drawWidth, (getHeight() / 2) * percent);
                canvas.scale(2f - percent, 2f - percent, drawWidth, (getHeight() / 2) * percent);
                rectF = new RectF(drawWidth - mRadius / 2, (getHeight() / 2) * percent - mRadius / 2, drawWidth + mRadius / 2, (getHeight() / 2) * percent + mRadius / 2);
            }
            if (percent >= 1) {
                drawWidth = width - (width / 2 * percent) - radius / 2;
                float alpha = 255;
                mPath.setAlpha((int) alpha);
            }
            if (i != 4) {
                rectF = new RectF(drawWidth - mRadius / 2, (getHeight() / 2) - mRadius / 2, drawWidth + mRadius / 2, (getHeight() / 2) + mRadius / 2);
            }
            canvas.drawRect(rectF, mPath);
        }
    }

    public void setFraction(float fraction) {
        this.fraction = fraction;
    }
}

