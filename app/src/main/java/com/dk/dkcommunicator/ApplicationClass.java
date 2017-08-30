package com.dk.dkcommunicator;

import android.app.Application;

public class ApplicationClass extends Application {

    private static ApplicationClass _instance;
    private SharedData _preferences;

    public static ApplicationClass get() {
        return _instance;
    }

    public SharedData getSharePref() {
        return _preferences;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        _instance = this;
        _preferences = new SharedData(this);
    }
}
