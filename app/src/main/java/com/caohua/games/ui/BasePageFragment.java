package com.caohua.games.ui;

public abstract class BasePageFragment extends BaseFragment {

    protected int loadedCount;

    protected void moreLoad() {
        loadData(false);
    }

    protected void freshLoad() {
        loadedCount = 0;
        super.freshLoad();
    }

    protected abstract void loadMoreFinish(boolean success);

    protected abstract void handleData(int type, boolean loadMore, Object data);

    protected LoadPageParams getLoadPageParams(int type) {
        LoadPageParams params = new LoadPageParams();
        params.requestType = type;
        params.loadedCount = loadedCount;
        params.requestCount = 10;
        return params;
    }

    @Override
    protected void handleData(LoadParams params, Object data) {
        if (params instanceof LoadPageParams) {
            LoadPageParams pageParams = (LoadPageParams) params;
            handleData(pageParams.requestType, pageParams.loadedCount > 0, data);
        } else {
            handleData(params, data);
        }
    }

    protected void serverLoadResult(boolean success) {
        loadMoreFinish(success);
        super.serverLoadResult(success);
    }

    public static class LoadPageParams extends LoadParams {
        public int requestCount;
        public int loadedCount;
    }
}