package com.citrix.recentrics.activity;

import android.app.Application;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class BaseApplication extends Application {

    private static Bus bus;

    public static Bus getBus() {
        return bus;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bus = new Bus(ThreadEnforcer.MAIN);
    }
}
