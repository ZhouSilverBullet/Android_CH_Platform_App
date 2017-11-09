package com.caohua.games.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.chsdk.utils.ViewUtil;

import static android.graphics.Bitmap.Config.ARGB_8888;

public class DecorationItemView extends View {

    private static final Paint iconPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    static {
        iconPaint.setColor(0xff34cf4d);
    }

    private Bitmap cache;

    public DecorationItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        if (cache != null && (cache.getWidth() != width || cache.getHeight() != height)) {
            cache.recycle();
            cache = null;
        }

        if (cache == null) {
            cache = Bitmap.createBitmap(width, height, ARGB_8888);

            Canvas cacheCanvas = new Canvas(cache);

            float halfWidth = width / 2f;
            float halfHeight = height / 2f;

            float strokeSize = ViewUtil.dp2px(getContext(), 2);

            iconPaint.setStrokeWidth(strokeSize);

            cacheCanvas.drawLine(halfWidth, 0, halfWidth, height, iconPaint);
            cacheCanvas.drawCircle(halfWidth, halfHeight, halfWidth, iconPaint);
        }
        canvas.drawBitmap(cache, 0, 0, null);
    }
}