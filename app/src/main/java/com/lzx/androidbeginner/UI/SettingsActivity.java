package com.lzx.androidbeginner.UI;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuItem;
import android.view.View;
import com.lzx.androidbeginner.R;
import com.lzx.androidbeginner.Service.DownloadService;
import com.lzx.androidbeginner.manager.UpdateManager;
import java.io.File;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity implements Preference.OnPreferenceClickListener, SharedPreferences.OnSharedPreferenceChangeListener {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    LocalReceiver localReceiver = null;
    String u = null;
    private boolean ifBindService = false;
    private boolean ifRegidterReceiver = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addPreferencesFromResource(R.xml.pref_settings);

        findPreference("checkUpdate").setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            this.finish();
        }
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("isNightMode")){
            boolean isNightMode = sharedPreferences.getBoolean("isNightMode", false);
            if (isNightMode){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            Snackbar snackbar = Snackbar.make(getCurrentFocus(), "返回主界面即可看到效果", Snackbar.LENGTH_INDEFINITE)
                    .setAction("返回", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
            snackbar.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceClick(android.preference.Preference preference) {
        UpdateManager update = new UpdateManager();
        final String url = update.checkUpdate();
        u = url;
        if (url != null){
            Intent setviceIntent = new Intent(SettingsActivity.this, DownloadService.class);
            bindService(setviceIntent, connection, BIND_AUTO_CREATE); // 绑定服务
            ifBindService = true;

            Snackbar snackbar = Snackbar.make(getCurrentFocus(), "有新版本", Snackbar.LENGTH_INDEFINITE)
                    .setAction("更新", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            downloadBinder.startDownload(url);
                            ifRegidterReceiver = true;
                            Snackbar s = Snackbar.make(getCurrentFocus(), "正在下载", Snackbar.LENGTH_INDEFINITE);
                            s.show();
                        }
                    });
            snackbar.show();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.lizhenxin.broadcast.DOWNLOAD_COMPLETE");
            localReceiver = new LocalReceiver();
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(localReceiver, intentFilter);
        }else {
            Snackbar snackbar = Snackbar.make(getCurrentFocus(), "没有新版本", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
        return true;
    }

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Snackbar snackbar = Snackbar.make(getCurrentFocus(), "下载完成", Snackbar.LENGTH_INDEFINITE)
                    .setAction("点击安装", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String fileName = u.substring(u.lastIndexOf("/"));
                            String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                            File file = new File(directory + fileName);

                            if (!file.exists())
                            {
                                return;
                            }
                            // 通过Intent安装APK文件
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setDataAndType(Uri.parse("file://" + file.toString()), "application/vnd.android.package-archive");
                            startActivity(i);
                        }
                    });
            snackbar.show();
        }
    }

    private DownloadService.DownloadBinder downloadBinder;

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            downloadBinder.cancelDownload();
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownloadService.DownloadBinder) service;
        }

    };

    @Override
    public void onBackPressed() {
        if (ifBindService == true){
            unbindService(connection);
            connection.onServiceDisconnected(null);
            Snackbar snackbar = Snackbar.make(getCurrentFocus(), "取消更新", Snackbar.LENGTH_LONG);
            snackbar.show();
            ifBindService = false;
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (ifBindService == true){
            unbindService(connection);
            ifBindService = false;
        }
        if (ifRegidterReceiver == true){
            unregisterReceiver(localReceiver);
            ifRegidterReceiver = false;
        }
        super.onDestroy();
    }
}
