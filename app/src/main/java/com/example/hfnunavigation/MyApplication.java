package com.example.hfnunavigation;

import android.app.Application;
import android.content.Context;
import com.example.hfnunavigation.util.LogUtil;

import org.litepal.LitePal;


public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        if (null == context) {
            context = getApplicationContext();
        }
        LitePal.initialize(this);
        LogUtil.setLevel(2);  //控制日志的打印，可在项目完成后不打印日志
    }

    public static Context getContext() {
        return context;
    }
}
