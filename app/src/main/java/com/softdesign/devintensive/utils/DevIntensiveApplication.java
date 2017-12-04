package com.softdesign.devintensive.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class DevIntensiveApplication extends Application
{
    private static SharedPreferences preferences;
    private static Context context;

    public static Context getContext()
    {
        return context;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        context = this;
    }

    public static SharedPreferences getSharedPreferencec()
    {
        return preferences;
    }
}
