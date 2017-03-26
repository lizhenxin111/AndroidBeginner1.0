package com.lzx.androidbeginner.UI;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lzx.androidbeginner.R;
import com.lzx.androidbeginner.db.DBHelper;
import com.lzx.androidbeginner.manager.FragmentsManager;
import com.lzx.androidbeginner.manager.NetworkStateManager;
import com.lzx.androidbeginner.utils.API;
import com.lzx.androidbeginner.utils.Article;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lizhenxin on 17-3-13.
 * 显示在线博客的Fragment
 */

public class BlogFragment extends Fragment implements TabLayout.OnTabSelectedListener{

    RecyclerView recyclerView;
    RecyclerViewAdapter commonRecyclerViewAdapter;
    private InitArticleList initArticleList = new InitArticleList();
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == API.DL_JAVA){
                commonRecyclerViewAdapter = new RecyclerViewAdapter(MainActivity.javaList);
            }else if (msg.what == API.DL_ANDROID){
                commonRecyclerViewAdapter = new RecyclerViewAdapter(MainActivity.androidList);
            }else if (msg.what == API.DL_SENIOR){
                commonRecyclerViewAdapter = new RecyclerViewAdapter(MainActivity.seniorList);
            }

            commonRecyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onClick(View view, int position) {
                    startContentActivity(view, position);
                }
            });
            recyclerView.setAdapter(commonRecyclerViewAdapter);
        }
    };

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch ((String)tab.getText()){
            case "Java":
                if (MainActivity.javaList.size() == 0){
                    new InitArticleList().execute(API.DL_JAVA + API.DOWNLOAD_ARTICLE_LIST);
                }else {
                    Message msg = new Message();
                    msg.what = API.DL_JAVA;
                    handler.sendMessage(msg);
                }

                break;
            case "Android":
                if (MainActivity.androidList.size() == 0){
                    new InitArticleList().execute(API.DL_ANDROID + API.DOWNLOAD_ARTICLE_LIST);
                }else {
                    Message msg = new Message();
                    msg.what = API.DL_ANDROID;
                    handler.sendMessage(msg);
                }
                break;
            case "进阶":
                if (MainActivity.seniorList.size() == 0){
                    new InitArticleList().execute(API.DL_SENIOR + API.DOWNLOAD_ARTICLE_LIST);
                }else {
                    Message msg = new Message();
                    msg.what = API.DL_SENIOR;
                    handler.sendMessage(msg);
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        //refresh the list
        int isNetConn = NetworkStateManager.getNetworkState(getContext());
        if (isNetConn == API.NO_CONNEXT){
            Snackbar snackbar = Snackbar
                    .make(getActivity().getCurrentFocus(), "网络被狗吃了……", Snackbar.LENGTH_LONG);
            snackbar.show();
        }else {
            switch ((String)tab.getText()){
                case "Java":
                    new InitArticleList().execute(API.DL_JAVA+API.UPDATE_ARTICLE_LIST);
                    break;
                case "Android":
                    new InitArticleList().execute(API.DL_ANDROID+API.UPDATE_ARTICLE_LIST);
                    break;
                case "进阶":
                    new InitArticleList().execute(API.DL_SENIOR+API.UPDATE_ARTICLE_LIST);
                    break;
                default:
                    break;
            }
        }
    }



    class InitArticleList extends AsyncTask<Integer, Void, Integer> implements RecyclerViewAdapter.OnItemClickListener {
        //下载博客目录
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            FragmentsManager tool = new FragmentsManager();
            tool.addLoading(BlogFragment.this, R.id.blog_fragment_container);
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            List<Article> articleList = new ArrayList<>();

            int which = params[0]/10*10;
            int isUpdate = params[0]%10;
            if (which == API.DL_JAVA){
                articleList = MainActivity.javaList;
            }else if (which == API.DL_ANDROID){
                articleList = MainActivity.androidList;
            }else if (which == API.DL_SENIOR){
                articleList = MainActivity.seniorList;
            }

            if (articleList.size() == 0 || isUpdate == API.UPDATE_ARTICLE_LIST){
                articleList.clear();

                String u = new String();
                if (which == API.DL_JAVA){
                    u = API.URL_JAVA;
                }else if (which == API.DL_ANDROID){
                    u = API.URL_ANDROID;
                }else if (which == API.DL_SENIOR){
                    u = API.URL_SENIOR;
                }
                try{
                    Document doc = Jsoup.connect(u).get();
                    if (doc != null){
                        Elements articles = doc.getElementsByAttributeValue("class", "archive-article archive-type-post");
                        for (Element article : articles){
                            //获取标题
                            Elements eTitle = article.getElementsByAttributeValue("class", "archive-article-title");
                            String titleTmp = eTitle.text();    //标题汉字
                            String title = URLEncoder.encode(titleTmp, "UTF-8");    //转换为UTF-8以打开网页
                            //获取链接，链接由时间+标题组成
                            Elements eTime = article.getElementsByTag("time");
                            String timeTmp = eTime.attr("datetime");
                            String timeTmp1 = timeTmp.substring(0, 10);      //截取时间
                            String time = timeTmp1.replaceAll("-", "/");    //将-全部转化为/
                            String url = API.BASE_URL + "/" + time + "/" + title;

                            Article dataClass = new Article(titleTmp, url);
                            articleList.add(dataClass);
                        }
                    }

                    if (which == API.DL_JAVA){
                        MainActivity.javaList = articleList;
                    }else if (which == API.DL_ANDROID){
                        MainActivity.androidList = articleList;
                    }else if (which == API.DL_SENIOR){
                        MainActivity.seniorList = articleList;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return which;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            Message message = new Message();
            message.what = integer;
            handler.sendMessage(message);

            if (getFragmentManager() != null){
                FragmentsManager tool = new FragmentsManager();
                tool.removeLoading(BlogFragment.this);
            }
        }

        @Override
        public void onClick(View view, int position) {
            startContentActivity(view, position);
        }
    }

    private void startContentActivity(View view, int position){
        TabLayout tabLayout = (TabLayout)getActivity().findViewById(R.id.main_tablayout);
        Intent intent = new Intent(getActivity(), ContentActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("which", tabLayout.getSelectedTabPosition());
        getActivity().startActivity(intent);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blog, container, false);
        DBHelper dbHelper = new DBHelper(getContext());
        dbHelper.getWritableDatabase();

        TabLayout tabLayout = (TabLayout)view.findViewById(R.id.main_tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("Java"));
        tabLayout.addTab(tabLayout.newTab().setText("Android"));
        tabLayout.addTab(tabLayout.newTab().setText("进阶"));
        tabLayout.addOnTabSelectedListener(this);

        recyclerView = (RecyclerView)view.findViewById(R.id.main_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        initArticleList.execute(API.DL_JAVA);

        return view;
    }
}
