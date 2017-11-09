package com.caohua.games.ui.adapter;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by zhouzhou on 2017/2/22.
 */

public class ItemDivider extends RecyclerView.ItemDecoration{
    private Paint paint;
    private int dividerWidth = 1;
    private RecyclerView.LayoutManager layoutManager;

    public ItemDivider() {
        initPaint();
        paint.setColor(0xffff0000);
    }

    private void initPaint() {
        if (paint == null) {
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.FILL);
        }
    }

    public ItemDivider setDividerWidth(int dividerWidth) {
        this.dividerWidth = dividerWidth;
        return this;
    }

    public ItemDivider setDividerColor(int color) {
        initPaint();
        paint.setColor(color);
        return this;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (layoutManager == null) {
            layoutManager = parent.getLayoutManager();
        }

        if (layoutManager instanceof LinearLayoutManager) {
            int orientation = ((LinearLayoutManager) layoutManager).getOrientation();
            if (orientation == LinearLayoutManager.VERTICAL) {
                outRect.bottom = dividerWidth;
            } else if (orientation == LinearLayoutManager.HORIZONTAL) {
                outRect.right = dividerWidth;
            }
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        // 这个值是为了补偿横竖方向上分割线交叉处间隙
        int offSet = (int) Math.ceil(dividerWidth * 1f / 2);
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int left2 = child.getLeft() - offSet - params.leftMargin;
            int right2 = child.getRight() + offSet + params.rightMargin;
            int top2 = child.getBottom() + params.bottomMargin;
            int bottom2 = top2 + dividerWidth;
            //绘制分割线(矩形)
            c.drawRect(left2, top2, right2, bottom2, paint);
        }
    }

}
