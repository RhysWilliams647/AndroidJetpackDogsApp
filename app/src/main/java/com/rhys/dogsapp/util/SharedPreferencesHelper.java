package com.rhys.dogsapp.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class SharedPreferencesHelper {

    private static final String PREF_TIME = "Pref Time";
    private static SharedPreferencesHelper instance;
    private SharedPreferences prefs;

    public static SharedPreferencesHelper getInstance(Context context){
        if(instance == null)
            instance = new SharedPreferencesHelper(context);
        return instance;
    }

    private SharedPreferencesHelper(Context context){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    //  Last time we retrieved data from the api
    public void saveUpdateTime(long time){
        prefs.edit().putLong(PREF_TIME, time).apply();
    }

    public Long getUpdateTime(){
        return prefs.getLong(PREF_TIME, 0);
    }
}
