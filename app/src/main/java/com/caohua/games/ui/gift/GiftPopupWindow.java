package com.caohua.games.ui.gift;

import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.caohua.games.R;
import com.chsdk.ui.widget.CHToast;

/**
 * Created by admin on 2016/11/12.
 */

public class GiftPopupWindow extends PopupWindow {

    private LinearLayout layout;
    private ImageView close;
    private Button copy;
    private TextView id;

    public GiftPopupWindow(Context context,String result) {
        init(context,result);
    }

    private void init(final Context context,String result) {
        View view = LayoutInflater.from(context).inflate(R.layout.ch_pop_gift_get, null);
        layout = (LinearLayout) view.findViewById(R.id.ch_pop_get_layout);
        close = (ImageView) view.findViewById(R.id.ch_pop_close);
        copy = (Button) view.findViewById(R.id.ch_pop_get_copy);
        id = (TextView) view.findViewById(R.id.ch_pop_id);
        id.setText(result);
        setContentView(view);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        showAtLocation(view, Gravity.CENTER, 0, 0);
        setOutsideTouchable(true);
        update();
        Drawable drawable = context.getResources().getDrawable(R.drawable.ch_pop_gift_bg);
        setBackgroundDrawable(drawable);
        layout.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss();
                }
                return false;
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String giftId = id.getText().toString().trim();
                ClipboardManager copyManager = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
                copyManager.setText(giftId);
                CHToast.show(context, "复制成功");
                dismiss();
            }
        });

    }
}
