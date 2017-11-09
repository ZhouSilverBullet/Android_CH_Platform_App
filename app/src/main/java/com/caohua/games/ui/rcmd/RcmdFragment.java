package com.caohua.games.ui.rcmd;

import com.caohua.games.R;
import com.caohua.games.biz.gift.GiftEntry;
import com.caohua.games.biz.home.BannerEntry;
import com.caohua.games.biz.home.DataMgr;
import com.caohua.games.biz.hot.HotEntry;
import com.caohua.games.biz.openning.OpenningEntry;
import com.caohua.games.biz.ranking.RankingEntry;
import com.caohua.games.biz.rcmd.DailyRcmdEntry;
import com.caohua.games.biz.search.HotGameEntry;
import com.caohua.games.biz.topic.TopicEntry;
import com.caohua.games.ui.BaseFragment;
import com.caohua.games.ui.BasePageFragment;
import com.caohua.games.ui.HomePagerActivity;
import com.caohua.games.ui.gift.HomeGiftView;
import com.caohua.games.ui.hot.HomeHotView;
import com.caohua.games.ui.openning.HomeOpenningView;
import com.caohua.games.ui.ranking.HomeRankingView;
import com.caohua.games.ui.topic.HomeTopicView;
import com.caohua.games.ui.widget.HomeSubTitleView;
import com.caohua.games.ui.widget.banner.BannerView;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class RcmdFragment extends BaseFragment {
    private BannerView header;
    private HomeRcmdView recomBody;
    private HomeOpenningView openBody;
    private HomeHotView hotBody;
    private HomeGiftView giftBody;
    private HomeRankingView rankBody;
    private HomeTopicView topicBody;
    private HomeSubTitleView.OnMoreClickListener listener;
    private List<HotGameEntry> rcmdGames;
    private boolean isEventBusNotify;

    public RcmdFragment() {
    }

    public void setOnMoreClickListener(HomeSubTitleView.OnMoreClickListener listener) {
        this.listener = listener;
        if (openBody != null) {
            openBody.setOnMoreClickListener(listener);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ch_fragment_recommend;
    }

    @Override
    protected void initChildView() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        header = findView(R.id.ch_home_header);
        recomBody = findView(R.id.ch_recom_body);
        hotBody = findView(R.id.ch_hot_body);
        openBody = findView(R.id.ch_open_body);
        giftBody = findView(R.id.ch_gift_body);
        topicBody = findView(R.id.ch_project_body);
        rankBody = findView(R.id.ch_ranking_body);
        openBody.setOnMoreClickListener(listener);
//        header.setRefreshLayout(refreshLayout);
    }

    @Override
    protected List<LoadParams> getDataType() {
        List<LoadParams> types = new ArrayList<>();

        if (isEventBusNotify) {
            isEventBusNotify = false;

            LoadParams params = new LoadParams();
            params.requestType = DataMgr.DATA_TYPE_GIFT;
            types.add(params);
        } else {
            LoadParams params = new LoadParams();
            params.requestType = DataMgr.DATA_TYPE_BANNER;
            types.add(params);

            params = new LoadParams();
            params.requestType = DataMgr.DATA_TYPE_RCMD;
            types.add(params);

            BasePageFragment.LoadPageParams LoadPageParams = new BasePageFragment.LoadPageParams();
            LoadPageParams.requestType = DataMgr.DATA_TYPE_HOT;
            LoadPageParams.loadedCount = 0;
            LoadPageParams.requestCount = 10;
            types.add(LoadPageParams);

            params = new LoadParams();
            params.requestType = DataMgr.DATA_TYPE_GIFT;
            types.add(params);

            LoadPageParams = new BasePageFragment.LoadPageParams();
            LoadPageParams.requestType = DataMgr.DATA_TYPE_TOPIC;
            LoadPageParams.loadedCount = 0;
            LoadPageParams.requestCount = 10;
            types.add(LoadPageParams);

            params = new LoadParams();
            params.requestType = DataMgr.DATA_TYPE_RANKING;
            types.add(params);

            params = new LoadParams();
            params.requestType = DataMgr.DATA_TYPE_SEARCH_GAME;
            ;
            types.add(params);

            params = new LoadParams();
            params.requestType = DataMgr.DATA_TYPE_SERVER_OPEN;
            types.add(params);
        }
        return types;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (header == null) {
            return;
        }
        if (isVisibleToUser) {
            LogUtil.errorLog("RcmdFragment onResume");
            header.startRunnable();
        } else {
            LogUtil.errorLog("RcmdFragment onStop");
            header.removeRunnable();
        }
    }

    @Override
    protected void handleData(LoadParams param, Object o) {
        int type = param.requestType;
        if (type == DataMgr.DATA_TYPE_HOT) {
            hotBody.initData((List<HotEntry>) o);
        } else if (type == DataMgr.DATA_TYPE_RCMD) {
            recomBody.initData((List<DailyRcmdEntry>) o);
        } else if (type == DataMgr.DATA_TYPE_BANNER) {
            header.initData((List<BannerEntry>) o);
        } else if (type == DataMgr.DATA_TYPE_GIFT) {
            giftBody.initData((List<GiftEntry>) o);
        } else if (type == DataMgr.DATA_TYPE_RANKING) {
            rankBody.initData((List<RankingEntry>) o);
        } else if (type == DataMgr.DATA_TYPE_SERVER_OPEN) {
            openBody.initData((List<OpenningEntry>) o);
        } else if (type == DataMgr.DATA_TYPE_TOPIC) {
            topicBody.initData((List<TopicEntry>) o);
        } else if (type == DataMgr.DATA_TYPE_SEARCH_GAME) {
            if (rcmdGames == null || rcmdGames.size() == 0) {
                rcmdGames = (List<HotGameEntry>) o;
//                ((HomeFragment) getParentFragment()).setRcmdGame();
                if (getActivity() != null) {
                    ((HomePagerActivity) getActivity()).setRcmdGame(rcmdGames);
                }
            } else {
                if (getActivity() != null) {
                    ((HomePagerActivity) getActivity()).setRcmdGame(rcmdGames);
                }
                rcmdGames = (List<HotGameEntry>) o;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (hotBody != null) {
            hotBody.unregisterEventBus();
        }
        if (giftBody != null) {
            giftBody.unRegisterEvent();
        }

        if (recomBody != null) {
            recomBody.onDestroy();
        }
        if (rankBody != null) {
            rankBody.onDestroy();
        }
        if (openBody != null) {
            openBody.onDestroy();
        }
    }

    @Subscribe
    public void eventRefresh(LoginUserInfo info) {
        isEventBusNotify = true;
        loadData(false);
    }

    public List<HotGameEntry> getRcmdGames() {
        return rcmdGames;
    }
}
