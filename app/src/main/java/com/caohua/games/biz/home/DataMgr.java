package com.caohua.games.biz.home;

import android.content.Context;

import com.caohua.games.biz.account.MyFavoriteEntry;
import com.caohua.games.biz.gift.GiftEntry;
import com.caohua.games.biz.gift.GiftModel;
import com.caohua.games.biz.hot.HotEntry;
import com.caohua.games.biz.mymsg.MyMsgEntry;
import com.caohua.games.biz.openning.OpenningEntry;
import com.caohua.games.biz.ranking.RankingEntry;
import com.caohua.games.biz.ranking.RankingTotalSubEntry;
import com.caohua.games.biz.rcmd.DailyRcmdEntry;
import com.caohua.games.biz.search.AssigCatEntry;
import com.caohua.games.biz.search.SearchGameEntry;
import com.caohua.games.biz.search.HotGameEntry;
import com.caohua.games.biz.topic.TopicDetailEntry;
import com.caohua.games.biz.topic.TopicEntry;
import com.caohua.games.ui.BaseFragment;
import com.caohua.games.ui.BasePageFragment;
import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.app.AppLogic;
import com.chsdk.biz.app.RankingLogic;
import com.chsdk.configure.SdkSession;
import com.chsdk.model.BaseModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ZengLei on 2016/10/19.
 */

public class DataMgr {
    public static final int DATA_TYPE_BANNER = 1;
    private static final String BANNER_FILE_NAME = "banner";
    private static final String BANNER_URL = "main/bannerAd";

    public static final int DATA_TYPE_RCMD = 2;
    private static final String DAILY_RCMD_FILE_NAME = "daily_rcmd";
    private static final String DAILY_RCMD_URL = "main/dailyGame";

    public static final int DATA_TYPE_HOT = 3;
    private static final String HOT_FILE_NAME = "hot";
    private static final String HOT_URL = "main/hotActivity";

    public static final int DATA_TYPE_GIFT = 4;
    private static final String GIFT_FILE_NAME = "gift";
    private static final String GIFT_URL = "main/giftCenter";

    public static final int DATA_TYPE_TOPIC = 5;
    private static final String TOPIC_FILE_NAME = "topic";
    private static final String TOPIC_URL = "main/gameSubject";

    public static final int DATA_TYPE_RANKING = 6;
    private static final String RANKING_FILE_NAME = "ranking";
    private static final String RANKING_URL = "main/multipleRank";

    public static final int DATA_TYPE_SERVER_OPEN = 7;
    private static final String SERVER_OPEN_FILE_NAME = "server_open";
    private static final String SERVER_OPEN_URL = "main/serverOpen";

    public static final int DATA_TYPE_RANKING_TOTAL = 8;
    private static final String RANKING_TOTAL_FILE_NAME = "ranking_total";
    private static final String RANKING_TOTAL_URL = "rank/index";

    public static final int DATA_TYPE_OPENNING_TOTAL = 9;
    private static final String OPENNING_TOTAL_FILE_NAME = "openning_total";
    private static final String OPENNING_TOTAL_URL = "server/index";

    public static final int DATA_TYPE_RANKING_SYNTH = 10;
    public static final String RANKING_SYNTH_FILE_NAME = "ranking_synth";
    public static final String RANKING_SYNTH_URL = "rank/multipleGame";

    public static final int DATA_TYPE_RANKING_NEW_GAME = 11;
    public static final String RANKING_NEW_GAME_FILE_NAME = "ranking_new_game";
    public static final String RANKING_NEW_GAME_URL = "rank/newGame";

    public static final int DATA_TYPE_RANKING_RECOMM = 12;
    public static final String RANKING_RECOMM_FILE_NAME = "ranking_recomm";
    public static final String RANKING_RECOMM_URL = "rank/recommendGame";

    public static final int DATA_TYPE_RANKING_NEW = 13;
    public static final String RANKING_NEW_FILE_NAME = "ranking_new";
    public static final String RANKING_NEW_URL = "rank/onlineGame";

    public static final int DATA_TYPE_SEARCH_GAME = 14;
    public static final String SEARCH_GAME_FILE_NAME = "search_game";
    public static final String SEARCH_GAME_URL = "game/hotGame";

    public static final int DATA_TYPE_ASSIGN_GAME = 15;
    public static final String ASSIGN_GAME_FILE_NAME = "assign_game";
    public static final String ASSIGN_GAME_URL = "game/searchGame";

    public static final int DATA_TYPE_ASSICAT_GAME = 16;
    public static final String ASSICAT_GAME_FILE_NAME = "assicat_game";
    public static final String ASSICAT_GAME_URL = "game/searchAssociation";

    public static final int DATA_TYPE_TOPIC_DETAIL = 17;
    private static final String TOPIC_DETAIL_FILE_NAME = "topic_detail";
    private static final String TOPIC_DETAIL_URL = "main/subjectGame";

    public static final int DATA_TYPE_MY_MSG = 18;
    private static final String MY_MSG_FILE_NAME = "my_msg";
    private static final String MY_MSG_URL = "forum/userMsgList";

    public static final int DATA_TYPE_MY_FAVORITE = 19;
    private static final String MY_FAVORITE_FILE_NAME = "my_favorite";
    private static final String MY_FAVORITE_URL = "forum/collectList";

    public static List<? extends Serializable> getLocalData(Context context, int type) {
        String fileName = null;
        if (type == DATA_TYPE_BANNER) {
            fileName = BANNER_FILE_NAME;
        } else if (type == DATA_TYPE_RCMD) {
            fileName = DAILY_RCMD_FILE_NAME;
        } else if (type == DATA_TYPE_HOT) {
            fileName = HOT_FILE_NAME;
        } else if (type == DATA_TYPE_GIFT) {
            fileName = GIFT_FILE_NAME;
        } else if (type == DATA_TYPE_TOPIC) {
            fileName = TOPIC_FILE_NAME;
        } else if (type == DATA_TYPE_SERVER_OPEN) {
            fileName = SERVER_OPEN_FILE_NAME;
        } else if (type == DATA_TYPE_RANKING) {
            fileName = RANKING_FILE_NAME;
        } else if (type == DATA_TYPE_RANKING_TOTAL) {
            fileName = RANKING_TOTAL_FILE_NAME;
        } else if (type == DATA_TYPE_OPENNING_TOTAL) {
            fileName = OPENNING_TOTAL_FILE_NAME;
        } else if (type == DATA_TYPE_RANKING_SYNTH) {
            fileName = RANKING_SYNTH_FILE_NAME;
        } else if (type == DATA_TYPE_RANKING_NEW_GAME) {
            fileName = RANKING_NEW_GAME_FILE_NAME;
        } else if (type == DATA_TYPE_RANKING_RECOMM) {
            fileName = RANKING_RECOMM_FILE_NAME;
        } else if (type == DATA_TYPE_RANKING_NEW) {
            fileName = RANKING_NEW_FILE_NAME;
        } else if (type == DATA_TYPE_SEARCH_GAME) {
            fileName = SEARCH_GAME_FILE_NAME;
        } else if (type == DATA_TYPE_ASSIGN_GAME) {
            fileName = ASSIGN_GAME_FILE_NAME;
        } else if (type == DATA_TYPE_ASSICAT_GAME) {
            fileName = ASSICAT_GAME_FILE_NAME;
        } else if (type == DATA_TYPE_TOPIC_DETAIL) {
            fileName = TOPIC_DETAIL_FILE_NAME;
        } else if (type == DATA_TYPE_MY_MSG) {
            fileName = MY_MSG_FILE_NAME;
        } else if (type == DATA_TYPE_MY_FAVORITE) {
            fileName = MY_FAVORITE_FILE_NAME;
        }
        return AppLogic.getLocalData(context, fileName);
    }

    public static void getPageServerData(Context context, BaseLogic.AppLogicListner listener,
                                         BaseFragment.LoadParams loadParams) {
        AppLogic.Params params = new AppLogic.Params();
        params.listner = listener;
        int type = loadParams.requestType;
        if (loadParams instanceof BasePageFragment.LoadPageParams) {
            BasePageFragment.LoadPageParams loadPageParams = (BasePageFragment.LoadPageParams) loadParams;
            params.append = loadPageParams.loadedCount > 0;
            params.model = new RequestPageModel(String.valueOf(loadPageParams.requestCount), String.valueOf(loadPageParams.loadedCount));
            if (loadParams.requestType == DATA_TYPE_HOT) {
                params.saveFileName = HOT_FILE_NAME;
                params.url = HOT_URL;
                params.cls = HotEntry.class;
            } else if (loadParams.requestType == DATA_TYPE_RANKING_SYNTH) {
                params.saveFileName = RANKING_SYNTH_FILE_NAME;
                params.url = RANKING_SYNTH_URL;
                params.cls = RankingEntry.class;
            } else if (loadParams.requestType == DATA_TYPE_RANKING_NEW_GAME) {
                params.saveFileName = RANKING_NEW_GAME_FILE_NAME;
                params.url = RANKING_NEW_GAME_URL;
                params.cls = RankingEntry.class;
            } else if (loadParams.requestType == DATA_TYPE_RANKING_RECOMM) {
                params.saveFileName = RANKING_RECOMM_FILE_NAME;
                params.url = RANKING_RECOMM_URL;
                params.cls = RankingEntry.class;
            } else if (loadParams.requestType == DATA_TYPE_RANKING_NEW) {
                params.saveFileName = RANKING_NEW_FILE_NAME;
                params.url = RANKING_NEW_URL;
                params.cls = RankingEntry.class;
            } else if (loadParams.requestType == DATA_TYPE_TOPIC) {
                params.saveFileName = TOPIC_FILE_NAME;
                params.url = TOPIC_URL;
                params.cls = TopicEntry.class;
            } else if (loadParams.requestType == DATA_TYPE_TOPIC_DETAIL) {
                TopicDetailModel model = new TopicDetailModel(String.valueOf(loadPageParams.requestCount),
                        String.valueOf(loadPageParams.loadedCount));
                model.setId((String) loadPageParams.object);
                params.model = model;
                params.saveFileName = TOPIC_DETAIL_FILE_NAME;
                params.url = TOPIC_DETAIL_URL;
                params.cls = TopicDetailEntry.class;
            } else if (loadParams.requestType == DATA_TYPE_MY_MSG) {
                params.model.getDataMap().put("ui", SdkSession.getInstance().getUserId());
                params.model.getDataMap().put("t", SdkSession.getInstance().getToken());
                params.saveFileName = MY_MSG_FILE_NAME;
                params.url = MY_MSG_URL;
                params.cls = MyMsgEntry.class;
            } else if (loadPageParams.requestType == DATA_TYPE_MY_FAVORITE) {
                params.model.getDataMap().put("ui", SdkSession.getInstance().getUserId());
                params.model.getDataMap().put("t", SdkSession.getInstance().getToken());
                params.saveFileName = MY_FAVORITE_FILE_NAME;
                params.url = MY_FAVORITE_URL;
                params.cls = MyFavoriteEntry.class;
            }
        } else {
            params.model = new BaseModel();
            if (type == DATA_TYPE_BANNER) {
                params.saveFileName = BANNER_FILE_NAME;
                params.url = BANNER_URL;
                params.cls = BannerEntry.class;
            } else if (type == DATA_TYPE_RCMD) {
                params.saveFileName = DAILY_RCMD_FILE_NAME;
                params.url = DAILY_RCMD_URL;
                params.cls = DailyRcmdEntry.class;
            } else if (type == DATA_TYPE_GIFT) {
                params.model = new GiftModel();
                params.saveFileName = GIFT_FILE_NAME;
                params.url = GIFT_URL;
                params.cls = GiftEntry.class;
            } else if (type == DATA_TYPE_SERVER_OPEN) {
                params.saveFileName = SERVER_OPEN_FILE_NAME;
                params.url = SERVER_OPEN_URL;
                params.cls = OpenningEntry.class;
            } else if (type == DATA_TYPE_RANKING) {
                params.saveFileName = RANKING_FILE_NAME;
                params.url = RANKING_URL;
                params.cls = RankingEntry.class;
            } else if (type == DATA_TYPE_RANKING_TOTAL) {
                params.saveFileName = RANKING_TOTAL_FILE_NAME;
                params.url = RANKING_TOTAL_URL;
                params.cls = RankingTotalSubEntry.class;
                new RankingLogic<RankingTotalSubEntry>(context, params).getData();
                return;
            } else if (type == DATA_TYPE_OPENNING_TOTAL) {
                params.saveFileName = OPENNING_TOTAL_FILE_NAME;
                params.url = OPENNING_TOTAL_URL;
                params.cls = OpenningEntry.class;
            } else if (type == DATA_TYPE_SEARCH_GAME) {
                params.saveFileName = SEARCH_GAME_FILE_NAME;
                params.url = SEARCH_GAME_URL;
                params.cls = HotGameEntry.class;
            } else if (type == DATA_TYPE_ASSIGN_GAME) {
                params.saveFileName = ASSIGN_GAME_FILE_NAME;
                params.url = ASSIGN_GAME_URL;
                params.cls = SearchGameEntry.class;
            } else if (type == DATA_TYPE_ASSICAT_GAME) {
                params.saveFileName = ASSICAT_GAME_FILE_NAME;
                params.url = ASSICAT_GAME_URL;
                params.cls = AssigCatEntry.class;
            }
        }
        new AppLogic(context, params).getData();
    }
}
