package com.caohua.games.ui.fragment;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.account.TieZiEntry;
import com.caohua.games.biz.account.TieZiLogic;
import com.caohua.games.biz.bbs.ForumShareEntry;
import com.caohua.games.biz.prefecture.GameCenterLogic;
import com.caohua.games.ui.account.AccountHeadView;
import com.caohua.games.ui.account.AccountHomePageActivity;
import com.caohua.games.ui.prefecture.InnerLoadListView;
import com.caohua.games.ui.widget.BlankLoginView;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.utils.PicUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by zhouzhou on 2017/5/28.
 */

public class TieZiFragment extends NormalFragment {
    private InnerLoadListView bottomListView;
    private TieZiAdapter adapter;
    private View emptyView;
    private BlankLoginView blankLogin;

    @Override
    protected void initChildView() {
        bottomListView = findView(R.id.ch_activity_tiezi_list);
        emptyView = findView(R.id.ch_fragment_tie_zi_empty);
        blankLogin = findView(R.id.ch_fragment_tie_zi_blank_login);
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tieZiLogic(getActivity(), 0, "");
            }
        });
        adapter = new TieZiAdapter(new ArrayList<TieZiEntry.DataBean>());
        bottomListView.setAdapter(adapter);
        if (blankLogin.isLogin()) {
            tieZiLogic(getActivity(), 0, "");
        } else {
            blankLogin.show(new BlankLoginView.BlankLoginListener() {
                @Override
                public void onBlankLogin(LoginUserInfo info) {
                    tieZiLogic(getActivity(), 0, "");
                }
            });
        }
        bottomListView.setOnLoadListener(new InnerLoadListView.OnLoadListener() {
            @Override
            public void onLoad(int page) {
                tieZiLogic(AppContext.getAppContext(), adapter.getCount(), "");
            }
        });
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Subscribe
    public void notifyToLoginTiezi(LoginUserInfo info) {
        if (blankLogin != null && blankLogin.isLogin()) {
            tieZiLogic(getActivity(), 0, "");
            blankLogin.setVisibility(View.GONE);
        }
    }

    private void tieZiLogic(final Context context, int page, String userId) {
        TieZiLogic tieZiLogic = new TieZiLogic(context, page, userId);
        tieZiLogic.tieZi(new GameCenterLogic.AppGameCenterListener() {
            @Override
            public void failed(String errorMsg) {
                CHToast.show(context, errorMsg);
                if (AppContext.getAppContext().isNetworkConnected()) {
                    if (!hasData()) {
                        showNoNetworkView(true);
                    }
                    return;
                }
                emptyView.setVisibility(hasData() ? View.GONE : View.VISIBLE);
            }

            @Override
            public void success(Object entryResult, int currentPage) {
                showNoNetworkView(false);
                if (entryResult instanceof TieZiEntry) {
                    TieZiEntry entry = (TieZiEntry) entryResult;
                    List<TieZiEntry.DataBean> data = entry.getData();
                    if (adapter != null) {
                        adapter.addAll(data);
                    }
                    if (data.size() == 0) {
                        loadMoreFinish(false);
                    } else {
                        loadMoreFinish(true);
                    }
                } else {
                    loadMoreFinish(false);
                }
                emptyView.setVisibility(hasData() ? View.GONE : View.VISIBLE);
            }
        });
    }

    protected void loadMoreFinish(boolean success) {
        bottomListView.loadComplete(!success);
        bottomListView.setLoadMoreUnable(!success);
    }

    private boolean hasData() {
        return adapter != null && adapter.getCount() > 0;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ch_fragment_tie_zi;
    }

    class TieZiAdapter extends BaseAdapter {

        List<TieZiEntry.DataBean> data;

        public TieZiAdapter(List<TieZiEntry.DataBean> data) {
            this.data = data;
        }

        @Override
        public int getCount() {
            return data == null ? 0 : data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public void addAll(Collection collection) {
            if (data != null) {
                data.addAll(collection);
                notifyDataSetChanged();
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ch_bbs_content_list_item, null);
                viewHolder.userAccountHeadView = (AccountHeadView) convertView.findViewById(R.id.ch_bbs_content_item_user_photo);
                viewHolder.nickNameText = (TextView) convertView.findViewById(R.id.ch_bbs_content_item_nick_name);
                viewHolder.timeText = (TextView) convertView.findViewById(R.id.ch_bbs_content_item_time);
                viewHolder.contentText = ((TextView) convertView.findViewById(R.id.ch_bbs_content_item_content));
                viewHolder.upvoteTotalText = (TextView) convertView.findViewById(R.id.ch_bbs_content_item_upvote_total);
                viewHolder.commentTotalText = (TextView) convertView.findViewById(R.id.ch_bbs_content_item_comment_total);
                viewHolder.readTotalText = ((TextView) convertView.findViewById(R.id.ch_bbs_content_item_read));
                viewHolder.imageContainerLayout = (LinearLayout) convertView.findViewById(R.id.ch_bbs_content_item_image_container);
                viewHolder.itemContentImage1 = (ImageView) convertView.findViewById(R.id.ch_bbs_content_item_image_1);
                viewHolder.image3Layout = (RelativeLayout) convertView.findViewById(R.id.ch_bbs_content_item_rl_3);
                viewHolder.imageLastText = (TextView) convertView.findViewById(R.id.ch_bbs_content_item_image_text);
                viewHolder.itemContentImage2 = (ImageView) convertView.findViewById(R.id.ch_bbs_content_item_image_2);
                viewHolder.itemContentImage3 = (ImageView) convertView.findViewById(R.id.ch_bbs_content_item_image_3);
                viewHolder.adminNameText = ((TextView) convertView.findViewById(R.id.ch_bbs_content_item_admin_name));
                viewHolder.itemContentStatusImage = (ImageView) convertView.findViewById(R.id.ch_bbs_content_item_status_image);
                viewHolder.itemContentLevelText = (TextView) convertView.findViewById(R.id.ch_bbs_content_item_level_value);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final TieZiEntry.DataBean dataBean = data.get(position);
            String user_photo = dataBean.getUser_photo();
            viewHolder.userAccountHeadView.setAccountImage(user_photo, false);
            viewHolder.userAccountHeadView.setAccountWidthHeadBg(dataBean.getImg_mask(), 10);

            clickEventUserMsg(viewHolder.userAccountHeadView.getAccountImage(), dataBean.getUserid(), dataBean.getNickname());
            clickEventUserMsg(viewHolder.nickNameText, dataBean.getUserid(), dataBean.getNickname());

            viewHolder.nickNameText.setText(dataBean.getNickname());
            viewHolder.timeText.setText(dataBean.getAdd_time());
            viewHolder.contentText.setText(dataBean.getTitle());
            viewHolder.upvoteTotalText.setText(dataBean.getUpvote_total());
            viewHolder.commentTotalText.setText(dataBean.getComment_total());
            List<String> images = dataBean.getImages();
            int size = images.size();
            viewHolder.imageContainerLayout.setVisibility(View.VISIBLE);
            if (size == 1) {
                viewHolder.itemContentImage1.setVisibility(View.VISIBLE);
                viewHolder.itemContentImage2.setVisibility(View.GONE);
                viewHolder.image3Layout.setVisibility(View.GONE);
                viewHolder.imageLastText.setVisibility(View.GONE);
            } else if (size == 2) {
                viewHolder.itemContentImage1.setVisibility(View.VISIBLE);
                viewHolder.itemContentImage2.setVisibility(View.VISIBLE);
                viewHolder.image3Layout.setVisibility(View.GONE);
                viewHolder.imageLastText.setVisibility(View.GONE);
            } else if (size >= 3) {
                viewHolder.itemContentImage1.setVisibility(View.VISIBLE);
                viewHolder.itemContentImage2.setVisibility(View.VISIBLE);
                viewHolder.image3Layout.setVisibility(View.VISIBLE);
                viewHolder.imageLastText.setVisibility(View.GONE);
                if (size > 3) {
                    viewHolder.imageLastText.setVisibility(View.VISIBLE);
                    viewHolder.imageLastText.setText("共" + size + "张图");
                }
            } else {
                viewHolder.itemContentImage1.setVisibility(View.GONE);
                viewHolder.itemContentImage2.setVisibility(View.GONE);
                viewHolder.image3Layout.setVisibility(View.GONE);
                viewHolder.imageLastText.setVisibility(View.GONE);
                viewHolder.imageContainerLayout.setVisibility(View.GONE);
            }
            String admin_name = dataBean.getAdmin_name();
            if (!TextUtils.isEmpty(admin_name)) {
                viewHolder.nickNameText.setTextColor(getResources().getColor(R.color.ch_bbs_purple));
                viewHolder.adminNameText.setVisibility(View.VISIBLE);
                viewHolder.adminNameText.setText(admin_name);
            } else {
                viewHolder.nickNameText.setTextColor(getResources().getColor(R.color.ch_black));
                viewHolder.adminNameText.setVisibility(View.GONE);
            }
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    String url = images.get(i);
                    if (i == 0) {
                        doImageIcon(viewHolder.itemContentImage1, url, R.drawable.ch_default_pic);
                    } else if (i == 1) {
                        doImageIcon(viewHolder.itemContentImage2, url, R.drawable.ch_default_pic);
                    } else if (i == 2) {
                        doImageIcon(viewHolder.itemContentImage3, url, R.drawable.ch_default_pic);
                    }
                }
            }

            boolean is_good = dataBean.isIs_good();
            boolean is_lock = dataBean.isIs_lock();
            boolean is_top = dataBean.isIs_top();
            viewHolder.itemContentStatusImage.setVisibility(View.GONE);
            if (is_lock) {
                viewHolder.itemContentStatusImage.setVisibility(View.VISIBLE);
                viewHolder.itemContentStatusImage.setImageResource(R.drawable.ch_bbs_suoding_icon);
            }
            if (is_good) {
                viewHolder.itemContentStatusImage.setVisibility(View.VISIBLE);
                viewHolder.itemContentStatusImage.setImageResource(R.drawable.ch_bbs_jinghua_icon);
            }
            if (is_top) {
                viewHolder.itemContentStatusImage.setVisibility(View.VISIBLE);
                viewHolder.itemContentStatusImage.setImageResource(R.drawable.ch_bbs_zhiding_icon);
            }

            String read_total = dataBean.getRead_total();
            if (TextUtils.isEmpty(read_total)) {
                viewHolder.readTotalText.setText(0 + "人阅读");
            } else {
                viewHolder.readTotalText.setText(read_total + "人阅读");
            }

            String grow_name = dataBean.getGrow_name();
            if (!TextUtils.isEmpty(grow_name)) {
                viewHolder.itemContentLevelText.setText(dataBean.getShow_level());
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String detail_url = dataBean.getDetail_url();
                    if (!TextUtils.isEmpty(detail_url)) {
                        ForumShareEntry forumShareEntry = new ForumShareEntry();
                        forumShareEntry.setTitle(dataBean.getTitle());
                        forumShareEntry.setGameIcon(dataBean.getGame_icon());
                        forumShareEntry.setGameName(dataBean.getGame_name());
                        WebActivity.startForForumPage(v.getContext(), detail_url, dataBean.getArticle_id(), forumShareEntry, -1);
                    }
                }
            });
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String detail_url = dataBean.getDetail_url();
                    if (!TextUtils.isEmpty(detail_url)) {
                        try {
                            String[] split = detail_url.split("id=");
                            if (split.length >= 2) {
                                String articleId = split[1];
                                ForumShareEntry forumShareEntry = new ForumShareEntry();
                                forumShareEntry.setTitle(dataBean.getTitle());
                                forumShareEntry.setGameIcon(dataBean.getGame_icon());
                                forumShareEntry.setGameName(dataBean.getGame_name());
                                WebActivity.startForForumPage(v.getContext(), detail_url, articleId, forumShareEntry, -1);
                            }
                        } catch (Exception e) {
                        }

                    }
                }
            });
            return convertView;
        }

        private void clickEventUserMsg(View view, final String userId, final String userName) {
            if (view != null) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AccountHomePageActivity.start(getActivity(), userId, userName);
                    }
                });
            }
        }

        public void doImageIcon(ImageView imageView, String url, int defaultPic) {
            imageView.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(url)) {
                PicUtil.displayImg(getActivity(), imageView, url, defaultPic);
            } else {
                imageView.setImageResource(defaultPic);
            }
        }

        class ViewHolder {
            AccountHeadView userAccountHeadView;
            TextView nickNameText;
            TextView timeText;
            TextView contentText;
            TextView upvoteTotalText;
            TextView commentTotalText;
            LinearLayout imageContainerLayout;
            ImageView itemContentImage1;
            ImageView itemContentImage2;
            ImageView itemContentImage3;
            TextView adminNameText;
            RelativeLayout image3Layout;
            TextView imageLastText;
            TextView readTotalText;
            ImageView itemContentStatusImage;
            TextView itemContentLevelText;
        }
    }

}
