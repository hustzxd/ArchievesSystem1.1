package com.example.hustzxd.archievessystem11.VolleyUtils;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * 全局的请求队列
 * Created by buxiaoyao on 2016/7/7.
 */
public class MyApplication extends Application {

    //  JSESSIONID=D42DBC504336EB3C555930B8EBA1D503
    public static volatile String cookies;
    
    //请求队列
    private static RequestQueue mQueues;

    @Override
    public void onCreate() {
        super.onCreate();
        mQueues = Volley.newRequestQueue(getApplicationContext());
    }

    public static RequestQueue getmQueues() {
        return mQueues;
    }


}
