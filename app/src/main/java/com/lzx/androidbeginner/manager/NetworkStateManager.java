package com.lzx.androidbeginner.manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.lzx.androidbeginner.utils.API;

/**
 * Created by lizhenxin on 17-3-24.
 * 网络连接情况管理类
 */

public class NetworkStateManager {
    public static int getNetworkState(Context context){
        if (context != null){
            ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (info != null){
                if (info.getType() == ConnectivityManager.TYPE_WIFI){
                    return API.WIFI_CONNEXT;
                }else if (info.getType() == ConnectivityManager.TYPE_MOBILE){
                    return API.MOBILE_CONNEXT;
                }
            }else {
                return API.NO_CONNEXT;
            }
        }
        return API.NO_CONNEXT;
    }
}
