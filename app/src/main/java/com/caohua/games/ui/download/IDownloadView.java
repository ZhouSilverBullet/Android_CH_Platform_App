package com.caohua.games.ui.download;

/**
 * Created by admin on 2016/11/26.
 */

public interface IDownloadView {
    void setDownloadingText(String text);
    void setProgress(int progress);
    void setPause();
    void setError();
    void setDownloading();
}
