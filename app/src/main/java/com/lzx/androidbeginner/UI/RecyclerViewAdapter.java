package com.lzx.androidbeginner.UI;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lzx.androidbeginner.R;
import com.lzx.androidbeginner.utils.Article;

import java.util.List;

/**
 * Created by lizhenxin on 17-3-8.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    List<Article> mArticleList;

    public RecyclerViewAdapter(List<Article> articleList){
        mArticleList = articleList;
    }

    public interface OnItemClickListener{
        void onClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.mOnItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, url;
        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.item_title);
            url = (TextView)itemView.findViewById(R.id.item_url);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Article article = mArticleList.get(position);
        holder.title.setText(article.getTitle());
        holder.url.setText(article.getUrl());

        if (mOnItemClickListener != null){
            holder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();
                    mOnItemClickListener.onClick(holder.title, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mArticleList.size();
    }
}
