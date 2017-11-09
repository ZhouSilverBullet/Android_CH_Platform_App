package com.caohua.games.ui.article;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.TransmitDataInterface;
import com.caohua.games.biz.article.ReportLogic;
import com.caohua.games.ui.BaseActivity;
import com.chsdk.biz.BaseLogic;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZengLei on 2017/5/25.
 */

public class ReportActivity extends BaseActivity {

    private static final String KEY_DATA = "artice_id";

    private RecyclerView chActivityArticleReportList;
    private EditText chActivityArticleReportContent;
    private TextView tvOther;
    private Button chActivityArticleReportBtn;
    private RecyclerAdapter recyclerAdapter;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.ch_activity_article_report);
        initView();
    }

    private void initView() {
        tvOther = (TextView) findViewById(R.id.ch_activity_article_report_tv_other);
        chActivityArticleReportList = (RecyclerView) findViewById(R.id.ch_activity_article_report_list);
        chActivityArticleReportContent = (EditText) findViewById(R.id.ch_activity_article_report_content);
        chActivityArticleReportBtn = (Button) findViewById(R.id.ch_activity_article_report_btn);

        recyclerAdapter = new RecyclerAdapter();
        recyclerAdapter.setData(getData());
        chActivityArticleReportList.setLayoutManager(new LinearLayoutManager(this));
        chActivityArticleReportList.setAdapter(recyclerAdapter);

        setOtherVisible(false);
    }

    private List<String> getData() {
        List<String> data = new ArrayList<>();
        data.add("色情");
        data.add("欺诈");
        data.add("侮辱诋毁");
        data.add("广告骚扰");
        data.add("政治");
        data.add("其他");
        return data;
    }

    private void setOtherVisible(boolean visible) {
        tvOther.setVisibility(visible ? View.VISIBLE : View.GONE);
        chActivityArticleReportContent.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void onClick(final View view) {
        if (!AppContext.getAppContext().isLogin()) {
            AppContext.getAppContext().login(this, new TransmitDataInterface() {
                @Override
                public void transmit(Object o) {

                }
            });
            return;
        }

        String content = null;
        if ("其他".equals(recyclerAdapter.getSelect())) {
            String editContent = chActivityArticleReportContent.getText().toString().trim();
            if (TextUtils.isEmpty(editContent)) {
                CHToast.show(this, "请输入举报的原因");
                return;
            }
            content = editContent;
        } else {
            content = recyclerAdapter.getSelect();
        }
        String articleId = getIntent().getStringExtra(KEY_DATA);

        LoadingDialog.start(this);

        new ReportLogic().report(articleId, content, new BaseLogic.LogicListener() {
            @Override
            public void failed(String errorMsg) {
                LoadingDialog.stop();
                CHToast.show(getApplicationContext(), errorMsg);
            }

            @Override
            public void success(String... result) {
                LoadingDialog.stop();
                CHToast.show(getApplicationContext(), "提交成功");

                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 100);
            }
        });
    }

    class RecyclerAdapter extends RecyclerView.Adapter {
        private List<String> data;
        private String lastTagName;

        public RecyclerAdapter() {
        }

        public void setData(List<String> data) {
            this.data = data;
        }

        public String getSelect() {
            return lastTagName;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerAdapter.BodyViewHolder(LayoutInflater.from(ReportActivity.this)
                    .inflate(R.layout.ch_view_article_report, parent,
                    false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            BodyViewHolder bodyViewHolder = (RecyclerAdapter.BodyViewHolder) holder;
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
                final String item = data.get(position);
                if (TextUtils.isEmpty(lastTagName) && position == 0
                        || !TextUtils.isEmpty(lastTagName) && lastTagName.equals(item)) {
                    lastTagName = item;
                    img.setBackgroundResource(R.drawable.ch_publish_check);
                } else {
                    img.setBackgroundResource(R.drawable.ch_publish_uncheck);
                }
                tvTagName.setText(item);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lastTagName = item;
                        notifyDataSetChanged();
                        setOtherVisible("其他".equals(lastTagName));
                    }
                });
            }
        }
    }

    public static void start(Context context, String articleId) {
        if (TextUtils.isEmpty(articleId)) {
            CHToast.show(context, "当前文章出了点小差错,无法举报");
            return;
        }

        Intent intent = new Intent(context, ReportActivity.class);
        intent.putExtra(KEY_DATA, articleId);
        context.startActivity(intent);
    }
}
