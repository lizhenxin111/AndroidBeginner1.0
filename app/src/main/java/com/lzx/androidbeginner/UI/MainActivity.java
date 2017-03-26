package com.lzx.androidbeginner.UI;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import com.lzx.androidbeginner.R;
import com.lzx.androidbeginner.manager.FragmentsManager;
import com.lzx.androidbeginner.manager.NetworkStateManager;
import com.lzx.androidbeginner.utils.API;
import com.lzx.androidbeginner.utils.Article;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static boolean isNightMode = false;
    public static Stack<Fragment> fragmentStack = new Stack<>();

    public static List<Article> javaList = new ArrayList<>();
    public static List<Article> androidList = new ArrayList<>();
    public static List<Article> seniorList = new ArrayList<>();
    public static List<Article> cacheList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        isNightMode = preferences.getBoolean("isNightMode", false);
        if (isNightMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            isNightMode = true;
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            isNightMode = false;
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("列表");



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Resources resources = (Resources)getBaseContext().getResources();
        ColorStateList colorStateList = (ColorStateList)resources.getColorStateList(R.color.colorText);
        navigationView.setItemTextColor(colorStateList);
        navigationView.setItemIconTintList(colorStateList);

        checkNetwokState();

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE }, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Snackbar snackbar = Snackbar.make(getCurrentFocus(), "无法获取存储权限", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    finish();
                }
                break;
            default:
        }
    }

    private void checkNetwokState(){
        int isNetConn = NetworkStateManager.getNetworkState(this);
        if (isNetConn == API.NO_CONNEXT){
            LinearLayout ll = (LinearLayout)findViewById(R.id.content_main);
            Snackbar snackbar = Snackbar.make(ll , "无可用网络", Snackbar.LENGTH_INDEFINITE)
                    .setAction("查看保存的文章", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FragmentsManager tool = new FragmentsManager();
                            tool.changeFragment(MainActivity.this, new CacheFragment());
                        }
                    });
            snackbar.show();
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkNetwokState();
                }
            });
        }else {
            FragmentsManager tool = new FragmentsManager();
            tool.changeFragment(MainActivity.this, new BlogFragment());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        FragmentsManager tool = new FragmentsManager();
        if (id == R.id.nav_blog) {
            tool.changeFragment(MainActivity.this, new BlogFragment());
            getSupportActionBar().setTitle("列表");
        } else if (id == R.id.nav_cache) {
            tool.changeFragment(MainActivity.this, new CacheFragment());
            getSupportActionBar().setTitle("缓存");
        } else if (id == R.id.nav_setting) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        } else if (id == R.id.nav_share) {
            tool.changeFragment(MainActivity.this, new ShareFragment());
            getSupportActionBar().setTitle("分享");
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
