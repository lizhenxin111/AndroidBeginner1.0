package com.lzx.androidbeginner.manager;

import android.content.Context;
import android.content.SharedPreferences;
import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * Created by lizhenxin on 17-3-24.
 * 权限管理类
 */

public class PreferenceManager {
    public static Boolean getIfSaveArticle(Context context){
        SharedPreferences sharedPref = getDefaultSharedPreferences(context);
        boolean value = sharedPref.getBoolean("isSave", false);
        return value;
    }
}
