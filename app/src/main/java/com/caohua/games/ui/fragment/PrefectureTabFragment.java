package com.caohua.games.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.bbs.ForumShareEntry;
import com.caohua.games.biz.prefecture.ContentEntry;
import com.caohua.games.biz.prefecture.GameCenterLogic;
import com.caohua.games.biz.prefecture.PrefectureTabLogic;
import com.caohua.games.ui.prefecture.InnerLoadListView;
import com.caohua.games.ui.widget.CHTwoBallView;
import com.caohua.games.ui.widget.NoNetworkView;
import com.chsdk.api.CacheManager;
import com.chsdk.biz.app.AnalyticsHome;
import com.chsdk.biz.web.UrlOperatorHelper;
import com.chsdk.configure.DataStorage;
import com.chsdk.ui.WebActivity;
import com.chsdk.utils.LogUtil;
import com.chsdk.utils.PicUtil;
import com.culiu.mhvp.core.InnerScroller;
import com.culiu.mhvp.core.InnerScrollerContainer;
import com.culiu.mhvp.core.OuterScroller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by zhouzhou on 2017/5/8.
 */

public class PrefectureTabFragment extends Fragment implements InnerScrollerContainer {

    private InnerLoadListView innerListView;
    private View empty;
    private MyAdapter adapter;
    private CHTwoBallView tabDownloadLayout;
    private String key;
    private int position;
    private int subjectId;
    private Context context;
    private NoNetworkView noNetworkView;

    public static PrefectureTabFragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt("position", position);
        PrefectureTabFragment fragment = new PrefectureTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int id = DataStorage.getSubjectGameId(getActivity());
        if (subjectId != id) {
            subjectId = id;
            init = true;
            if (adapter != null) {
                adapter.clear();
            }
        }
        View inflate = inflater.inflate(R.layout.ch_fragment_prefecture_tab, container, false);
        innerListView = (InnerLoadListView) inflate.findViewById(R.id.ch_prefecture_inner_list_view);
        innerListView.setLoadMoreUnable(true);
        innerListView.register2Outer(mOuterScroller, mIndex);
        position = getArguments().getInt("position");
        adapter = new MyAdapter(key, new ArrayList<ContentEntry.DataBean.ContentBean>());
        innerListView.setAdapter(adapter);
        empty = inflate.findViewById(R.id.ch_fragment_test_empty);
        noNetworkView = (NoNetworkView) inflate.findViewById(R.id.ch_prefecture_tab_no_network);

        tabDownloadLayout = (CHTwoBallView) inflate.findViewById(R.id.ch_prefecture_tab_download_progress_layout);
        innerListView.setOnLoadListener(new InnerLoadListView.OnLoadListener() {
            @Override
            public void onLoad(int page) {
                tabLogic(getSubjectId(), adapter.getCount());
            }
        });
        if (position == 0) {
//            if (init) {
            tabLogic(getSubjectId(), 0);
            if (tabDownloadLayout != null) {
                tabDownloadLayout.setVisibility(View.VISIBLE);
            }
//            }
        }
        return inflate;
    }

    protected void showNoNetworkView(boolean showNoNetwork) {
        if (showNoNetwork) {
            noNetworkView.setVisibility(View.VISIBLE);
            noNetworkView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noNetworkView.setVisibility(View.GONE);
                    tabDownloadLayout.setVisibility(View.VISIBLE);
                    tabLogic(getSubjectId(), 0);
                }
            });
        } else {
            noNetworkView.setVisibility(View.GONE);
        }
    }

    public void setKey(String key) {
        this.key = key;
    }

    private void setData() {
        LogUtil.errorLog("getUserVisibleHint(): " + getUserVisibleHint() + "init: " + init);
        if (getUserVisibleHint() && init && position != 0) {
            tabLogic(getSubjectId(), 0);
            if (tabDownloadLayout != null) {
                tabDownloadLayout.setVisibility(View.VISIBLE);
            }
        }

        if (getUserVisibleHint() && adapter != null && adapter.getCount() == 0 && position != 0) {
            tabLogic(getSubjectId(), 0);
            if (tabDownloadLayout != null) {
                tabDownloadLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private boolean init = true;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        setData();
    }

    private int getSubjectId() {
        return DataStorage.getSubjectGameId(AppContext.getAppContext());
    }

    private void tabLogic(int gameId, final int page) {
        if (key == null) {
            return;
        }
        if (position == 0 && page == 0) {
            ContentEntry contentEntry = (ContentEntry) CacheManager.readObject(context, "contentEntry");
            if (contentEntry != null) {
                setTabData(page, contentEntry);
            }
        }
        innerListView.setVisibility(View.VISIBLE);
        PrefectureTabLogic logic = new PrefectureTabLogic(gameId + "", key, page, position);
        logic.tabLogic(new GameCenterLogic.AppGameCenterListener() {
            @Override
            public void failed(String errorMsg) {
                tabDownloadLayout.setVisibility(View.GONE);
                if (!AppContext.getAppContext().isNetworkConnected() && adapter.getCount() == 0) {
                    showNoNetworkView(true);
                    return;
                }
                if (adapter.getCount() == 0) {
                    empty.setVisibility(View.VISIBLE);
                    innerListView.setVisibility(View.GONE);
                    empty.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tabDownloadLayout.setVisibility(View.VISIBLE);
                            tabLogic(getSubjectId(), 0);
                        }
                    });
                } else {
                    empty.setVisibility(View.GONE);
                    innerListView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void success(Object entryResult, int currentPage) {
                showNoNetworkView(false);
                tabDownloadLayout.setVisibility(View.GONE);
                empty.setVisibility(View.GONE);
                if (entryResult instanceof ContentEntry) {
                    setTabData(page, (ContentEntry) entryResult);
                } else {
                    failed("");
                }
            }
        });
    }

    private void setTabData(int page, ContentEntry entryResult) {
        ContentEntry contentEntry = entryResult;
        List<ContentEntry.DataBean.ContentBean> content = contentEntry.getData().getContent();
        if (content.size() != 0) {
            if (page == 0) {
                adapter.clear();
            }
            init = false;
            adapter.addAll(content);
        }
        if (adapter.getCount() == 0) {
            empty.setVisibility(View.VISIBLE);
            innerListView.setVisibility(View.GONE);
            empty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tabDownloadLayout.setVisibility(View.VISIBLE);
                    tabLogic(getSubjectId(), 0);
                }
            });
        }
        boolean enable = content.size() == 0;
        if (enable) {
            innerListView.setLoadMoreUnable(true);
            innerListView.loadComplete(true);
        } else {
            innerListView.setLoadMoreUnable(false);
            innerListView.loadComplete(false);
        }
    }

    @Override
    public void onDestroy() {
        LogUtil.errorLog("PrefectureTabFragment onDestroy");
        init = true;
        super.onDestroy();
    }

    protected OuterScroller mOuterScroller;
    protected int mIndex;

    @Override
    public void setOuterScroller(OuterScroller outerScroller, int myPosition) {
        if (outerScroller == mOuterScroller && myPosition == mIndex) {
            return;
        }
        mOuterScroller = outerScroller;
        mIndex = myPosition;

        if (getInnerScroller() != null) {
            getInnerScroller().register2Outer(mOuterScroller, mIndex);
        }
    }

    class MyAdapter extends BaseAdapter {
        private List<ContentEntry.DataBean.ContentBean> list;
        private String key;

        public MyAdapter(String key, List<ContentEntry.DataBean.ContentBean> list) {
            this.key = key;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(parent.getContext(), R.layout.ch_prefecture_item_strategy, null);
                viewHolder.strategyImage = ((ImageView) convertView.findViewById(R.id.ch_prefecture_strategy_image));
                viewHolder.strategyText = ((TextView) convertView.findViewById(R.id.ch_prefecture_strategy_title));
                viewHolder.strategyType = (TextView) convertView.findViewById(R.id.ch_prefecture_strategy_type);
                viewHolder.strategyDate = ((TextView) convertView.findViewById(R.id.ch_prefecture_strategy_date));
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final ContentEntry.DataBean.ContentBean contentBean = list.get(position);
            viewHolder.strategyText.setText(contentBean.getTitle());
            if (!TextUtils.isEmpty(contentBean.getImage())) {
                viewHolder.strategyImage.setVisibility(View.VISIBLE);
                PicUtil.displayImg(parent.getContext(), viewHolder.strategyImage, contentBean.getImage(), R.drawable.ch_default_pic);
            } else {
                viewHolder.strategyImage.setVisibility(View.GONE);
            }
            viewHolder.strategyText.setText(contentBean.getTitle());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AnalyticsHome.umOnEvent(AnalyticsHome.SUBJECT_FRAGMENT_TAB_ITEM, key + "专区内容点击");
                    String detail_url = contentBean.getUrl();
                    if (!TextUtils.isEmpty(detail_url) && detail_url.contains(UrlOperatorHelper.OPEN_ARTICLE_FORUM)) {
                        try {
                            String[] split = detail_url.split("id=");
                            if (split.length >= 2) {
                                String articleId = split[1];
                                ForumShareEntry forumShareEntry = new ForumShareEntry();
                                forumShareEntry.setTitle(contentBean.getTitle());
                                forumShareEntry.setGameIcon(contentBean.getImage());
                                forumShareEntry.setGameName(contentBean.getClassify_name());
                                WebActivity.startForForumPage(getActivity(), detail_url, articleId, forumShareEntry, -1);
                            }
                        } catch (Exception e) {
                        }
                        return;
                    }
                    WebActivity.startWebPage(getActivity(), detail_url);
                }
            });
            viewHolder.strategyDate.setText(contentBean.getTime());
            viewHolder.strategyType.setText(contentBean.getClassify_name());
            return convertView;
        }

        class ViewHolder {
            ImageView strategyImage;
            TextView strategyText;
            TextView strategyType;
            TextView strategyDate;
        }

        public void addAll(Collection c) {
            if (list != null) {
                list.addAll(c);
                notifyDataSetChanged();
            }
        }

        public void clear() {
            if (list != null) {
                list.clear();
            }
        }
    }

    @Override
    public InnerScroller getInnerScroller() {
        return innerListView;
    }
}
