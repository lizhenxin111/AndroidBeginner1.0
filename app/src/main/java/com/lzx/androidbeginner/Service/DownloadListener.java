package com.lzx.androidbeginner.Service;


/**
 * Created by lizhenxin on 17-3-23.
 * 更新应用的下载接口
 */

public interface DownloadListener {
    void onProgress(int progress);

    void onSuccess();

    void onFailed();

    void onPaused();

    void onCanceled();
}
