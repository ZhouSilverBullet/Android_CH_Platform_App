package com.caohua.games.ui.dataopen.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.dataopen.entry.DataOpenBindListEntry;
import com.caohua.games.biz.dataopen.DataOpenBindListLogic;
import com.caohua.games.biz.dataopen.entry.DataOpenGameBindEntry;
import com.caohua.games.biz.dataopen.DataOpenSwitchRoleLogic;
import com.caohua.games.ui.dataopen.DataExistCallback;
import com.chsdk.biz.BaseLogic;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.LoadingDialog;
import com.chsdk.utils.LogUtil;
import com.chsdk.utils.PicUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/9/26.
 */

public class DataOpenGameBindLayout extends RelativeLayout implements DataExistCallback {
    private Context context;
    private ImageView icon;
    private TextView name;
    private TextView qufu;
    private Button btn;
    private TextView level;
    private GameBindNotifyListener gameBindNotifyListener;
    private DataOpenGameBindEntry entry;

    @Override
    public boolean existBack() {
        if (entry != null) {
            return true;
        }
        return false;
    }

    public interface GameBindNotifyListener {
        void onGameBindNotify();
    }

    public void setGameBindNotifyListener(GameBindNotifyListener gameBindNotifyListener) {
        this.gameBindNotifyListener = gameBindNotifyListener;
    }

    public DataOpenGameBindLayout(Context context) {
        this(context, null);
    }

    public DataOpenGameBindLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DataOpenGameBindLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.ch_data_open_game_bind_layout, this, true);
        setVisibility(GONE);
        icon = (ImageView) findViewById(R.id.ch_data_open_game_bind_icon);
        name = (TextView) findViewById(R.id.ch_data_open_game_bind_name);
        qufu = (TextView) findViewById(R.id.ch_data_open_game_bind_qufu);
        btn = (Button) findViewById(R.id.ch_data_open_game_bind_btn);
        level = (TextView) findViewById(R.id.ch_data_open_game_bind_level);
    }

    private int lastPosition = -1;
    private String ids;
    private boolean isNotify;
    public void setData(final DataOpenGameBindEntry entry, final String managerGameId) {
        if (entry != null) {
            isNotify = true;
            this.entry = entry;
            setVisibility(VISIBLE);
            level.setText("LV" + entry.role_level);
            qufu.setText(entry.cp_sname);
            String roleName = entry.role_name;
            ids = entry.id;
            name.setText(roleName);
            PicUtil.displayImg(context, icon, entry.face, R.drawable.ch_account);
            //        icon.setImageBitmap();  //用户图片
            btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isNotify) {
                        return;
                    }
                    final LoadingDialog dialog = new LoadingDialog(context, "");
                    dialog.show();
                    new DataOpenBindListLogic().openBindLogic(managerGameId, new BaseLogic.DataLogicListner<List<DataOpenBindListEntry>>() {
                        @Override
                        public void failed(String errorMsg, int errorCode) {
                            dialog.dismiss();
                        }

                        @Override
                        public void success(final List<DataOpenBindListEntry> entryResult) {
                            dialog.dismiss();
                            if (entryResult != null && entryResult.size() > 0) {
                                List<String> list = new ArrayList<String>();
                                List<String> idList = new ArrayList<String>();
                                for (DataOpenBindListEntry dataOpenBindListEntry : entryResult) {
                                    String role_name = dataOpenBindListEntry.role_name;
                                    String id = dataOpenBindListEntry.id;
                                    list.add(role_name);
                                    idList.add(id);
                                }
                                if (!TextUtils.isEmpty(ids) && idList.contains(ids)) {
                                    lastPosition = idList.indexOf(ids);
                                }
                                DataOpenBottomDialog dataOpenBottomDialog = new DataOpenBottomDialog(context, "选择角色");
                                dataOpenBottomDialog.setListener(new DataOpenBottomDialog.DialogItemClickListener() {
                                    @Override
                                    public void onDialogClick(int position) {
                                        LogUtil.errorLog("position = " + position + "-- lastPosition =" + lastPosition);
                                        if (lastPosition == position) {
                                            CHToast.show(context, "相同绑定！");
                                            return;
                                        }
                                        lastPosition = position;
                                        bindLogic(position, entryResult);
                                    }
                                });

                                dataOpenBottomDialog.showContentPop(list, false);
                            }
                        }
                    });
                }

                private void bindLogic(int position, List<DataOpenBindListEntry> entryResult) {
                    if (position == -1) {
                        return;
                    }
                    DataOpenBindListEntry listEntry = entryResult.get(position);
                    if (listEntry != null) {
                        final LoadingDialog loadingDialog = new LoadingDialog(context, "");
                        loadingDialog.show();

                        new DataOpenSwitchRoleLogic().openSwitchRole(managerGameId, listEntry, new BaseLogic.DataLogicListner<String>() {
                            @Override
                            public void failed(String errorMsg, int errorCode) {
                                loadingDialog.dismiss();
                                CHToast.show(context, errorMsg);
                            }

                            @Override
                            public void success(String entryResult) {
                                loadingDialog.dismiss();
                                CHToast.show(context, "切换绑定成功！");
                                if (gameBindNotifyListener != null) {
                                    isNotify = false;
                                    gameBindNotifyListener.onGameBindNotify();
                                }
                            }
                        });
                    }
                }
            });
        } else {
            setVisibility(GONE);
        }
    }

}
