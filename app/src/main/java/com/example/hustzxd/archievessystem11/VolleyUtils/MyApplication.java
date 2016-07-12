package com.example.hustzxd.archievessystem11.VolleyUtils;

import android.app.Application;
import android.hardware.uhf.magic.DevBeep;
import android.hardware.uhf.magic.reader;

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

    static String C5U = "/dev/ttyMT1";
    static String C7DU = "/dev/ttyMT2";
    static String CM550 = "/dev/ttyMT2";
    static String CM398M = "/dev/ttyMSM0";

    @Override
    public void onCreate() {
        super.onCreate();
        mQueues = Volley.newRequestQueue(getApplicationContext());

        InitUHF();

    }

    public void InitUHF() {
        android.hardware.uhf.magic.reader.init(C5U);
        android.hardware.uhf.magic.reader.Open(C5U);
        reader.SetTransmissionPower(1950);
        DevBeep.init(MyApplication.this);
    }

    public static RequestQueue getmQueues() {
        return mQueues;
    }


}
