package com.caohua.games.ui.send;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.send.GetTagLogic;
import com.caohua.games.ui.BaseActivity;
import com.caohua.games.ui.ranking.RankingDetailFragment;
import com.caohua.games.ui.widget.EmptyView;
import com.chsdk.biz.BaseLogic;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.LoadingDialog;
import com.chsdk.utils.ViewUtil;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZengLei on 2017/5/22.
 */

public class ChooseTagActivity extends BaseActivity {

    public static final String KEY_DATA = "tag";
    public static final String KEY_DATA_ID = "id";
    public static final String KEY_FORUM_ID = "fi";

    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private EmptyView emptyView;
    private String lastTagName;
    private String forumId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch_activity_send_choose_tag);


        if (getIntent() != null) {
            lastTagName = getIntent().getStringExtra(KEY_DATA);
            forumId = getIntent().getStringExtra(KEY_FORUM_ID);
        }
        if (TextUtils.isEmpty(forumId)) {
            CHToast.show(this, "当前论坛出错,请重试");
            finish();
            return;
        }

        initView();
        loadTagDatas();
    }

    private void initView() {
        emptyView = getView(R.id.ch_activity_send_choose_tag_empty);
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emptyView.setVisibility(View.GONE);
                loadTagDatas();
            }
        });
//        getView(R.id.ch_activity_send_choose_tag_back).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                returnResult();
//                finish();
//            }
//        });
        recyclerView = getView(R.id.ch_activity_send_choose_tag_list);
        int spacingInPixels = ViewUtil.dp2px(this, 10);
        recyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerAdapter = new RecyclerAdapter(lastTagName);
    }

    private void loadTagDatas() {
        final LoadingDialog dialog = new LoadingDialog(this, "");
        dialog.show();

        GetTagLogic logic = new GetTagLogic();
        logic.getTag(forumId, new BaseLogic.DataLogicListner<List<TagItem>>() {
            @Override
            public void failed(String errorMsg, int code) {
                dialog.dismiss();
                emptyView.setVisibility(View.VISIBLE);
            }

            @Override
            public void success(List<TagItem> entryResult) {
                dialog.dismiss();
                setData(entryResult);
            }
        });
    }

    private void setData(List<TagItem> data) {
        if (data == null || data.size() == 0)
            return;

        recyclerAdapter.setData(data);
        recyclerView.setAdapter(recyclerAdapter);
    }

    class RecyclerAdapter extends RecyclerView.Adapter {
        private List<TagItem> data;
        private String lastTagName;
        private String lastTagId;

        public RecyclerAdapter(String lastTagName) {
            this.lastTagName = lastTagName;
        }

        public void setData(List<TagItem> data) {
            this.data = data;
            if (TextUtils.isEmpty(lastTagName)) {
                this.lastTagName = data.get(0).tagName;
                this.lastTagId = data.get(0).tagId;
            } else {
                for (TagItem item : data) {
                    if (lastTagName.equals(item.tagName)) {
                        lastTagId = item.tagId;
                        break;
                    }
                }
            }
        }

        public String getSelect() {
            return lastTagName;
        }

        public String getSelectId() {
            return lastTagId;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new BodyViewHolder(LayoutInflater.from(ChooseTagActivity.this).inflate(R.layout.ch_view_send_choose_tag, parent,
                    false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            BodyViewHolder bodyViewHolder = (BodyViewHolder) holder;
            bodyViewHolder.setData(position);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class BodyViewHolder extends RecyclerView.ViewHolder {
            private TextView tvTagName;
            private View img;

            public BodyViewHolder(View itemView) {
                super(itemView);
                tvTagName = (TextView) itemView.findViewById(R.id.ch_view_send_choose_tag_name);
                img = itemView.findViewById(R.id.ch_view_send_choose_tag_cbx);
            }

            public void setData(int position) {
                final TagItem item = data.get(position);
                if (!TextUtils.isEmpty(lastTagName) && lastTagName.equals(item.tagName)) {
                    img.setBackgroundResource(R.drawable.ch_publish_check);
                } else {
                    img.setBackgroundResource(R.drawable.ch_publish_uncheck);
                }
                tvTagName.setText(item.tagName);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lastTagName = item.tagName;
                        lastTagId = item.tagId;
                        notifyDataSetChanged();

                        v.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                returnResult();
                                finish();
                            }
                        }, 100);
                    }
                });
            }
        }
    }

    public static class TagItem{
        @SerializedName("tag_name")
        public String tagName;
        @SerializedName("tag_id")
        public String tagId;
    }

    private void returnResult() {
        Intent intent = new Intent();
        intent.putExtra(KEY_DATA, recyclerAdapter.getSelect());
        intent.putExtra(KEY_DATA_ID, recyclerAdapter.getSelectId());
        setResult(Activity.RESULT_OK, intent);
    }

    @Override
    public void onBackPressed() {
        returnResult();
        super.onBackPressed();
    }

    public static void lauch(Activity activity, int code, String tagName, String forumId) {
        Intent intent = new Intent(activity, ChooseTagActivity.class);
        intent.putExtra(KEY_DATA, tagName);
        intent.putExtra(KEY_FORUM_ID, forumId);
        activity.startActivityForResult(intent, code);
    }

    public static class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;
            outRect.left = space;
        }
    }
}
