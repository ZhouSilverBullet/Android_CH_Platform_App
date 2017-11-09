package com.caohua.games.ui.dataopen.widget;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.dataopen.DataOpenBindRoleLogic;
import com.caohua.games.biz.dataopen.DataOpenDelBindRoleLogic;
import com.caohua.games.biz.dataopen.DataOpenServerLogic;
import com.caohua.games.biz.dataopen.entry.DataOpenBindListEntry;
import com.caohua.games.biz.dataopen.entry.DataOpenServerEntry;
import com.caohua.games.ui.adapter.GridDividerItemDecoration;
import com.caohua.games.ui.adapter.MultiRecyclerViewAdapter;
import com.caohua.games.ui.adapter.ViewHolder;
import com.caohua.games.ui.dataopen.DataExistCallback;
import com.caohua.games.ui.vip.widget.VipTitleView;
import com.chsdk.biz.BaseLogic;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.LoadingDialog;
import com.chsdk.utils.ViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/9/20.
 */

public class DataOpenBindGridView extends RelativeLayout implements DataExistCallback {
    private Context context;
    private VipTitleView titleView;
    private RecyclerView recyclerView;
    private GridAdapter adapter;
    private List<DataOpenBindListEntry> dataList;
    private String managerGameId;
    private DataOpenBindListener dataOpenBindListener;

    @Override
    public boolean existBack() {
        if (adapter != null && adapter.getItemCount() > 0) {
            return true;
        }
        return false;
    }

    public interface DataOpenBindListener {
        void onDataOpenBind();
    }

    public void setDataOpenBindListener(DataOpenBindListener dataOpenBindListener) {
        this.dataOpenBindListener = dataOpenBindListener;
    }

    public DataOpenBindGridView(Context context) {
        this(context, null);
    }

    public DataOpenBindGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DataOpenBindGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.ch_view_data_open_bind_view, this, true);
        setVisibility(GONE);
        titleView = (VipTitleView) findViewById(R.id.ch_view_data_open_bind_title_view);
        recyclerView = (RecyclerView) findViewById(R.id.ch_view_data_open_bind_grid_view);
        recyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        recyclerView.addItemDecoration(new GridDividerItemDecoration(ViewUtil.dp2px(context, 1), getResources().getColor(R.color.ch_gray)));
        adapter = new GridAdapter(context, new ArrayList<DataOpenBindListEntry>());
        recyclerView.setAdapter(adapter);
    }

    private boolean isEditStatus;

    public void setData(List<DataOpenBindListEntry> data, String managerGameId) {
        if (data == null) {
            setVisibility(GONE);
            return;
        }
        this.managerGameId = managerGameId;
        setVisibility(VISIBLE);
        if (adapter != null) {
            dataList = data;
            data.add(new DataOpenBindListEntry());
            adapter.addAll(data);
            titleView.getMoreText().setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isEditStatus) {
                        titleView.getMoreText().setText("取消编辑");
                        isEditStatus = true;
                        for (DataOpenBindListEntry entry : dataList) {
                            entry.isFloat = true;
                        }
                        adapter.addAll(dataList);
                    } else {
                        titleView.getMoreText().setText("管理角色");
                        isEditStatus = false;
                        for (DataOpenBindListEntry entry : dataList) {
                            entry.isFloat = false;
                        }
                        adapter.addAll(dataList);
                    }
                }
            });
        }
    }

    private class GridAdapter extends MultiRecyclerViewAdapter<DataOpenBindListEntry> {
        public GridAdapter(Context context, List<DataOpenBindListEntry> list) {
            super(context, list);
        }

        @Override
        protected void covert(ViewHolder holder, final int position) {
            if (position == list.size() - 1) {
                DataOpenBindListEntry entry = list.get(position);
                if (entry.isFloat) {
                    holder.getView(R.id.ch_view_data_open_bind_item_end_rl).setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CHToast.show(context, "当前是编辑状体，请退出编辑状态再操作");
                        }
                    });
                } else {
                    holder.getView(R.id.ch_view_data_open_bind_item_end_rl).setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showGirdDialog();
                        }
                    });
                }
            } else {
                TextView name = holder.getView(R.id.ch_view_data_open_bind_item_name);
                TextView area = holder.getView(R.id.ch_view_data_open_bind_item_area);
                View editIcon = holder.getView(R.id.ch_view_data_open_bind_item_edit);
                final DataOpenBindListEntry entry = list.get(position);
                if (entry != null) {
                    String roleName = entry.role_name;
                    name.setText(TextUtils.isEmpty(roleName) ? "" : roleName);
                    area.setText(TextUtils.isEmpty(entry.cp_sname) ? "" : entry.cp_sname);
                    if (entry.isFloat) {
                        editIcon.setVisibility(VISIBLE);
                        editIcon.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final LoadingDialog dialog = new LoadingDialog(context, "");
                                new DataOpenDelBindRoleLogic().openDelBindRole(managerGameId, entry.id, new BaseLogic.DataLogicListner<String>() {
                                    @Override
                                    public void failed(String errorMsg, int errorCode) {
                                        dialog.dismiss();
                                        CHToast.show(context, errorMsg);
                                    }

                                    @Override
                                    public void success(String entryResult) {
                                        dialog.dismiss();
                                        CHToast.show(context, "解绑成功！");
                                        if (dataList != null && dataList.size() > position) {
                                            dataList.remove(position);
                                            adapter.addAll(dataList);
                                        }
                                    }
                                });

                            }
                        });
                    } else {
                        editIcon.setVisibility(GONE);
                        editIcon.setClickable(false);
                    }
                }

            }
        }

        private void showGirdDialog() {
            final LoadingDialog dialog = new LoadingDialog(context, "");
            dialog.show();
            new DataOpenServerLogic().getDataServerOpen(managerGameId, new BaseLogic.DataLogicListner<List<DataOpenServerEntry>>() {
                @Override
                public void failed(String errorMsg, int errorCode) {
                    dialog.dismiss();
                    CHToast.show(context, errorMsg);
                }

                @Override
                public void success(final List<DataOpenServerEntry> entryList) {
                    dialog.dismiss();
                    if (entryList != null) {
                        List<String> nameList = new ArrayList<String>();
                        DataOpenBottomDialog bottomDialog = new DataOpenBottomDialog(context, "绑定区服");
                        for (DataOpenServerEntry dataOpenServerEntry : entryList) {
                            String cp_sname = dataOpenServerEntry.cp_sname;
                            nameList.add(cp_sname);
                        }
                        bottomDialog.showContentPop(nameList, true);
                        bottomDialog.setListener(new DataOpenBottomDialog.DialogItemClickListener() {
                            @Override
                            public void onDialogClick(int position) {
                                final LoadingDialog dialog2 = new LoadingDialog(context, "");
                                dialog2.show();
                                if (position != -1 && entryList.size() > position) {
                                    DataOpenServerEntry dataOpenServerEntry = entryList.get(position);
                                    new DataOpenBindRoleLogic().openBindRole(managerGameId, dataOpenServerEntry, new BaseLogic.DataLogicListner<String>() {
                                        @Override
                                        public void failed(String errorMsg, int errorCode) {
                                            dialog2.dismiss();
                                            CHToast.show(context, errorMsg);
                                        }

                                        @Override
                                        public void success(String entryResult) {
                                            dialog2.dismiss();
                                            CHToast.show(context, "绑定成功！");
                                            if (dataOpenBindListener != null) {
                                                dataOpenBindListener.onDataOpenBind();
                                            }
                                        }
                                    });
                                } else {
                                    dialog2.dismiss();
                                }
                            }
                        });
                    }
                }
            });
        }

        @Override
        protected int getMultiItemViewType(int position) {
            if (position == list.size() - 1) {
                return R.layout.ch_view_data_open_bind_item_view_end;
            }
            return R.layout.ch_view_data_open_bind_item_view_n;
        }
    }

//    private void floatAnim(View view, int delay) {
//        List<Animator> animators = new ArrayList<>();
//        ObjectAnimator translationXAnim = ObjectAnimator.ofFloat(view, "translationX", -6.0f, 6.0f, -6.0f);
//        translationXAnim.setDuration(1500);
//        translationXAnim.setRepeatCount(ValueAnimator.INFINITE);//无限循环
//        translationXAnim.setRepeatMode(ValueAnimator.RESTART);//
//        translationXAnim.start();
//        animators.add(translationXAnim);
//        ObjectAnimator translationYAnim = ObjectAnimator.ofFloat(view, "translationY", -3.0f, 3.0f, -3.0f);
//        translationYAnim.setDuration(1000);
//        translationYAnim.setRepeatCount(ValueAnimator.INFINITE);
//        translationYAnim.setRepeatMode(ValueAnimator.RESTART);
//        translationYAnim.start();
//        animators.add(translationYAnim);
//
//        AnimatorSet btnSexAnimatorSet = new AnimatorSet();
//        btnSexAnimatorSet.playTogether(animators);
//        btnSexAnimatorSet.setStartDelay(delay);
//        btnSexAnimatorSet.start();
//    }
}
