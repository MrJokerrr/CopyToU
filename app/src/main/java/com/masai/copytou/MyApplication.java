package com.masai.copytou;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.View;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MyApplication extends Application {

    private static Context mContext;
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        mContext = getApplicationContext();
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public static Context getContext() {
        return mContext;
    }

    private List<Activity> list = new LinkedList<Activity>();

}
