package com.caohua.games.ui.giftcenter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.gift.GiftCenterClassifyEntry;
import com.caohua.games.biz.gift.GiftCenterClassifyLogic;
import com.caohua.games.ui.fragment.NormalFragment;
import com.caohua.games.ui.giftcenter.widget.WaveSideBarView;
import com.caohua.games.ui.widget.EmptyView;
import com.caohua.games.ui.widget.NoNetworkView;
import com.chsdk.biz.BaseLogic;
import com.chsdk.http.HttpConsts;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.utils.PicUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by admin on 2017/10/26.
 */

public class GiftCenterClassifyFragment extends NormalFragment {
    private RecyclerView mRecyclerView;
    private WaveSideBarView mSideBarView;
    private EmptyView emptyView;
    private boolean isFragmentVisible;
    private boolean isFirst = true;
    private ClassifyAdapter adapter;
    private ProgressBar progress;
    private NoNetworkView noNetworkView;

    @Override
    protected void initChildView() {
        mRecyclerView = findView(R.id.ch_fragment_gift_center_classify_recycler_view);
        mSideBarView = findView(R.id.ch_fragment_gift_center_classify_side_view);
        emptyView = findView(R.id.ch_fragment_gift_center_classify_empty);
        progress = findView(R.id.ch_fragment_gift_center_classify_progress);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(activity));

        final PinnedHeaderDecoration decoration = new PinnedHeaderDecoration();
        decoration.registerTypePinnedHeader(1, new PinnedHeaderDecoration.PinnedHeaderCreator() {
            @Override
            public boolean create(RecyclerView parent, int adapterPosition) {
                return true;
            }
        });
        mRecyclerView.addItemDecoration(decoration);

        if (isFragmentVisible && isFirst) {
            progress.setVisibility(View.VISIBLE);
            progress.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onFragmentVisibleChange(true);
                }
            }, 300);
        }

        mSideBarView.setOnTouchLetterChangeListener(new WaveSideBarView.OnTouchLetterChangeListener() {
            @Override
            public void onLetterChange(String letter) {
                int pos = adapter.getLetterPosition(letter);

                if (pos != -1) {
                    mRecyclerView.scrollToPosition(pos);
                    LinearLayoutManager mLayoutManager =
                            (LinearLayoutManager) mRecyclerView.getLayoutManager();
                    mLayoutManager.scrollToPositionWithOffset(pos, 0);
                }
            }
        });

        noNetworkView = findView(R.id.ch_fragment_gift_center_classify_no_network);
    }

    protected void showNoNetworkView(boolean showNoNetwork) {
        if (showNoNetwork) {
            noNetworkView.setVisibility(View.VISIBLE);
            noNetworkView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noNetworkView.setVisibility(View.GONE);
                    loadData(true);
                }
            });
        } else {
            noNetworkView.setVisibility(View.GONE);
        }
    }

    protected void showEmptyView(boolean showEmpty) {
        if (showEmpty) {
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    emptyView.setVisibility(View.GONE);
                    loadData(true);
                }
            });
        } else {
            emptyView.setVisibility(View.GONE);
        }
    }

    public void loadData(boolean b) {
        onFragmentVisibleChange(b);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isFragmentVisible = true;
        }

        if (mRoot == null) {
            return;
        }
        //可见，并且没有加载过
        if (isFirst && isFragmentVisible) {
            progress.setVisibility(View.VISIBLE);
            onFragmentVisibleChange(true);
            return;
        }
        //由可见——>不可见 已经加载过
        if (isFragmentVisible) {
            onFragmentVisibleChange(false);
            isFragmentVisible = false;
        }
    }

    protected void onFragmentVisibleChange(boolean b) {
        if (b) {
            onLogic();
        }
    }

    private void onLogic() {
        isFirst = false;
        new GiftCenterClassifyLogic().centerClassify(new BaseLogic.DataLogicListner<GiftCenterClassifyEntry>() {
            @Override
            public void failed(String errorMsg, int errorCode) {
                progress.setVisibility(View.GONE);
                mSideBarView.setVisibility(View.GONE);
                if (!HttpConsts.ERROR_CODE_PARAMS_VALID.equals(errorMsg)) {
                    CHToast.show(activity, errorMsg);
                }
                if (adapter != null && adapter.getItemCount() > 0) {
                    showEmptyView(false);
                    showNoNetworkView(false);
                    return;
                }
                if (HttpConsts.ERROR_NO_NETWORK.equals(errorMsg)) {
                    showNoNetworkView(true);
                    return;
                }
                showEmptyView(true);
            }

            @Override
            public void success(GiftCenterClassifyEntry entry) {
                progress.setVisibility(View.GONE);
                if (entry == null) {
                    showEmptyView(true);
                    return;
                }

                List<GiftCenterClassifyEntry.GiftBean> gift = entry.getGift();
                List<String> nav = entry.getNav();
                for (String s : nav) {
                    GiftCenterClassifyEntry.GiftBean bean = new GiftCenterClassifyEntry.GiftBean();
                    bean.setNav(s);
                    bean.setType(1);
                    gift.add(0, bean);
                }

                if (gift == null || gift.size() == 0) {
                    showEmptyView(true);
                    return;
                }

                Collections.sort(gift, new LetterComparator());
                showEmptyView(false);
                if (adapter == null) {
                    adapter = new ClassifyAdapter(gift);
                    mRecyclerView.setAdapter(adapter);
                } else {
                    adapter.addAll(gift);
                }
                mSideBarView.setVisibility(View.VISIBLE);

                if (adapter != null && adapter.getItemCount() > 0) {
                    return;
                }
                showEmptyView(true);
            }
        });
    }

    private class LetterComparator implements Comparator<GiftCenterClassifyEntry.GiftBean> {

        @Override
        public int compare(GiftCenterClassifyEntry.GiftBean l, GiftCenterClassifyEntry.GiftBean r) {
            if (l == null || r == null) {
                return 0;
            }

            String lhsSortLetters = l.getNav().substring(0, 1).toUpperCase();
            String rhsSortLetters = r.getNav().substring(0, 1).toUpperCase();
            if (lhsSortLetters == null || rhsSortLetters == null) {
                return 0;
            }
            return lhsSortLetters.compareTo(rhsSortLetters);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ch_fragment_gift_center_classify;
    }

    private class ClassifyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<GiftCenterClassifyEntry.GiftBean> list;
        private LayoutInflater inflater;

        public ClassifyAdapter(List<GiftCenterClassifyEntry.GiftBean> list) {
            this.list = list;
            inflater = LayoutInflater.from(activity);
        }

        public int getLetterPosition(String letter) {
            for (int i = 0; i < list.size(); i++) {
                int type = list.get(i).getType();
                if (type == 1 && list.get(i).getNav().equals(letter)) {
                    return i;
                }
            }
            return -1;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 0)
                return new HeaderHolder(inflater.inflate(R.layout.ch_gift_center_classify_item_content, parent, false));
            else
                return new PinnedHolder(inflater.inflate(R.layout.ch_gift_center_classify_item_header, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof HeaderHolder) {
                GiftCenterClassifyEntry.GiftBean entry = list.get(position);
                ((HeaderHolder) holder).title.setText(entry.getGame_name());
                ((HeaderHolder) holder).count.setText("" + entry.getGift_num());
                PicUtil.displayImg(activity, ((HeaderHolder) holder).image, entry.getGame_icon(), R.drawable.ch_default_apk_icon);
                final String game_id = entry.getGame_id();
                ((HeaderHolder) holder).layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(game_id)) {
                            GameGiftActivity.start(activity, game_id);
                        }
                    }
                });
            } else {
                GiftCenterClassifyEntry.GiftBean entry = list.get(position);
                ((PinnedHolder) holder).city_tip.setText(entry.getNav());
            }
        }

        public void addAll(List<GiftCenterClassifyEntry.GiftBean> entryList) {
            if (list != null) {
                list.addAll(entryList);
                notifyDataSetChanged();
            }
        }

        @Override
        public int getItemViewType(int position) {
            GiftCenterClassifyEntry.GiftBean entry = list.get(position);
            return entry.getType();
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }

        class HeaderHolder extends RecyclerView.ViewHolder {

            View layout;
            ImageView image;
            TextView title;
            TextView count;

            public HeaderHolder(View view) {
                super(view);
                title = (TextView) view.findViewById(R.id.ch_gift_center_classify_item_content_title);
                image = (ImageView) view.findViewById(R.id.ch_gift_center_classify_item_content_image);
                count = (TextView) view.findViewById(R.id.ch_gift_center_classify_item_content_count);
                layout = view.findViewById(R.id.ch_gift_center_classify_item_content_layout);
            }
        }


        class PinnedHolder extends RecyclerView.ViewHolder {

            TextView city_tip;

            public PinnedHolder(View view) {
                super(view);
                city_tip = (TextView) view.findViewById(R.id.ch_gift_center_classify_item_tip);
            }
        }
    }
}