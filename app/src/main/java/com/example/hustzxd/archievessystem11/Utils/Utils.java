package com.example.hustzxd.archievessystem11.Utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * 工具类
 * Created by buxiaoyao on 2016/7/8.
 */
public class Utils {
    public static final String TAG = "zxd";

    public static void toast(Context c, String s) {
        Toast.makeText(c, s, Toast.LENGTH_LONG).show();
    }

    public static void log(String tag, String s) {
        Log.d(TAG + tag, s);
    }
}
