package com.example.smscp;

import android.app.Application;

import cn.bmob.v3.Bmob;

/**
 * Created by 周旭 on 2017/1/14.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //第一：默认初始化
        Bmob.initialize(this, "eb501c214a09b1fef037a4d12335306a");
    }
}
