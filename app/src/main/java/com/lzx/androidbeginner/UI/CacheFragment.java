package com.lzx.androidbeginner.UI;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lzx.androidbeginner.R;
import com.lzx.androidbeginner.manager.DBManager;
import com.lzx.androidbeginner.utils.API;


/**
 * Created by lizhenxin on 17-3-13.
 * 缓存文章的Fragment
 */

public class CacheFragment extends Fragment {

    RecyclerView recyclerView;

    RecyclerViewAdapter commonRecyclerViewAdapter = new RecyclerViewAdapter(MainActivity.cacheList);

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cache, container, false);

        recyclerView = (RecyclerView)view.findViewById(R.id.cache_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        new getArticleFromCache().execute();

        setHasOptionsMenu(true);
        return view;
    }


    private class getArticleFromCache extends AsyncTask<Void, Void, Void> implements RecyclerViewAdapter.OnItemClickListener {
        //从数据库里加载数据较多，用异步方式
        @Override
        protected Void doInBackground(Void... params) {
            DBManager dbTool = new DBManager(getActivity());
            MainActivity.cacheList = dbTool.getArticles();
            return null;
        }

        @Override
        protected void onPostExecute(Void s) {
            commonRecyclerViewAdapter = new RecyclerViewAdapter(MainActivity.cacheList);
            commonRecyclerViewAdapter.setOnItemClickListener(this);
            recyclerView.setAdapter(commonRecyclerViewAdapter);
        }

        @Override
        public void onClick(View view, int position) {
            Intent intent = new Intent(getActivity(), ContentActivity.class);
            intent.putExtra("position", position);
            intent.putExtra("which", API.GET_CACHE);
            getActivity().startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        new getArticleFromCache().execute();
        super.onResume();
    }
}
