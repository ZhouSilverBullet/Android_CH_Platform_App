package com.caohua.games.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.TransmitDataInterface;
import com.chsdk.model.login.LoginUserInfo;

/**
 * Created by admin on 2017/10/31.
 */

public class BlankLoginView extends RelativeLayout {

    private Context context;
    private ImageView imageView;
    private Button btn;

    public interface BlankLoginListener {
        void onBlankLogin(LoginUserInfo info);
    }

    public BlankLoginView(Context context) {
        this(context, null);
    }

    public BlankLoginView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BlankLoginView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.ch_blank_login_view, this, true);
        setVisibility(GONE);
        setClickable(true);
        imageView = ((ImageView) findViewById(R.id.ch_blank_login_img));
        btn = ((Button) findViewById(R.id.ch_blank_login_btn));
    }

    public void show(final BlankLoginListener blankLoginListener) {
        final AppContext appContext = AppContext.getAppContext();
        if (appContext.isLogin()) {
            return;
        }
        setVisibility(VISIBLE);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                appContext.login(((Activity) context), new TransmitDataInterface() {
                    @Override
                    public void transmit(Object o) {
                        if (o instanceof LoginUserInfo) {
                            setVisibility(GONE);
                            if (blankLoginListener != null) {
                                blankLoginListener.onBlankLogin(((LoginUserInfo) o));
                            }
                        }
                    }
                });
            }
        });
    }

    public boolean isLogin() {
        final AppContext appContext = AppContext.getAppContext();
        if (appContext.isLogin()) {
            return true;
        }
        return false;
    }
}
