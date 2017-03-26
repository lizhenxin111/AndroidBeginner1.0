package com.lzx.androidbeginner.utils;

/**
 * Created by lizhenxin on 17-3-8.
 * 一篇文章的自定义类
 */

public class Article {
    private String title;
    private String url;
    private String html;

    public Article(String title, String url){
        this.title = title;
        this.url = url;
    }

    public Article(String title, String url, String html){
        this.title = title;
        this.url = url;
        this.html = html;
    }

    public String getTitle(){
        return this.title;
    }
    public String getUrl(){
        return this.url;
    }
    public String getHtml(){
        return this.html;
    }
}
