package com.muv.technicalplan;
import android.content.Context;
import android.support.multidex.MultiDex;
import com.orm.SugarApp;

public class MyApplication extends SugarApp {
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}