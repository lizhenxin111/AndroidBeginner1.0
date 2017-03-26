package com.lzx.androidbeginner.manager;

import com.lzx.androidbeginner.utils.API;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lizhenxin on 17-3-24.
 * 更新管理类
 */

public class UpdateManager {
    public String checkUpdate(){
        CallableClass callableClass = new CallableClass();
        FutureTask<String> futureTask = new FutureTask<String>(callableClass);
        Thread thread = new Thread(futureTask);
        thread.start();
        String url = null;
        try {
            url = futureTask.get();
        }catch (Exception e){
            e.printStackTrace();;
        }
        return url;
    }

    class CallableClass implements Callable<String> {


        @Override
        public String call() throws Exception {
            String version = null;
            String url = null;
            try{
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("https://raw.githubusercontent.com/lizhenxin111/ApkStore/master/update.json")
                        .build();
                Response response = client.newCall(request).execute();
                String data = response.body().string();

                JSONArray jsonArray = new JSONArray(data);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                version = jsonObject.getString("version");
                if (Double.valueOf(version) > API.CURRENT_VERSION){
                    url = jsonObject.getString("url");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return url;
        }
    }
}
