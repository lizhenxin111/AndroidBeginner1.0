package com.lzx.androidbeginner.UI;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.lzx.androidbeginner.R;
import com.lzx.androidbeginner.manager.DBManager;
import com.lzx.androidbeginner.manager.FragmentsManager;
import com.lzx.androidbeginner.utils.API;
import com.lzx.androidbeginner.utils.Article;
import java.lang.reflect.Field;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ContentActivity extends AppCompatActivity {

    Article article;
    WebView webView = null;
    private SetWebView setWebView = new SetWebView();

    private String getHtml(){
        Intent intent = getIntent();
        int which = intent.getIntExtra("which", 0);
        int position = intent.getIntExtra("position", 0);

        String htmlCssDay = "<head>\n" +
                "<meta name=\"viewport\" content=\"width=device-width,initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no\">" +
                "<meta content=\"IE=edge\" http-equiv=\"X-UA-Compatible\">" +
                "<style type=\"text/css\">\n" +
                "body{\n" +
                "word-wrap:break-word;\n" +
                "font-family:Arial;\n" +
                "}" +
                "img{\n" +
                "max-width:99%;\n" +
                "height:auto;\n" +
                "}\n" +
                "</style>\n" +
                "</head>";
        String htmlCssNight = "<head>\n" +
                "<meta name=\"viewport\" content=\"width=device-width,initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no\">" +
                "<meta content=\"IE=edge\" http-equiv=\"X-UA-Compatible\">" +
                "<style type=\"text/css\">\n" +
                "body{\n" +
                "word-wrap:break-word;\n" +
                "font-family:Arial;" +
                "background: #222222;\n" +
                "color: #bbbbbb;\n" +
                "line-height: 1.3\n" +
                "}\n" +
                "img{\n" +
                "z-index:2;\n" +
                "opacity:0.4;\n" +
                "max-width:99%;\n" +
                "height:auto;\n" +
                "}\n" +
                "a {\n" +
                "  background-color: transparent;\n" +
                "  color:#9999d6;\n" +
                "}\n" +
                "</style>\n" +
                "</head>";

        String url = new String();
        String title = new String();
        switch (which){
            case API.GET_JAVA:
                title = MainActivity.javaList.get(position).getTitle();
                url = MainActivity.javaList.get(position).getUrl();
                break;
            case API.GET_ANDROID:
                title = MainActivity.androidList.get(position).getTitle();
                url = MainActivity.androidList.get(position).getUrl();
                break;
            case API.GET_SENIOR:
                title = MainActivity.seniorList.get(position).getTitle();
                url = MainActivity.seniorList.get(position).getUrl();
                break;
            case API.GET_CACHE:
                title = MainActivity.cacheList.get(position).getTitle();
                url = MainActivity.cacheList.get(position).getUrl();
        }
        //StringBuilder html = new StringBuilder();
        String html = null;
        if (which != API.GET_CACHE){
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                Response response = client.newCall(request).execute();
                html = response.body().string();
                //html.append(response.body().string());

                int start = html.indexOf("<article id");
                int end = html.indexOf("</article>") + 10;
                html = html.substring(start, end);      //截取文章部分

                end = html.indexOf("<div class=\"page-reward");

                //去掉赞赏按钮
                //增加Style标签使图片不超过屏幕。完整html有该设置但被删除
                html = html.substring(0, end) + "</div></article>";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            html = MainActivity.cacheList.get(position).getHtml();
        }
        article = new Article(title, url, html);
        html = MainActivity.isNightMode == false ? htmlCssDay + html : htmlCssNight + html;

        return html;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_content);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setWebView.execute();
    }

    class SetWebView extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            FragmentsManager tool = new FragmentsManager();
            tool.addLoading(ContentActivity.this, R.id.article_webview);

            webView = (WebView)findViewById(R.id.article_webview);
            webView.getSettings().setLoadsImagesAutomatically(true);        //后加载图片
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);        //关闭硬件加速，解决夜间模式白屏问题
        }

        @Override
        protected String doInBackground(Void... params) {
            return getHtml();
        }

        @Override
        protected void onPostExecute(final String html) {
            //String url = article.getUrl();
            webView.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageFinished(WebView view, String url) {
                    if (!webView.getSettings().getLoadsImagesAutomatically()){
                        webView.getSettings().setLoadsImagesAutomatically(true);
                    }

                    FragmentsManager tool = new FragmentsManager();
                    tool.removeLoading(ContentActivity.this);
                }
            });
            webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_article, menu);
        Intent intent = getIntent();
        int op = intent.getIntExtra("which", 0);
        if (op == 3){
            menu.setGroupVisible(R.id.menu_group_download, false);
        }else {
            menu.setGroupVisible(R.id.menu_group_delete, false);
        }
        super.onPrepareOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_download) {
            String title = article.getTitle();
            String url = article.getUrl();
            String html = article.getHtml();
            DBManager dbTool = new DBManager(this);
            long result = dbTool.insertArticle(title, url, html);
            String message = result == 0 ? "已经存在" : "下载完成";
            LinearLayout ll = (LinearLayout)findViewById(R.id.content_content);
            Snackbar snackbar = Snackbar.make(ll, message, Snackbar.LENGTH_SHORT);
            snackbar.show();
        }else if (id == R.id.action_delete){
            String title = article.getTitle();
            DBManager dbTool = new DBManager(this);
            dbTool.deleteArticle(title);
            LinearLayout ll = (LinearLayout)findViewById(R.id.content_content);
            Snackbar snackbar = Snackbar.make(ll, "删除成功", Snackbar.LENGTH_SHORT);
            snackbar.show();
            Log.d("Main", "delete");
            this.finish();
        }else if (id == android.R.id.home){
            this.finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.getSettings().setJavaScriptEnabled(false);
        try {
            Field sConfigCallback = Class.forName("android.webkit.BrowserFrame").getDeclaredField("sConfigCallback");
            if (sConfigCallback != null){
                sConfigCallback.setAccessible(true);
                sConfigCallback.set(null, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
